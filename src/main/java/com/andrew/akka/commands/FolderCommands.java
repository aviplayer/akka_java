package com.andrew.akka.commands;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class FolderCommands {

    @AllArgsConstructor
    @ToString
    public static class GetData {
        public final ActorRef replyTo;
    }

    @AllArgsConstructor
    @ToString
    public static class UpdateName {
        public final String newName;
    }

    @AllArgsConstructor
    @ToString
    public static class GetDataConditionally {
        public final String condition;
        public final ActorRef replyTo;
    }

    @RequiredArgsConstructor
    @ToString
    public static class ConditionNotMet {
        public final String message;
    }

    @RequiredArgsConstructor
    @ToString
    public static class Delete {}
}

