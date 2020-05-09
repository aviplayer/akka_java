package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderMessages;

public class RootBehavior {


    public static Behavior<FolderCollectionMessages> create() {
        return Behaviors.setup(RootBehavior::apply);
    }


    private static Behavior<FolderCollectionMessages> apply(ActorContext<FolderCollectionMessages> context) {

        ActorRef foldersCollection;
        foldersCollection = context.spawn(FolderCollectionActor.create(), "folders-collection");

        for (var i = 0; i < 10; i++) {
            foldersCollection.tell(new FolderCollectionMessages.CreateFolder("Folder"));
        }

        foldersCollection.tell(new FolderCollectionMessages.DeleteFolderById(1));
        foldersCollection.tell(new FolderCollectionMessages.GetFolderById(1, context.getSelf()));
        foldersCollection.tell(new FolderCollectionMessages.UpdateFolderById(4, "New name"));
        foldersCollection.tell(new FolderCollectionMessages.UpdateFolderById(5, "New name 1"));
        foldersCollection.tell(new FolderCollectionMessages.GetFolderById(4, context.getSelf()));
        foldersCollection.tell(new FolderCollectionMessages.GetFoldersConditionally(context.getSelf(), "New name"));
        foldersCollection.tell(new FolderCollectionMessages.GetFoldersConditionally(context.getSelf(), "fake"));
        foldersCollection.tell(new FolderCollectionMessages.GetAllFolders(context.getSelf()));
        return Behaviors.receive(
                (context_new, message) -> {
                    if (message instanceof FolderMessages.FolderData) {
                        context.getLog().info("Folder with id {} has name {}\n", ((FolderMessages.FolderData) message).getId(), ((FolderMessages.FolderData) message).getName());
                    } else if (message instanceof FolderMessages.ConditionNotMet) {
                        context.getLog().info(((FolderMessages.ConditionNotMet) message).getCondition());
                    } else if (message instanceof FolderCollectionMessages.FoldersDataWithCondition) {
                        var folders = ((FolderCollectionMessages.FoldersDataWithCondition) message).getFolders();
                        var condition = ((FolderCollectionMessages.FoldersDataWithCondition) message).getCondition();
                        context.getLog().info("Folders with condition {} : {}\n", condition, folders);
                    } else if (message instanceof FolderCollectionMessages.FoldersData) {
                        var folders = ((FolderCollectionMessages.FoldersData) message).getFolders();
                        context.getLog().info("Folders are : \n{}\n", folders);
                    }
                    return Behaviors.same();
                },
                (context_new, signal) -> {
                    if (signal instanceof PostStop) {
                        context.getLog().info("Root behaviour stopped");
                    }
                    return Behaviors.same();
                }
        );
    }

}
