package com.andrew.akka.commands;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;

public class FolderCommands {
    
    @AllArgsConstructor
    public static class CreateFolder {
        public final String name;
    }

    @AllArgsConstructor
    public static class GetData {
        public final ActorRef replyTo;
    }

    @AllArgsConstructor
    public static class UpdateName {
        public final int id;
        public final String name;
    }

    @AllArgsConstructor
    public static class GetDataConditionally {
        public final ActorRef replyTo;
        public final String condition;
    }
}
