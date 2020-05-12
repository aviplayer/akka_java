package com.andrew.akka.app.states;

import akka.actor.typed.ActorRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RealCollectionState extends CollectionState {
    private Map<Integer, ActorRef> state = new HashMap<>();

    @JsonIgnore
    private boolean isCheckedout;

    @JsonCreator
    public RealCollectionState(Map<Integer, ActorRef> state) {
        this.isCheckedout = true;
        this.state = state;
    }

    public RealCollectionState() {
        this.isCheckedout = false;
    }
}
