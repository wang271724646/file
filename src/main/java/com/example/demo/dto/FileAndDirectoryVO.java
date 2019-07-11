package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName FileAndDirectoryVO
 * @Descripion ToDo
 * @Author wangchen
 * @Date 2019/7/9 16:15
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileAndDirectoryVO {

    private Long id;

    private Long pid;

    private String name;

    private String path;

    private Long fileId;

    private Long filePid;

    private String fileName;

    private String fileType;

    private String fileSize;

    private Date fileUpdated;

    private String filePath;



}
