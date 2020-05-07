package com.andrew.akka.commands;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

public class FolderCommands {
    public interface FolderCommand {}

    @AllArgsConstructor
    @ToString
    public static class GetData implements FolderCommand {
        public final ActorRef replyTo;
    }

    @AllArgsConstructor
    @ToString
    public static class UpdateName implements FolderCommand {
        public final String newName;
    }

    @AllArgsConstructor
    @ToString
    public static class GetDataConditionally implements FolderCommand {
        public final String condition;
        public final ActorRef replyTo;
    }

    @RequiredArgsConstructor
    @ToString
    public static class ConditionNotMet implements FolderCommand {
        public final String message;
    }

    @RequiredArgsConstructor
    @ToString
    public static class Delete implements FolderCommand {
        public final ActorRef replyTo;
    }

    @RequiredArgsConstructor
    @ToString
    public static class Folder implements FolderCommand {
        public final Object data;
    }

    @AllArgsConstructor
    public static class GetFolder implements FolderCommand {
        private final int id;
        private String name;
        private final ZonedDateTime createdAt;
        private ZonedDateTime modifiedAt;

        @Override
        public String toString() {
            return "id:{" + id + "}, " + "name:{" + name + "}, " +
                    "createdAt:{" + createdAt.toString() + "}, "
                    + "createdAt:{" + modifiedAt.toString() + "}, ";
        }
    }

    public static class Stop implements FolderCommand {}
}

