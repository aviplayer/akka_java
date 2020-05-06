package com.andrew.akka.app.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.andrew.akka.commands.FolderCommands;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
public class FolderActor extends AbstractActor {
    private final int id;
    private String name;
    private final ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public FolderActor(int id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = ZonedDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Object getDataByCondition(String condition) {
        if (name.toLowerCase().contains(condition.toLowerCase())) {
            return "Found by Condition: << " + this.toString() + " >>";
        } else {
            return new FolderCommands.ConditionNotMet("Folder with  id {" + id + "} has name {" + name + "}");
        }
    }

    @Override
    public String toString() {
        return "id:{" + id + "}, " + "name:{" + name + "}, " +
                "createdAt:{" + createdAt.toString() + "}, "
                + "createdAt:{" + modifiedAt.toString() + "}, ";
    }

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
                .match(FolderCommands.UpdateName.class, command -> {
                    setModifiedAt(ZonedDateTime.now());
                    setName(command.newName);
                    log.info("Folder updated: " + toString());
                })
                .match(FolderCommands.GetData.class, command ->
                        command.replyTo.tell(toString(), getSelf())
                )
                .match(FolderCommands.GetDataConditionally.class, command ->
                        command.replyTo.tell(getDataByCondition(command.condition), getSelf())
                )
                .match(FolderCommands.Delete.class, response -> {
                    log.info("Stopping Folder Actor");
                    getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
                })
                .matchAny(message ->
                        log.info(message.getClass().getName(), message.toString())
                )
                .build();
    }
}
