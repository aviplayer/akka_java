package com.andrew.akka.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@AllArgsConstructor
@Data
public class FolderDto {
    private int id;
    private String Name;
    private Date createdAt;
    private Date modifiedAt;
}
