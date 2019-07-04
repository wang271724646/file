package com.example.demo.controller;

import com.example.demo.dao.FileDao;
import com.example.demo.util.GetFileSize;
import com.example.demo.model.FileInfo;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName FileController
 * @Descripion 文件上传下载
 * @Author wangchen
 * @Date 2019/7/2 12:24
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private FileDao fileDao;

    @PostMapping(value = "/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            //获得原始名字
            String fileName = file.getOriginalFilename();
            //获得后缀
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //获得文件类型
            String fileContentType = file.getContentType();
            //获得文件大小
            Long fileSize = file.getSize();
            String printSize = GetFileSize.getPrintSize(fileSize);
            //最大文件大小10M
            final long MAX_SIZE = 1024 * 100 * 100;
            if (fileSize > MAX_SIZE) {

                return "文件不能超过10M";

            }

            //防止上传重复
            String newFileName = UUID.randomUUID() + "_" + fileName;
            //上传存储路径
            String filePath = "E:/demo2/src/main/resources/file/";
            File saveFile = new File(filePath + newFileName);
            try {

                file.transferTo(saveFile);

            } catch (Exception e) {

                e.printStackTrace();
                return "上传失败" + e.getMessage();

            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(newFileName)
                    .setFileUpdated(new Date())
                    .setFilePath(filePath)
                    .setFileType(fileContentType)
                    .setFileSize(printSize);
            fileDao.save(fileInfo);

            return "上传" + fileName + "成功";

        } else {

            return "上传文件不能为空";
        }

    }


    @GetMapping(value = "/down/{id}")
    public ResponseEntity<byte[]> fileDown(@PathVariable int id) throws IOException {

        //根据ID获得文件对象
        FileInfo fileInfo = fileDao.selectByPrimaryKey(id);
        //获得文件名字
        String fileName = fileInfo.getFileName();
        //解析真实名字
        String[] realFileNameArray = fileName.split("_");
        //得到文件路径
        String filePath = fileInfo.getFilePath();
        //得到文件
        File file = new File(filePath + fileName);
        byte[] body = null;
        InputStream is = new FileInputStream(file);
        body = new byte[is.available()];
        is.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + URLEncoder.encode(realFileNameArray[1], "UTF-8"));
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<>(body, headers, statusCode);

        return entity;

    }


    @GetMapping(value = "/list")
    public List listFileInfo() {

        List<FileInfo> listFileInfo = fileDao.listFileInfo();

        return listFileInfo;

    }

}
