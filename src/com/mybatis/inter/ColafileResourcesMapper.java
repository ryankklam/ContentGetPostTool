package com.mybatis.inter;

import java.util.List;

import com.mybatis.model.ColafileResources;

public interface ColafileResourcesMapper {
    int deleteByPrimaryKey(String objectlink);

    int insert(ColafileResources record);

    int insertSelective(ColafileResources record);

    ColafileResources selectByPrimaryKey(String objectlink);

    int updateByPrimaryKeySelective(ColafileResources record);

    int updateByPrimaryKey(ColafileResources record);
    
    List<ColafileResources> selectBySelfFolderName(String selfFolderName);
    
    int deleteByObjectName(String objectName);
    
    List<ColafileResources> selectByObjectName(String objectName);
    
    List<ColafileResources> selectPendingPostItem();
    
    int selectPendingPostItemCount();
    
    List<String> selectPendingUpdateFolderList();
    
    List<String> selectExisitngPrefixList();
    
}