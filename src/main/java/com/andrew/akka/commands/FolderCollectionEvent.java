package com.andrew.akka.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

public interface FolderCollectionEvent {
    @Getter
    @Setter
    public class FolderShouldAdded implements FolderCollectionEvent {
        private String name;
        @JsonCreator
        public FolderShouldAdded(String name) {
            this.name = name;
        }
    }
    
    @Getter
    public class FolderShouldDeleted implements FolderCollectionEvent {
        private int id;
        @JsonCreator
        public FolderShouldDeleted(int id) {
            this.id = id;
        }
    }
}
