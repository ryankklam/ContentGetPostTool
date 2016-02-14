package com.mybatis.inter;

import java.util.List;

import com.mybatis.model.JavPostInfo;

public interface JavPostInfoMapper {
    int deleteByPrimaryKey(String javpostlink);

    int insert(JavPostInfo record);

    int insertSelective(JavPostInfo record);

    JavPostInfo selectByPrimaryKey(String javpostlink);

    int updateByPrimaryKeySelective(JavPostInfo record);

    int updateByPrimaryKey(JavPostInfo record);
    
    List<JavPostInfo> selectAll();
    
    JavPostInfo selectByJavPostId(String javpostid);
    
    List<String> selectPendingPostJavID();
}