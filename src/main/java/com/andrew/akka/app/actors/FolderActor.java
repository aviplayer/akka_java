package com.andrew.akka.app.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.*;
import com.andrew.akka.app.states.FolderEmptyState;
import com.andrew.akka.app.states.FolderRealState;
import com.andrew.akka.app.states.FolderState;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderEvent;
import com.andrew.akka.commands.FolderEvent.*;
import com.andrew.akka.commands.FolderMessages;
import com.andrew.akka.commands.FolderMessages.FolderData;
import com.andrew.akka.commands.FolderMessages.FolderMessage;
import com.andrew.akka.commands.FolderMessages.GetData;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.ZonedDateTime;

public class FolderActor extends EventSourcedBehavior<FolderMessage, FolderEvent, FolderState> {
    ActorContext<FolderMessage> context;
    private Logger log;
    private int id;
    private String name;
    private final ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public static Behavior<FolderMessage> create(int id, String name) {
        PersistenceId persistenceId = PersistenceId.ofUniqueId("Folder" + id);
        return Behaviors.setup(ctx -> new FolderActor(persistenceId, ctx, id, name));
    }

    private FolderActor(PersistenceId persistenceId, ActorContext<FolderMessage> ctx, int id, String name) {
        super(persistenceId);
        SupervisorStrategy.restartWithBackoff(Duration.ofMillis(200), Duration.ofSeconds(5), 0.1);
        context = ctx;
        log = ctx.getLog();
        this.id = id;
        this.name = name;
        this.createdAt = ZonedDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    @Override
    public SignalHandler<FolderState> signalHandler() {
        return newSignalHandlerBuilder()
                .onSignal(
                        PostStop.class,
                        (state, completed) -> {
                            postStop();
                        })
                .build();
    }

    private Behavior<FolderMessage> postStop() {
        log.info("\nFolder Actor {} stopped\n", id);
        return this;
    }

    @Override
    public FolderState emptyState() {
        return new FolderEmptyState();
    }

    @Override
    public EventHandler<FolderState, FolderEvent> eventHandler() {
        var builder = newEventHandlerBuilder();
        builder.forStateType(FolderEmptyState.class)
                .onEvent(FolderCreated.class, (state, event) -> {
                    log.info("\nFolder Created {}\n", event.toString());
                    return new FolderRealState(event.getId(), event.getName(), event.getCreatedAt(), event.getModifiedAt());
                });

        builder.forStateType(FolderRealState.class)
                .onEvent(FolderUpdated.class, (state, event) -> {
                    log.info("\nFolder Updated {}\n", event.toString());
                    state.setName(event.getName());
                    state.setModifiedAt(event.getModifiedAt());
                    return state;
                })
                .onEvent(FolderDeleted.class, (state, event) -> {
                    Behaviors.stopped(() -> log.info("\nStopping Folder Actor! {}\n", id));
                    return new FolderEmptyState();
                });


        return builder.build();
    }

    @Override
    public CommandHandler commandHandler() {
        CommandHandlerBuilder<FolderMessage, FolderEvent, FolderState> builder = newCommandHandlerBuilder();

        builder.forStateType(FolderEmptyState.class)
                .onCommand((GetData.class), (folderRealState, command) -> {
                    command.replyTo.tell(new FolderData(id, name, createdAt, modifiedAt));
                    return Effect().persist(new FolderCreated(id, name, createdAt, modifiedAt));
                });

        builder.forStateType(FolderRealState.class)
                .onCommand((GetData.class), (folderRealState, command) -> {
                    command.replyTo.tell(new FolderData(id, folderRealState.getName(), createdAt, modifiedAt));
                    return Effect().none();
                })
                .onCommand(FolderMessages.UpdateName.class, (folderRealState, command) -> {
                            String name = command.getNewName();
                            ZonedDateTime modifiedAt = ZonedDateTime.now();
                            return Effect().persist(new FolderUpdated(name, modifiedAt));
                        }
                )
                .onCommand(FolderMessages.GetDataConditionally.class, (folderRealState, command) -> {
                    if (name.toLowerCase().contains(command.getCondition().toLowerCase())) {
                        command.getReplyTo().tell(new FolderMessages.FolderData(id, name, createdAt, modifiedAt));
                    } else {

                        command.getReplyTo().tell(new FolderMessages.ConditionNotMet(command.getCondition()));
                    }
                    return Effect().none();
                })
                .onCommand(FolderMessages.Delete.class, (folderRealState, command) -> {
                    command.getReplyTo().tell(new FolderCollectionMessages.FolderDelete(id));
                    return Effect().persist(new FolderDeleted()).
                            thenStop();
                });

        return builder.build();
    }
}
