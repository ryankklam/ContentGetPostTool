package com.mybatis.inter;

import java.util.List;

import com.mybatis.model.MagnetInfo;

public interface MagnetInfoMapper {
    int deleteByPrimaryKey(String magnetinfohashinhex);

    int insert(MagnetInfo record);

    int insertSelective(MagnetInfo record);

    MagnetInfo selectByPrimaryKey(String magnetinfohashinhex);

    int updateByPrimaryKeySelective(MagnetInfo record);

    int updateByPrimaryKey(MagnetInfo record);
    
    List<MagnetInfo> selectAll();
}