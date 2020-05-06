package com.andrew.akka.app;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
@Component
public class FolderCollection {
    private List<FolderDto> folderCollection;

    public void createFolder(String name) {
        int id = folderCollection.size();
        Date date = new Date();
        FolderDto folder = new FolderDto(id, name, date, date);
        folderCollection.add(folder);
    }

    public void updateFolder(int id, String name) {
        folderCollection.get(id).setName(name);
        folderCollection.get(id).setModifiedAt(new Date());
    }

    public void getData(ActorRef responder) {
        responder.tell(this.folderCollection, responder);
    }

    public void getDataByCondition(ActorRef responder, String condition) {
        var folderConditionCollection = folderCollection.stream()
                .filter(folder -> folder.getName().toLowerCase().contains(condition.toLowerCase())).collect(Collectors.toList());
        responder.tell(folderConditionCollection, responder);
    }
}




