package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderMessages;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;

public class FolderCollectionActor extends AbstractBehavior<FolderCollectionMessages> {
    private Logger log = getContext().getLog();
    private Map<Integer, ActorRef> folders;
    private final String actorNamePrefix = "folder-";
    private int lastId;

    static Behavior<FolderCollectionMessages> create() {
        return Behaviors.setup(FolderCollectionActor::new);
    }

    private FolderCollectionActor(ActorContext<FolderCollectionMessages> context) {
        super(context);
        folders = new HashMap<>();
        lastId = 0;
    }

    private Behavior<FolderCollectionMessages> postStop() {
        log.info("Folder Collection Actor stopped");
        return this;
    }

    @Override
    public Receive<FolderCollectionMessages> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> postStop())
                .onMessage(FolderCollectionMessages.CreateFolder.class, command -> {
                    int id = lastId;
                    String name = command.getName();
                    String actorName = actorNamePrefix + lastId;
                    ActorRef folder = getContext()
                            .spawn(FolderActor.create(id, name), actorName);
                    folders.put(id, folder);
                    log.info("Folder {} with id {} created by path {}", name, id, folder.path());
                    lastId++;
                    return this;
                })
                .onMessage(FolderCollectionMessages.UpdateFolderById.class, command -> {
                    int id = command.getId();
                    if(folders.containsKey(id)) {
                        ActorRef folderActor = folders.get(id);
                        folderActor.tell(new FolderMessages.UpdateName(command.getName()));
                    }
                    return this;
                })
                .onMessage(FolderCollectionMessages.GetAllFolders.class, message -> {
                    getContext().spawn(FoldersAggregator
                            .folderAggregator(folders, message.getReplyTo()), "folders-aggregator");
                    return this;
                })
                .onMessage(FolderCollectionMessages.FoldersData.class, response -> {
                    log.info("Get folders data response {} ", response.getFolders());
                    return this;
                })
                .build();
    }
}

