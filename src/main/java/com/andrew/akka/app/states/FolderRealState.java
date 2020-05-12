package com.andrew.akka.app.states;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
@Setter
public class FolderRealState implements FolderState {
    private int id;
    private String name;
    private final ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;
}
