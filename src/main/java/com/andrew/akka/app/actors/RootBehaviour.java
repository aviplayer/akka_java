package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderCommands;
import org.slf4j.Logger;

public class RootBehaviour extends AbstractBehavior<FolderCommands.Stop> {

    private Logger log = getContext().getLog();

    public static Behavior<FolderCommands.Stop> create() {
        return Behaviors.setup(RootBehaviour::apply);
    }

    RootBehaviour(ActorContext<FolderCommands.Stop> context) {
        super(context);
    }

    private static Behavior<FolderCommands.Stop> apply(ActorContext<FolderCommands.Stop> context) {
        ActorRef printer, folder;
        printer = context.spawn(PrinterActor.create(), "Printer");
        folder = context.spawn(FolderActor.create(), "Folder");

        folder.tell(new FolderCommands.GetData(printer));
        folder.tell(new FolderCommands.UpdateName("Akka"));
        folder.tell(new FolderCommands.GetData(printer));
        folder.tell(new FolderCommands.GetDataConditionally("akk", printer));
        folder.tell(new FolderCommands.GetDataConditionally("no_data", printer));
        folder.tell(new FolderCommands.Delete(printer));

        return Behaviors.same();
    }


    private Behavior<FolderCommands.Stop> postStop(FolderCommands.Stop stop) {
        log.info("Folder Actor stopped");
        return this;
    }


    @Override
    public Receive<FolderCommands.Stop> createReceive() {
        return newReceiveBuilder().onMessage(FolderCommands.Stop.class, this::postStop).build();
    }
}
