package com.andrew.akka.app;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.andrew.akka.commands.FolderCommands;

public class PrinterActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public void preStart() {
        log.info("Printer started");
    }

    @Override
    public void postStop() {
        log.info("Printer stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FolderCommands.CreateFolder.class, response -> {
                    log.info("Create folder: ", response.name);
                })
                .match(FolderCommands.UpdateName.class, response -> {
                    log.info("Update folder: ", response.name);
                })
                .match(FolderCommands.GetData.class, response -> {
                    log.info("Update folder: ", response.replyTo.path());
                })
                .matchAny(s -> {
                    log.info("Logging  {}!", s);
                })
                .matchEquals("stop", s -> {
                    getContext().stop(getSelf());
                })
                .build();
    }
}
