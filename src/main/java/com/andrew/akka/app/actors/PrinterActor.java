package com.andrew.akka.app.actors;

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
                .matchAny(message -> {
                    log.info("Logging  {}! ", message);
                })
                .match(FolderCommands.ConditionNotMet.class, conditionNotMet -> {
                    log.error("conditionNotMet Exception: ", conditionNotMet.message);
                })
                .match(FolderCommands.Delete.class, s -> {
                    getContext().stop(getSelf());
                })
                .build();
    }
}
