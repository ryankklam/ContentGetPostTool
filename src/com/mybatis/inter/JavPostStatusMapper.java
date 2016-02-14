package com.mybatis.inter;

import java.util.List;

import com.mybatis.model.JavPostStatus;
import org.apache.ibatis.annotations.Param;

public interface JavPostStatusMapper {
    int deleteByPrimaryKey(String javpostlink);

    int insert(JavPostStatus record);

    int insertSelective(JavPostStatus record);

    JavPostStatus selectByPrimaryKey(String javpostlink);

    int updateByPrimaryKeySelective(JavPostStatus record);

    int updateByPrimaryKey(JavPostStatus record);
    
    List<JavPostStatus> selectByStorageDest(String storageDest);
    
    int selectCountAll();
    
    int selectCountAllByStatus(String status);
    
    List<JavPostStatus> selectByJavPostIdRange(@Param("low")int low, @Param("high")int high);
}