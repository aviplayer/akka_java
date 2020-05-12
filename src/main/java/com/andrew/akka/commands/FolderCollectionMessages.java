package com.andrew.akka.commands;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public interface FolderCollectionMessages {


    @AllArgsConstructor
    @ToString
    @Getter
    public static class CreateFolder implements FolderCollectionMessages {
        private final String name;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class UpdateFolderById implements FolderCollectionMessages {
        private int id;
        private String name;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class DeleteFolderById implements FolderCollectionMessages {
        private int id;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class FolderDelete implements FolderCollectionMessages {
        private int id;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class GetFolderById implements FolderCollectionMessages {
        private int id;
        private ActorRef replyTo;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class GetAllFolders implements FolderCollectionMessages {
        private ActorRef replyTo;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class GetFoldersConditionally implements FolderCollectionMessages {
        ActorRef replyTo;
        String condition;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class FoldersData implements FolderCollectionMessages, PrinterMessages {
        List<FolderMessages.FolderData> folders;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class FoldersDataWithCondition implements FolderCollectionMessages, PrinterMessages {
        List<FolderMessages.FolderData> folders;
        String condition;
    }
}
