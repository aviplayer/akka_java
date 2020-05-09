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

    public static Behavior<FolderMessages.FolderAggregatorMessage> folderAggregator(Map<Integer, ActorRef> folders, ActorRef replyTo) {
        return Behaviors.setup(context -> {
            folders.values().forEach(folder -> folder.tell(new FolderMessages.GetData(context.getSelf())));
            return folderAggregatorCore(folders, replyTo, new ArrayList<>());
        });
    }

    public static Behavior<FolderMessages.FolderAggregatorMessage> folderAggregator(Map<Integer, ActorRef> folders, ActorRef replyTo, String condition) {
        return Behaviors.setup(context -> {
            folders.values().forEach(folder -> folder.tell(new FolderMessages.GetDataConditionally(condition, context.getSelf())));
            return folderAggregatorWithCondition(folders, replyTo, new ArrayList<>(), condition, new ArrayList<>());
        });
    }

    private static Behavior<FolderMessages.FolderAggregatorMessage> folderAggregatorCore(
            Map<Integer, ActorRef> folders,
            ActorRef replyTo,
            List<FolderMessages.FolderData> responses) {
        if (responses.size() == folders.size()) {
            replyTo.tell(new FolderCollectionMessages.FoldersData(responses));
            return Behaviors.stopped();
        }
        return Behaviors.receive(
                (context, message) -> {
                    responses.add((FolderMessages.FolderData) message);
                    return folderAggregatorCore(folders, replyTo, responses);
                },
                (context, signal) -> {
                    if (signal instanceof PostStop) {
                        context.getLog().info("Folder Aggregator stopped");
                    }
                    return Behaviors.same();
                });
    }

    private static Behavior<FolderMessages.FolderAggregatorMessage> folderAggregatorWithCondition(
            Map<Integer, ActorRef> folders,
            ActorRef replyTo,
            List<FolderMessages.FolderData> responses,
            String condition,
            ArrayList<Integer> counter) {
        if (counter.size() == folders.size()) {
            replyTo.tell(new FolderCollectionMessages.FoldersDataWithCondition(responses, condition));
            return Behaviors.stopped();
        }
        counter.add(1);
        return Behaviors.receive(
                (context, message) -> {
                    if (message instanceof FolderMessages.FolderData) {
                        responses.add((FolderMessages.FolderData) message);
                    }
                    return folderAggregatorWithCondition(folders, replyTo, responses, condition, counter);
                },
                (context, signal) -> {
                    if (signal instanceof PostStop) {
                        context.getLog().info("Folder Aggregator stopped");
                    }
                    return Behaviors.same();
                });
    }

}
