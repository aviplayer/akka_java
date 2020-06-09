package com.andrew.akka.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public abstract class PrinterMessages extends Object {
    @RequiredArgsConstructor
    @ToString
    @Getter
    public static
    class SimpleMessage extends PrinterMessages {
        private final String data;
    }

    public static class Stop extends PrinterMessages {
    }
}
