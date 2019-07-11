package com.example.demo.dao;

import com.example.demo.model.FileInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface FileDao {

    int deleteByPrimaryKey(Integer id);

    int save(FileInfo fileInfo);

    FileInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileInfo fileInfo);

    List<FileInfo> listFileInfo();

    List<FileInfo> selectByPid(Long pid);

}