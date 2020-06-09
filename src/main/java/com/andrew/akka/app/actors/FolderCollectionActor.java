package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.RecoveryCompleted;
import akka.persistence.typed.javadsl.*;
import com.andrew.akka.app.states.CollectionState;
import com.andrew.akka.app.states.EmptyCollectionState;
import com.andrew.akka.app.states.RealCollectionState;
import com.andrew.akka.commands.FolderCollectionEvent;
import com.andrew.akka.commands.FolderCollectionEvent.*;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderCollectionMessages.*;
import com.andrew.akka.commands.FolderMessages;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class FolderCollectionActor extends EventSourcedBehavior<FolderCollectionMessages,
        FolderCollectionEvent,
        CollectionState> {

    private final ActorContext<FolderCollectionMessages> context;

    private int lastId = 0;
    private Logger log;
    private final String actorNamePrefix = "folder-";
    private Map<Integer, ActorRef> folders;

    public static Behavior<FolderCollectionMessages> create(PersistenceId persistenceId) {
        return Behaviors.setup(ctx -> new FolderCollectionActor(persistenceId, ctx));
    }

    private FolderCollectionActor(PersistenceId persistenceId, ActorContext<FolderCollectionMessages> ctx) {
        super(persistenceId);
        SupervisorStrategy.restartWithBackoff(Duration.ofMillis(200), Duration.ofSeconds(5), 0.1);
        this.context = ctx;
        log = ctx.getLog();
        folders = new HashMap<>();
    }


    @Override
    public RetentionCriteria retentionCriteria() {
        return RetentionCriteria.snapshotEvery(100, 3);
    }

    @Override
    public RealCollectionState emptyState() {
        return new EmptyCollectionState();
    }

    private CollectionState createFolder(String name, RealCollectionState state) {
        int id = lastId;
        String actorName = actorNamePrefix + id;
        ActorRef folder = context
                .spawn(FolderActor.create(id, name), actorName);
        folders.put(id, folder);
        state.getState().put(id, folder);
        folder.tell(new FolderMessages.GetData(context.getSelf()));
        lastId++;
        return state;
    }

    @Override
    public SignalHandler<CollectionState> signalHandler() {
        return newSignalHandlerBuilder()
                .onSignal(
                        PostStop.class,
                        (state, completed) ->
                                postStop()
                )
                .onSignal(
                        RecoveryCompleted.instance(),
                        state -> {
                            log.info("TODO: add some end-of-recovery side-effect here");
                        })
                .build();
    }

    private Behavior<FolderCollectionMessages> postStop() {
        log.info("\nCollection Actor stopped\n");
        return this;
    }

    @Override
    public EventHandler<CollectionState, FolderCollectionEvent> eventHandler() {

        var builder = newEventHandlerBuilder();

        builder.forStateType(EmptyCollectionState.class)
                .onEvent(FolderShouldAdded.class, (realCollectionState, event) -> {
                    lastId = 0;
                    String name = event.getName();
                    RealCollectionState state = new RealCollectionState();
                    return createFolder(name, state);
                });

        builder.forStateType(RealCollectionState.class)
                .onEvent(FolderShouldAdded.class, (realCollectionState, event) -> {
                    String name = event.getName();
                    RealCollectionState state = new RealCollectionState();
                    return createFolder(name, state);
                })
                .onEvent(FolderShouldDeleted.class, (realCollectionState, event) -> {
                    int id = event.getId();
                    if (folders.containsKey(id)) {
                        ActorRef folderActor = folders.get(id);
                        folders.remove(id);
                        folderActor.tell(new FolderMessages.Delete(context.getSelf()));
                        realCollectionState.getState().remove(id);
                    } else {
                        log.error("Folder with id {} doesn't exist's! ", id);
                    }
                    return realCollectionState;
                });

        return builder.build();
    }

    @Override
    public CommandHandler<FolderCollectionMessages, FolderCollectionEvent, CollectionState> commandHandler() {
        CommandHandlerBuilder<FolderCollectionMessages, FolderCollectionEvent, CollectionState> builder =
                newCommandHandlerBuilder();

        builder.forStateType(EmptyCollectionState.class)
                .onCommand(CreateFolder.class, (emptyCollectionState, command) ->
                        Effect().persist(new FolderShouldAdded(command.getName()))
                )
                .onAnyCommand((emptyCollectionState, command) -> {
                    log.info("\nEmpty state for unhandled command: {}\n", command);
                    return Effect().none();
                });

        builder.forState(realCollectionState ->
                !((RealCollectionState) realCollectionState).isCheckedout()
        )
                .onCommand(CreateFolder.class, (realCollectionState, command) ->
                        Effect().persist(new FolderShouldAdded(command.getName()))
                )
                .onCommand(UpdateFolderById.class, (realCollectionState, command) -> {
                    int id = command.getId();
                    String newName = command.getName();
                    if (folders.containsKey(id)) {
                        ActorRef folderActor = folders.get(id);
                        folderActor.tell(new FolderMessages.UpdateName(newName));
                    } else {
                        log.error("Folder with id {} doesn't exist's! ", id);
                    }
                    return Effect().none();
                })
                .onCommand(GetAllFolders.class, message -> {
                    context.spawn(FoldersAggregator
                            .folderAggregator(folders, message.getReplyTo()), "folders-aggregator");
                    return Effect().none();
                })
                .onCommand(GetFoldersConditionally.class, message -> {
                    context.spawn(FoldersAggregator
                            .folderAggregator(folders, message.getReplyTo(), message.getCondition()), "folders-aggregator"+ UUID.randomUUID());
                    return Effect().none();
                })
                .onCommand(FolderMessages.FolderData.class, message -> {
                    log.info("Folder created {} ", message.getId());
                    return Effect().none();
                })
                .onCommand(GetFolderById.class, message -> {
                    ActorRef replyTo = message.getReplyTo();
                    int id = message.getId();
                    if (folders.containsKey(id)) {
                        folders.get(id).tell(new FolderMessages.GetData(replyTo));
                    }else {
                        replyTo.tell(new FolderMessages.ConditionNotMet("Invalid id " + id));
                    }
                    return Effect().none();
                })
                .onCommand(DeleteFolderById.class, command -> {
                    int id = command.getId();
                    return Effect().persist(new FolderShouldDeleted(id));
                })
                .onCommand(FolderDelete.class, command -> {
                   log.info("Folder {} has been deleted!", command.getId());
                   return Effect().none();
               });

        return builder.build();
    }
}

