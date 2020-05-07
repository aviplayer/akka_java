package com.andrew.akka.app.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.andrew.akka.commands.FolderMessages;
import com.andrew.akka.commands.PrinterMessages;
import org.slf4j.Logger;

public class RootBehavior extends AbstractBehavior<FolderMessages.Stop> {

    private Logger log = getContext().getLog();

    public static Behavior<FolderMessages.Stop> create() {
        return Behaviors.setup(RootBehavior::apply);
    }

    RootBehavior(ActorContext<FolderMessages.Stop> context) {
        super(context);
    }

    private static Behavior<FolderMessages.Stop> apply(ActorContext<FolderMessages.Stop> context) {
        ActorRef printer, folder;
        printer = context.spawn(PrinterActor.create(), "Printer");
        folder = context.spawn(FolderActor.create(), "Folder");

        folder.tell(new FolderMessages.GetData(printer));
        folder.tell(new FolderMessages.UpdateName("Akka"));
        folder.tell(new FolderMessages.GetData(printer));
        folder.tell(new FolderMessages.GetDataConditionally("akk", printer));
        folder.tell(new FolderMessages.GetDataConditionally("no_data", printer));

        var printerStopper = context.spawn(Behaviors.<FolderMessages.Stopped>receive((ctx, message) -> {
            printer.tell(new PrinterMessages.Stop());
            return Behaviors.stopped();
        }), "stopper");

        folder.tell(new FolderMessages.Delete(printerStopper));

        return Behaviors.same();
    }


    private Behavior<FolderMessages.Stop> postStop(FolderMessages.Stop stop) {
        log.info("Folder Actor stopped");
        return this;
    }


    @Override
    public Receive<FolderMessages.Stop> createReceive() {
        return newReceiveBuilder().onMessage(FolderMessages.Stop.class, this::postStop).build();
    }
}
