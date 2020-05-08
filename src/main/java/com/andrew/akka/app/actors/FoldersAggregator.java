package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.Behaviors;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FoldersAggregator {
    public static Behavior<FolderMessages.FolderData> folderAggregator(Map<Integer, ActorRef> folders, ActorRef replyTo) {
        return Behaviors.setup(context -> {
            folders.values().forEach(folder -> folder.tell(new FolderMessages.GetData(context.getSelf())));
            return folderAggregatorCore(folders, replyTo, new ArrayList<>());
        });
    }

    private static Behavior<FolderMessages.FolderData> folderAggregatorCore(
            Map<Integer, ActorRef> folders,
            ActorRef replyTo,
            List<FolderMessages.FolderData> responses) {
        if (responses.size() == folders.size()) {
            replyTo.tell(new FolderCollectionMessages.FoldersData(responses));
            return Behaviors.stopped();
        }
        return Behaviors.receive(
                (context, message) -> {
                    responses.add(message);
                    return folderAggregatorCore(folders, replyTo, responses);
                },
                (context, signal) -> {
                    if (signal instanceof PostStop) {
                        context.getLog().info("Folder Aggregator stopped");
                    }
                    return Behaviors.same();
                });
    }

}
