package com.example.demo;

import com.example.demo.dao.FileDao;
import com.example.demo.model.FileInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {

        List<String> list = new ArrayList();

        list.add("123");
        list.add("456");
        list.add("789");
        list.add("111");
        list.add("222");
        list.add("222");
        list.add("222");
        list.add("222");

        ArrayList<String> list1 = new ArrayList<>();

        list.add("123");
        list.add("222");



    }

}
