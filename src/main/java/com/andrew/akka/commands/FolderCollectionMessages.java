package com.andrew.akka.commands;

import akka.actor.typed.ActorRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public interface FolderCollectionMessages {


    @ToString
    @Getter
    public class CreateFolder implements FolderCollectionMessages {
        private final String name;
        @JsonCreator
        public CreateFolder(String name) {
            this.name = name;
        }
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class UpdateFolderById implements FolderCollectionMessages {
        private int id;
        private String name;
    }
    
    @ToString
    @Getter
    public static class DeleteFolderById implements FolderCollectionMessages {
        private int id;
        @JsonCreator
        public DeleteFolderById(int id){
            this.id = id;
        }
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

    @ToString
    @Getter
    public static class GetAllFolders implements FolderCollectionMessages {
        private ActorRef replyTo;
        @JsonCreator
        public GetAllFolders(ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class GetFoldersConditionally implements FolderCollectionMessages {
        ActorRef replyTo;
        String condition;
    }

    @ToString
    @Getter
    public static class FoldersData extends PrinterMessages implements FolderCollectionMessages {
        List<FolderMessages.FolderData> folders;
        @JsonCreator
        public FoldersData(List<FolderMessages.FolderData> folders) {
            this.folders = folders;
        }
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class FoldersDataWithCondition extends PrinterMessages implements FolderCollectionMessages {
        List<FolderMessages.FolderData> folders;
        String condition;
    }
}
