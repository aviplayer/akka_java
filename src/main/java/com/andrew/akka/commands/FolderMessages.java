package com.andrew.akka.commands;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

public class FolderMessages {
    public interface FolderMessage {}

    public interface FolderResponses {}

    public interface FolderAggregatorMessage {}

    @AllArgsConstructor
    @ToString
    public static class GetData implements FolderMessage {
        public final ActorRef replyTo;
    }

    @AllArgsConstructor
    @ToString
    public static class UpdateName implements FolderMessage {
        public final String newName;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class GetDataConditionally implements FolderMessage {
        private final String condition;
        private final ActorRef replyTo;
    }

    @RequiredArgsConstructor
    @ToString
    @Getter
    public static class ConditionNotMet implements FolderMessage, PrinterMessages, FolderAggregatorMessage, FolderCollectionMessages {
        private final String condition;
    }

    @RequiredArgsConstructor
    @ToString
    @Getter
    public static class Delete implements FolderMessage {
        private final ActorRef replyTo;
    }

    @RequiredArgsConstructor
    @ToString
    @Getter
    public static class FolderData implements PrinterMessages, FolderCollectionMessages, FolderMessage, FolderAggregatorMessage {
        private final int id;
        private final String name;
        private final ZonedDateTime createdAt;
        private final ZonedDateTime modifiedAt;
    }

    public static class Stopped implements FolderCollectionMessages{}

    public static class Stop implements FolderMessage, FolderCollectionMessages{
    }
}

