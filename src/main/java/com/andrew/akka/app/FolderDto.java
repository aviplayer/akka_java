package com.andrew.akka.app;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class FolderDto {
    private int id;
    private String name;
    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public FolderDto(String Name){
        ZonedDateTime date = ZonedDateTime.now();
        setId(1);
        setName(Name);
        setCreatedAt(date);
        setModifiedAt(date);
    }

    @Override
    public String toString(){
        return "id:{" + id + "}, " + "name:{" + name + "}, "  +
                "createdAt:{" + createdAt.toString() + "}, "
                + "createdAt:{" + modifiedAt.toString() + "}, ";
    }

    public String getDataByCondition(String condition) throws NoSuchFieldException {
        if(name.toLowerCase().contains(condition.toLowerCase())){
            return "Found by Condition: << " + this.toString() + " >>";
        }else {
            throw new NoSuchFieldException("Invalid Folder criteria!");
        }

    }
}
