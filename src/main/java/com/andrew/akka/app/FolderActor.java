package com.andrew.akka.app;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.andrew.akka.commands.FolderCommands;

import java.util.ArrayList;

public class FolderActor extends AbstractActor {

    FolderCollection folderCollection = new FolderCollection(new ArrayList<>());
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    @Override
    public void preStart() {
        log.info("Folder Actor started");
    }

    @Override
    public void postStop() {
        log.info("Folder Actor stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("stop", s -> {
                    getContext().stop(getSelf());
                })
                .match(FolderCommands.CreateFolder.class, response -> {
                    folderCollection.createFolder(response.name);
                    getSender().tell(response, getSelf());
                })
                .match(FolderCommands.UpdateName.class, response -> {
                    folderCollection.updateFolder(response.id, response.name);
                    getSender().tell(response, getSelf());
                })
                .match(FolderCommands.GetData.class, response -> {
                    folderCollection.getData(response.replyTo);
                    getSender().tell(response, getSelf());
                })
                .match(FolderCommands.GetDataConditionally.class, response -> {
                    folderCollection.getDataByCondition(response.replyTo, response.condition);
                    getSender().tell(response, getSelf());
                })
                .matchAny(message -> {
                    getSender().tell(message, getSelf());
                })
                .build();
    }
}
