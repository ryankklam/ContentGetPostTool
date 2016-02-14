package com.mybatis.inter;

import java.util.List;

import com.mybatis.model.YunFileInfoItemEntity;

public interface YunFileInfoItemEntityMapper {
    int deleteByPrimaryKey(String fileId);

    int insert(YunFileInfoItemEntity record);

    int insertSelective(YunFileInfoItemEntity record);

    YunFileInfoItemEntity selectByPrimaryKey(String fileId);

    int updateByPrimaryKeySelective(YunFileInfoItemEntity record);

    int updateByPrimaryKey(YunFileInfoItemEntity record);
    
    List<YunFileInfoItemEntity> selectAll();
    
    int selectPendingPostItemCount();
    
    List<YunFileInfoItemEntity> selectPendingPostItem();
    
    List<YunFileInfoItemEntity> selectByObjectName(String objectName);
}