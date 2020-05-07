package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderCommands;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;

import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = false)
public class FolderActor extends AbstractBehavior<FolderCommands.FolderCommand> {
    private final int id;
    private String name;
    private final ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;
    Logger log = getContext().getLog();
    ActorRef printer;

    static Behavior<FolderCommands.FolderCommand> create() {
        return Behaviors.setup(FolderActor::new);
    }

    private FolderActor(ActorContext<FolderCommands.FolderCommand> context) {
        super(context);
        this.id = 1;
        this.name = "First Name";
        this.createdAt = ZonedDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    private FolderCommands.FolderCommand getDataByCondition(String condition) {
        if (name.toLowerCase().contains(condition.toLowerCase())) {
            return new FolderCommands.GetFolder(id, name, createdAt, modifiedAt);
        } else {
            return new FolderCommands.ConditionNotMet("Folder with  id {" + id + "} has name {" + name + "}");
        }
    }


    private Behavior<FolderCommands.FolderCommand> postStop() {
        log.info("Folder Actor stopped");
        return this;
    }


    @Override
    public Receive<FolderCommands.FolderCommand> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> postStop())
                .onMessage(FolderCommands.UpdateName.class, command -> {
                    modifiedAt = ZonedDateTime.now();
                    name = command.newName;
                    log.info("Folder updated: " + toString());
                    return this;
                })
                .onMessage(FolderCommands.GetData.class, command -> {
                    command.replyTo.tell(new FolderCommands.GetFolder(id, name, createdAt, modifiedAt));
                    return this;
                })
                .onMessage(FolderCommands.GetDataConditionally.class, command -> {
                    command.replyTo.tell(new FolderCommands.Folder(getDataByCondition(command.condition)));
                    return this;
                })
                .onMessage(FolderCommands.Delete.class, command -> {
                    log.info("Stopping Folder Actor");
                    command.replyTo.tell(new FolderCommands.Stop());
                    return Behaviors.stopped(() -> getContext().getSystem().log().info("Stopping Folder Actor!"));
                })
                .onAnyMessage(command -> {
                    log.info(command.toString());
                    return this;
                })
                .build();
    }
}
