package com.andrew;


import akka.actor.typed.ActorSystem;
import com.andrew.akka.app.cluster.ClusterListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

public class App {
    public static void main(String[] args) {


        Config nodeConfig1 = ConfigFactory.parseString("akka.remote.artery.canonical.port=25251")
                .withFallback(ConfigFactory.load());

        Config nodeConfig2 = ConfigFactory.parseString("akka.remote.artery.canonical.port=25252")
                .withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create(ClusterListener.create(), "ClusterSystem");

        try {
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            system.terminate();
        }

    }
}

