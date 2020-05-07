package com.andrew.akka.app.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderCommands;
import org.slf4j.Logger;

public class PrinterActor extends AbstractBehavior<FolderCommands.FolderCommand> {
    private Logger log = getContext().getLog();

    static Behavior<FolderCommands.FolderCommand> create() {
        return Behaviors.setup(PrinterActor::new);
    }

    private PrinterActor(ActorContext<FolderCommands.FolderCommand> context) {
        super(context);
        log.info("Printer Actor created!");
    }

    private Behavior<FolderCommands.FolderCommand> postStop() {
        log.info("Printer Actor stopped");
        return this;
    }

    @Override
    public Receive<FolderCommands.FolderCommand> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> postStop())
                .onMessage(FolderCommands.ConditionNotMet.class, command -> {
                    log.info(command.message);
                    return this;
                })
                .onMessage(FolderCommands.Stop.class, command -> {
                    log.info("Stopping Printer Actor");
                    return Behaviors.stopped(() -> getContext().getSystem().log().info("Stopping Printer Actor!"));
                })
                .onAnyMessage(command -> {
                    log.info("Logging  {}! " + command.getClass().getName() + " " + command);
                    return this;
                })
                .build();
    }
}
