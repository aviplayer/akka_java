package com.andrew;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.andrew.akka.app.FolderActor;
import com.andrew.akka.app.PrinterActor;
import com.andrew.akka.commands.FolderCommands;

public class App {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("folder-system");

        ActorRef folderActor = system.actorOf(Props.create(FolderActor.class), "folder-actor");
        ActorRef printerActor = system.actorOf(Props.create(PrinterActor.class), "printer-actor");

        folderActor.tell(new FolderCommands.CreateFolder("Folder1"), printerActor);
        folderActor.tell(new FolderCommands.CreateFolder("Folder2"), printerActor);
        folderActor.tell(new FolderCommands.CreateFolder("Folder3"), printerActor);

        folderActor.tell(new FolderCommands.UpdateName(1, "Akka"), printerActor);

        folderActor.tell(new FolderCommands.GetData(printerActor), printerActor);

        folderActor.tell(new FolderCommands.GetDataConditionally( printerActor, "akk"), printerActor);


        folderActor.tell("stop", printerActor);
        printerActor.tell("stop", folderActor);
    }
}
