package com.andrew.akka.app.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderCollectionMessages;
import com.andrew.akka.commands.FolderMessages;
import com.andrew.akka.commands.PrinterMessages;
import org.slf4j.Logger;

public class PrinterActor extends AbstractBehavior<PrinterMessages> {
    private Logger log = getContext().getLog();

    public PrinterActor(ActorContext<PrinterMessages> context) {
        super(context);
        log.info("Printer Actor created!");
    }

    private Behavior<PrinterMessages> postStop() {
        log.info("Printer Actor stopped");
        return this;
    }

    @Override
    public Receive<PrinterMessages> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> postStop())
                .onMessage(FolderMessages.ConditionNotMet.class, message -> {
                    log.info("Folder doesn't met condition: {}", message.getCondition());
                    return this;
                })
                .onMessage(FolderMessages.FolderData.class, message -> {
                    log.info("Folder with id {} has name {}", message.getId(), message.getName());
                    return this;
                })
                .onMessage(PrinterMessages.Stop.class, command -> {
                    log.info("Stopping Printer Actor");
                    return Behaviors.stopped(() -> getContext().getSystem().log().info("Stopping Printer Actor!"));
                })
                .onMessage(PrinterMessages.SimpleMessage.class, message -> {
                    log.info(message.getData());
                    return this;
                })
                .onMessage(FolderCollectionMessages.FoldersDataWithCondition.class, message -> {
                    var folders = message.getFolders();
                    var condition = message.getCondition();
                    log.info("\nFolders with condition {} : {}\n", condition, folders);
                    return this;
                })
                .onMessage(FolderCollectionMessages.FoldersData.class, message -> {
                    var folders = message.getFolders();
                    log.info("Folders are : \n{}\n", folders);
                    return this;
                })
                .onAnyMessage(command -> {
                    log.info("Logging {} {} ", command.getClass().getName(), command);
                    return this;
                })
                .build();
    }

    public static Behavior<PrinterMessages> create() {
        return Behaviors.setup(PrinterActor::new);
    }
}
