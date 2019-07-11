package com.example.demo.controller;

import com.example.demo.dao.DirectoryDao;
import com.example.demo.dao.FileDao;
import com.example.demo.model.Directory;
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
import java.util.*;
import java.util.stream.Collectors;

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
            if (id == 0) {

                //防止上传重复
                String newFileName = UUID.randomUUID() + "_" + fileName;
                //上传存储路径
                String filePath = "E:\\demo2\\src\\main\\resources\\upload";
                //拼接"/"才能存在文件夹内。
                filePath = filePath + "/";
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

            }
            if (directoryDao.selectByPrimaryKey(id) != null) {
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

            } else {

                return "文件夹不存在";
            }

            return "上传" + fileName + "成功";

        } else {

            return "上传文件不能为空";
        }
    }

    @GetMapping(value = "/down/{id}")
    public ResponseEntity<byte[]> fileDown(@PathVariable int id) throws IOException {

        //根据ID获得文件对象
        if (fileDao.selectByPrimaryKey(id) != null) {

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

        } else {

            return null;

        }
    }


    @PostMapping(value = "/addDirectory")
    public String addDirectory(@RequestBody Directory directory) {

        String Path = "E:\\demo2\\src\\main\\resources\\upload";

        if (directory.getPid() == null){
            return "文件夹添加失败";
        }

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


    @GetMapping(value = "/findNext/{pid}")
    public List findNext(@PathVariable long pid) {

        if (fileDao.selectByPid(pid) != null || directoryDao.selectByPid(pid) != null) {

            List<FileInfo> listFileInfo = fileDao.selectByPid(pid);

            List<Directory> listDirectory = directoryDao.selectByPid(pid);

            ArrayList<Object> fileAndDirectoryList = new ArrayList<>();

            fileAndDirectoryList.add(listFileInfo);

            fileAndDirectoryList.add(listDirectory);


            return fileAndDirectoryList;

        } else {

            return null;
        }
    }


    @GetMapping(value = "/list")
    public List listDirectory() {

        List<Directory> listDirectory = directoryDao.listDirectory();

        if (directoryDao.selectByPid(new Long(0)) == null){

            return null;
        }

        List<Directory> listTopDirectory = directoryDao.selectByPid(new Long(0));

        ArrayList list = new ArrayList<>();

        if (listTopDirectory.size() != 0) {

            listTopDirectory.stream().forEach(x -> {

                getChildren(list, listDirectory, x);

            });

        } else {

            return null;
        }

        return list;

    }

    private Map getChildren(List list, List<Directory> listDirectory, Directory directory) {

        Map<String, Object> nodeMap = new HashMap<>();

        nodeMap.put("id", directory.getId());
        nodeMap.put("pid", directory.getPid());
        nodeMap.put("name", directory.getName());
        nodeMap.put("path", directory.getPath());

        List<Directory> collect = listDirectory.stream().filter(x -> directory.getId().equals(x.getPid()))
                .collect(Collectors.toList());

        if (collect.size() != 0) {

            List childList = new ArrayList();

            collect.stream().forEach(d -> {

                childList.add(getChildren(list, listDirectory, d));

            });

            nodeMap.put("children", childList);
        }

        if (new Long(0).equals(directory.getPid())) list.add(nodeMap);

        return nodeMap;

    }

}
