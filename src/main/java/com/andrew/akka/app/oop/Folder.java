package com.andrew.akka.app.oop;

import com.andrew.akka.app.FolderDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Folder {
    FolderDto folder = new FolderDto("Object Folder");

    public void updateFolder(String name) {
        this.folder.setName(name);
        Printer.log("New name: " + name);
    }

    public void getData() {
        Printer.log(folder.toString());
    }

    public void getDataConditionally(String condition) {
        try {
            Printer.log(folder.getDataByCondition(condition));
        } catch (NoSuchFieldException e) {
            Printer.log(e.getMessage());
        }
    }
}
