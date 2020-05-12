package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import com.andrew.akka.commands.FolderCollectionMessages.*;
import com.andrew.akka.commands.FolderCollectionMessages;

public class RootBehavior {

    public static Behavior<FolderCollectionMessages> create() {
        return Behaviors.setup(RootBehavior::apply);
    }

    private static Behavior<FolderCollectionMessages> apply(ActorContext<FolderCollectionMessages> context) {

        ActorRef foldersCollection = context.spawn(FolderCollectionActor.create(PersistenceId.ofUniqueId("folders-collection")), "folders-collection-name");
        ActorRef printer = context.spawn(PrinterActor.create(), "Printer");
        for (var i = 0; i < 5; i++) {
           foldersCollection.tell(new FolderCollectionMessages.CreateFolder("Folder"));
        }

        foldersCollection.tell(new DeleteFolderById(1));
        foldersCollection.tell(new GetFolderById(1, printer));
        foldersCollection.tell(new UpdateFolderById(0, "New name"));
        foldersCollection.tell(new UpdateFolderById(4, "New name 1"));
        foldersCollection.tell(new GetFolderById(4, printer));
        foldersCollection.tell(new GetFoldersConditionally(printer, "New name"));
        foldersCollection.tell(new GetFoldersConditionally(printer, "fake"));
        foldersCollection.tell(new GetAllFolders(printer));
        
        return Behaviors.receive(
                (context_new, message) ->
                    Behaviors.same(),
                (context_new, signal) -> {
                    if (signal instanceof PostStop) {
                        context.getLog().info("Root behaviour stopped");
                    }
                    return Behaviors.same();
                }
        );
    }

}
