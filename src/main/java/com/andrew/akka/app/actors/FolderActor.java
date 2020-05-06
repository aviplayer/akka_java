package com.andrew.akka.app.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.andrew.akka.app.FolderDto;
import com.andrew.akka.commands.FolderCommands;

import java.time.ZonedDateTime;

public class FolderActor extends AbstractActor {

    FolderDto folder = new FolderDto("Actor Folder");
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final String printerPath = "akka://folder-system/user/printer-actor";


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
                .match(FolderCommands.UpdateName.class, response -> {
                    folder.setModifiedAt(ZonedDateTime.now());
                    folder.setName(response.name);
                    getContext().getSystem().actorSelection(printerPath).tell(response, getSelf());
                })
                .match(FolderCommands.GetData.class, response -> {
                    getSender().tell(folder, getSelf());
                })
                .match(FolderCommands.GetDataConditionally.class, response -> {
                    try {
                        getSender().tell(folder.getDataByCondition(response.condition), getSelf());
                    } catch(NoSuchFieldException e){
                         getSender().tell(new FolderCommands.ConditionNotMet(e.getMessage()), getSelf());
                    }
                    getSender().tell(response, getSelf());
                    
                })
                .match(FolderCommands.Delete.class, response -> {
                    getContext().getSystem().actorSelection(printerPath).tell(new FolderCommands.Delete(), ActorRef.noSender());
                    getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
                })
                .matchAny(message -> {
                    getSender().tell(message, getSelf());
                })
                .build();
    }
}
