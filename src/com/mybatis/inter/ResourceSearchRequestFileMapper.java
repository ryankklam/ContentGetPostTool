package com.mybatis.inter;

import com.mybatis.model.ResourceSearchRequestFile;

public interface ResourceSearchRequestFileMapper {
    int deleteByPrimaryKey(String processsequence);

    int insert(ResourceSearchRequestFile record);

    int insertSelective(ResourceSearchRequestFile record);

    ResourceSearchRequestFile selectByPrimaryKey(String processsequence);

    int updateByPrimaryKeySelective(ResourceSearchRequestFile record);

    int updateByPrimaryKey(ResourceSearchRequestFile record);
    
    int CheckOutstandingProcessStatusCount();
    
    ResourceSearchRequestFile selectLatestOutstandingProcessRecord();
    
    ResourceSearchRequestFile selectLatestProcessRecordByPrefix(String prefix);
}