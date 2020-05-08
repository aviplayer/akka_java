package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderMessages;
import org.slf4j.Logger;

public class RootBehavior extends AbstractBehavior<FolderMessages.FolderMessage> {

    private Logger log = getContext().getLog();

    public static Behavior<FolderMessages.FolderMessage> create() {
        return Behaviors.setup(RootBehavior::apply);
    }

    RootBehavior(ActorContext<FolderMessages.FolderMessage> context) { super(context); }

    private static Behavior<FolderMessages.FolderMessage> apply(ActorContext<FolderMessages.FolderMessage> context) {
        ActorRef foldersCollection;
        foldersCollection = context.spawn(FolderCollectionActor.create(), "folders-collection");

        for (var i = 0; i < 10; i++) {
            foldersCollection.tell(new FolderCollectionMessages.CreateFolder("Folder"));
        }

        foldersCollection.tell(new FolderCollectionMessages.GetAllFolders(foldersCollection));
        return Behaviors.same();
    }

    private Behavior<FolderMessages.FolderMessage> postStop(FolderMessages.Stop stop) {
        log.info("Folder Actor stopped");
        return this;
    }


    @Override
    public Receive<FolderMessages.FolderMessage> createReceive() {
        return newReceiveBuilder().onMessage(FolderMessages.Stop.class, this::postStop).build();
    }
}
