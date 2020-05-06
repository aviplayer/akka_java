package com.andrew.akka.app.oop;

import java.util.logging.Logger;

public class Printer {
    public static void log(String message) {
        Logger.getGlobal().info(message);
    }
}
