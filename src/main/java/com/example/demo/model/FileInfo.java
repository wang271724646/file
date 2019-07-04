package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileInfo implements Serializable {

    private Integer id;

    private String fileName;

    private String fileType;

    private String fileSize;

    private Date fileUpdated;

    private String filePath;

}