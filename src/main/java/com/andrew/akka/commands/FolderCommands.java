package com.andrew.akka.commands;

import akka.actor.PoisonPill;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public class FolderCommands {

    @AllArgsConstructor
    @ToString
    public static class GetData {
    }

    @AllArgsConstructor
    @ToString
    public static class UpdateName {
        public final String name;
    }

    @AllArgsConstructor
    @ToString
    public static class GetDataConditionally {
        public final String condition;
    }

    @RequiredArgsConstructor
    @ToString
    public static class ConditionNotMet {
        public final String message;
    }

    @RequiredArgsConstructor
        @ToString
        public static class Delete {
            public final PoisonPill poisonPill = PoisonPill.getInstance();
        }
}

