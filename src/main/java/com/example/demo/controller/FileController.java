package com.example.demo.controller;

import com.example.demo.dao.DirectoryDao;
import com.example.demo.dao.FileDao;
import com.example.demo.model.Directory;
import com.example.demo.util.GetFileSize;
import com.example.demo.model.FileInfo;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    @Autowired
    private DirectoryDao directoryDao;

    @PostMapping(value = "/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("id") long id) {

        if (!file.isEmpty()) {

            //获得原始名字
            String fileName = file.getOriginalFilename();
            //获得文件类型
            String fileContentType = file.getContentType();
            //获得文件大小
            long fileSize = file.getSize();
            String printSize = GetFileSize.getPrintSize(fileSize);
            //最大文件大小10M
            final long MAX_SIZE = 1024 * 100 * 100;
            if (fileSize > MAX_SIZE) {

                return "文件不能超过10M";

            }

            //防止上传重复
            String newFileName = UUID.randomUUID() + "_" + fileName;
            //上传存储路径
            String filePath = directoryDao.selectByPrimaryKey(id).getPath();
            //拼接"/"才能存在文件夹内。
            filePath = filePath + "/";
            System.out.println(filePath);
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
                    .setFileSize(printSize)
                    .setPid(id);

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
        //得到文件路径
        String filePath = fileInfo.getFilePath();
        //得到文件
        File file = new File(filePath + fileName);
        byte[] body = null;
        InputStream is = new FileInputStream(file);
        body = new byte[is.available()];
        is.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> entity = new ResponseEntity<>(body, headers, statusCode);

        return entity;
    }


    @PostMapping(value = "/addDirectory")
    public String addDirectory(@RequestBody Directory directory) {

        String Path = "E:\\demo2\\src\\main\\resources\\upload";

        if (directory.getPid() == 0) {
            //拼接存储路径
            Path = Path + "\\" + directory.getName();
            directory.setPath(Path);
            directoryDao.insert(directory);
            File file = new File(Path);
            file.mkdirs();

        } else {

            String path = directoryDao.selectByPrimaryKey(directory.getPid()).getPath();
            //拼接存储路径
            Path = path + "\\" + directory.getName();
            directory.setPath(Path);
            directoryDao.insert(directory);
            File file = new File(Path);
            file.mkdirs();

        }

        return "添加" + directory.getName() + "成功";
    }



/*    @GetMapping(value = "/list")
    public void listDirectory(@PathVariable long id) {

        Directory directory = directoryDao.selectByPrimaryKey(id);

        String path = directory.getPath();





    }*/

    @GetMapping(value = "/list")
    public void listDirectory() {

        List<Directory> listDirectory = directoryDao.listDirectory();

        List<Directory> listTopDirectory = directoryDao.selectByPid(new Long(0));

        HashMap<String, Object> HashMap = new HashMap<>();

        listTopDirectory.stream().forEach(x->{
            HashMap.put("id",x.getId());
            HashMap.put("pid","0");
            HashMap.put("name",x.getName());
            HashMap.put("path",x.getPath());
            HashMap.put("children",getChildren(x.getId(),listDirectory,x))


        });

    }

    private list getChildren(Long id, List<Directory> listDirectory,Directory directory) {

        listDirectory.stream().forEach(x->{

            Map<String, Object> Map = new HashMap<>();

            ArrayList<Directory> directoryArrayList = new ArrayList<>(16);

            if (id.equals(x.getPid())){

                directoryArrayList.add(x);

            }

        });

        Map.put("Children",directoryArrayList);

        getChildren(x.getId(),listDirectory,directory);

        System.out.println(Map);
        System.out.println(directoryArrayList);

     return list;
    }
}
