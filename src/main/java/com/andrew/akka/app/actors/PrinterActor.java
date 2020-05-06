package com.andrew.akka.app.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
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
                .match(FolderCommands.ConditionNotMet.class, command -> {
                    log.info(command.message);
                })
                .match(FolderCommands.Delete.class, command -> {
                    log.info("Stopping Printer Actor");
                    getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
                })
                .matchAny(command -> {
                    log.info("Logging  {}! " + command.getClass().getName() + " " + command);
                })
                .build();
    }
}
