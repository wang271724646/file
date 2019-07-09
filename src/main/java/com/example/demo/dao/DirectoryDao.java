package com.example.demo.dao;

import com.example.demo.model.Directory;
import com.example.demo.model.FileInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DirectoryDao {

    int deleteByPrimaryKey(Long id);

    int insert(Directory record);

    int insertSelective(Directory record);

    Directory selectByPrimaryKey(Long id);

    List<Directory> selectByPid(Long id);

    List<Directory> listDirectory();

    int updateByPrimaryKeySelective(Directory record);

    int updateByPrimaryKey(Directory record);

    List<FileInfo> selectByDirectoryKey(Long id);
}