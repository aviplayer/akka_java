package com.andrew;


import akka.actor.typed.ActorSystem;
import com.andrew.akka.app.actors.RootBehavior;

import java.io.IOException;

public class App {
    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create(RootBehavior.create(), "folder-app");

        try {
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            system.terminate();
        }

    }
}

