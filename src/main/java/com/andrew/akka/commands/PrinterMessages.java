package com.andrew.akka.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface PrinterMessages extends FolderMessages.FolderMessage {
    @RequiredArgsConstructor
    @ToString
    @Getter
    class SimpleMessage implements PrinterMessages {
        private final String data;
    }

    class Stop implements PrinterMessages {
    }
}
