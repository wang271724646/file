package com.example.demo;

import com.example.demo.dao.DirectoryDao;
import com.example.demo.model.FileInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private DirectoryDao directoryDao;

    @Test
    public void contextLoads() {


        List<FileInfo> list = directoryDao.selectByDirectoryKey(new Long(7));
        System.out.println(list);


    }
}