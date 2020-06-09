package com.andrew.akka.spring.akka;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ClusterEvent;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.ClusterSingleton;
import akka.cluster.typed.SingletonActor;
import akka.cluster.typed.Subscribe;
import akka.persistence.typed.PersistenceId;
import com.andrew.akka.app.actors.FolderCollectionActor;
import com.andrew.akka.app.actors.PrinterActor;
import com.andrew.akka.commands.FolderCollectionMessages;

import java.time.Duration;


public final class ClusterListener extends AbstractBehavior<ClusterListener.Event> {

    interface Event {
    }

    // internal adapted cluster events only
    private static final class ReachabilityChange implements Event {
        final ClusterEvent.ReachabilityEvent reachabilityEvent;

        ReachabilityChange(ClusterEvent.ReachabilityEvent reachabilityEvent) {
            this.reachabilityEvent = reachabilityEvent;
        }
    }

    private static final class MemberChange implements Event {

        final ClusterEvent.MemberEvent memberEvent;

        MemberChange(ClusterEvent.MemberEvent memberEvent) {
            this.memberEvent = memberEvent;
        }
    }

    public static Behavior<Event> create() {
        return Behaviors.setup(ClusterListener::new);
    }

    private ClusterListener(ActorContext<Event> context) {

        super(context);

        Cluster cluster = Cluster.get(context.getSystem());

        ActorRef<ClusterEvent.MemberEvent> memberEventAdapter =
                context.messageAdapter(ClusterEvent.MemberEvent.class, MemberChange::new);
        cluster.subscriptions().tell(Subscribe.create(memberEventAdapter, ClusterEvent.MemberEvent.class));

        ActorRef<ClusterEvent.ReachabilityEvent> reachabilityAdapter =
                context.messageAdapter(ClusterEvent.ReachabilityEvent.class, ReachabilityChange::new);
        cluster.subscriptions().tell(Subscribe.create(reachabilityAdapter, ClusterEvent.ReachabilityEvent.class));

        ActorRef printer = context.spawn(PrinterActor.create(), "Printer");


        ClusterSingleton singleton = ClusterSingleton.get(context.getSystem());

        ActorRef foldersCollection = singleton.init(
                SingletonActor.of(
                        Behaviors.supervise(FolderCollectionActor.create(PersistenceId.ofUniqueId("folders-collection")))
                                .onFailure(
                                        SupervisorStrategy.restartWithBackoff(
                                                Duration.ofSeconds(1), Duration.ofSeconds(10), 0.2)),
                        "folders-collection-name"));

        for (var i = 0; i < 5; i++) {
            foldersCollection.tell(new FolderCollectionMessages.CreateFolder("Folder"));
        }

//        foldersCollection.tell(new FolderCollectionMessages.DeleteFolderById(1));
//        foldersCollection.tell(new FolderCollectionMessages.GetFolderById(1, printer));
//        foldersCollection.tell(new FolderCollectionMessages.UpdateFolderById(0, "New name"));
//        foldersCollection.tell(new FolderCollectionMessages.UpdateFolderById(4, "New name 1"));
//        foldersCollection.tell(new FolderCollectionMessages.GetFolderById(4, printer));
//        foldersCollection.tell(new FolderCollectionMessages.GetFoldersConditionally(printer, "New name"));
//        foldersCollection.tell(new FolderCollectionMessages.GetFoldersConditionally(printer, "fake"));
//        foldersCollection.tell(new FolderCollectionMessages.GetAllFolders(printer));
 //       foldersCollection.tell(new FolderCollectionMessages.GetFoldersConditionally(someActor, "Testing"));

    }


    @Override
    public Receive<Event> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReachabilityChange.class, this::onReachabilityChange)
                .onMessage(MemberChange.class, this::onMemberChange)
                .build();
    }


    private Behavior<Event> onReachabilityChange(ReachabilityChange event) {
        if (event.reachabilityEvent instanceof ClusterEvent.UnreachableMember) {
            getContext().getLog().info("Member detected as unreachable: {}", event.reachabilityEvent.member());
        } else if (event.reachabilityEvent instanceof ClusterEvent.ReachableMember) {
            getContext().getLog().info("Member back to reachable: {}", event.reachabilityEvent.member());
        }
        return this;
    }

    private Behavior<Event> onMemberChange(MemberChange event) {
        if (event.memberEvent instanceof ClusterEvent.MemberUp) {
            getContext().getLog().info("Member is up: {}", event.memberEvent.member());
        } else if (event.memberEvent instanceof ClusterEvent.MemberRemoved) {
            getContext().getLog().info("Member is removed: {} after {}",
                    event.memberEvent.member(),
                    ((ClusterEvent.MemberRemoved) event.memberEvent).previousStatus()
            );
        }
        return this;
    }

}
