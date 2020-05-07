package com.andrew;


import akka.actor.typed.ActorSystem;
import com.andrew.akka.app.actors.RootBehaviour;

import java.io.IOException;

public class App {
    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create(RootBehaviour.create(), "folder-app");

        try {
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            system.terminate();
        }

    }
}

