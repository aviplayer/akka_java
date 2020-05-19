package com.andrew.akka.commands;

import akka.actor.typed.ActorRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

import java.time.ZonedDateTime;

public class FolderMessages {
    public interface FolderMessage {}

    public interface FolderAggregatorMessage {}

    @AllArgsConstructor
    @ToString
    public static class GetData implements FolderMessage {
        public final ActorRef replyTo;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class UpdateName extends FolderMessages implements FolderMessage {
        private String newName;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class GetDataConditionally implements FolderMessage {
        private final String condition;
        private final ActorRef replyTo;
    }

    @ToString
    @Getter
    public static class ConditionNotMet implements FolderMessage, PrinterMessages, FolderAggregatorMessage, FolderCollectionMessages {
        private final String condition;
        @JsonCreator
        public ConditionNotMet(String condition) {
            this.condition = condition;
        }
    }

    @RequiredArgsConstructor
    @ToString
    @Getter
    public static class Delete implements FolderMessage {
        private final ActorRef replyTo;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class FolderData implements PrinterMessages, FolderCollectionMessages, FolderMessage, FolderAggregatorMessage {
        private final int id;
        private final String name;
        private final ZonedDateTime createdAt;
        private final ZonedDateTime modifiedAt;
    }
}

