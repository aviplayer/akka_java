package com.andrew;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.andrew.akka.app.actors.FolderActor;
import com.andrew.akka.app.actors.PrinterActor;
import com.andrew.akka.commands.FolderCommands;

public class App {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("folder-system");

        ActorRef folderActor = system.actorOf(Props.create(FolderActor.class, 1, "Folder Actor"), "folder-actor");
        ActorRef printerActor = system.actorOf(Props.create(PrinterActor.class), "printer-actor");

        folderActor.tell(new FolderCommands.GetData(printerActor), ActorRef.noSender());

        folderActor.tell(new FolderCommands.UpdateName("Akka"), ActorRef.noSender());

        folderActor.tell(new FolderCommands.GetData(printerActor), ActorRef.noSender());

        folderActor.tell(new FolderCommands.GetDataConditionally("akk", printerActor), ActorRef.noSender());

        folderActor.tell(new FolderCommands.GetDataConditionally("no_data", printerActor), ActorRef.noSender());

        folderActor.tell(new FolderCommands.Delete(), ActorRef.noSender());
        printerActor.tell(new FolderCommands.Delete(), ActorRef.noSender());
    }
}
