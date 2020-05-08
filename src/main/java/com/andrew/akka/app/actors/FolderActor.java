package com.andrew.akka.app.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderMessages;
import org.slf4j.Logger;

import java.time.ZonedDateTime;

public class FolderActor extends AbstractBehavior<FolderMessages.FolderMessage> {
    private int id;
    private String name;
    private final ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;
    private Logger log = getContext().getLog();

    static Behavior<FolderMessages.FolderMessage> create(int id, String name) {
        return Behaviors.setup(context -> new FolderActor(context, id, name));
    }

    private FolderActor(ActorContext<FolderMessages.FolderMessage> context, int id, String name) {
        super(context);
        this.id = id;
        this.name = name;
        this.createdAt = ZonedDateTime.now();
        this.modifiedAt = this.createdAt;
    }


    private Behavior<FolderMessages.FolderMessage> postStop() {
        log.info("Folder Actor stopped");
        return this;
    }


    @Override
    public Receive<FolderMessages.FolderMessage> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> postStop())
                .onMessage(FolderMessages.UpdateName.class, command -> {
                    modifiedAt = ZonedDateTime.now();
                    name = command.newName;
                    log.info("Folder updated: " + toString());
                    return this;
                })
                .onMessage(FolderMessages.GetData.class, command -> {
                    command.replyTo.tell(new FolderMessages.FolderData(id, name, createdAt, modifiedAt));
                    return this;
                })
                .onMessage(FolderMessages.GetDataConditionally.class, command -> {
                    if (name.toLowerCase().contains(command.getCondition().toLowerCase())) {
                        command.getReplyTo().tell(new FolderMessages.FolderData(id, name, createdAt, modifiedAt));
                    } else {

                        command.getReplyTo().tell(new FolderMessages.ConditionNotMet(command.getCondition()));
                    }
                    return this;
                })
                .onMessage(FolderMessages.Delete.class, command -> {
                    log.info("Stopping Folder Actor");
                    command.getReplyTo().tell(new FolderMessages.Stopped());
                    return Behaviors.stopped(() -> getContext().getSystem().log().info("Stopping Folder Actor!"));
                })
                .onAnyMessage(command -> {
                    log.info(command.toString());
                    return this;
                })
                .build();
    }
}
