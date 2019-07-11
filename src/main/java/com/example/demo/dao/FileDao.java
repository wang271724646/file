package com.example.demo.dao;

import com.example.demo.dto.FileAndDirectoryVO;
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

    List<FileAndDirectoryVO> selectFileAndDirectory(Long pid);

}