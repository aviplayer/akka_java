package com.andrew.akka.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

public interface FolderEvent {

    @AllArgsConstructor
    @ToString
    @Getter
    public class FolderCreated implements FolderEvent {
        private int id;
        private String name;
        private ZonedDateTime createdAt;
        private ZonedDateTime modifiedAt;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public class FolderUpdated implements FolderEvent {
        private String name;
        private ZonedDateTime modifiedAt;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    @Getter
    public class FolderDeleted implements FolderEvent {
        @JsonCreator
        public FolderDeleted(){}
    }
}
