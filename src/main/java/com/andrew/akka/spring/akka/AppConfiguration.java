package com.andrew.akka.spring.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import com.andrew.akka.app.actors.PrinterActor;
import com.andrew.akka.commands.FolderMessages;
import com.andrew.akka.commands.PrinterMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ComponentScan
public class AppConfiguration {

    @Autowired
    private ApplicationContext applicationContext;




    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create(ClusterListener.create(), "ClusterSystem");
        return system;
    }

    @Bean
    @DependsOn("actorSystem")
    public ActorRef<PrinterMessages> someActor() {
        Behaviors.<PrinterMessages>setup(ctx -> {
            ctx.spawn( PrinterActor.create(), "Printer_2");
            return  Behaviors.same();
        });
        ActorRef ref = actorSystem().systemActorOf(
                PrinterActor.create(),
                "my_actor",
                Props.empty()
        );

        ref.tell(new FolderMessages.ConditionNotMet("Hello from Akka !!!"));


        return ref;
    }

}
