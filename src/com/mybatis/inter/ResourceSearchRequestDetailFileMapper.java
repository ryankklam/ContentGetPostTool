package com.mybatis.inter;

import java.util.List;

import com.mybatis.model.ResourceSearchRequestDetailFile;
import com.mybatis.model.ResourceSearchRequestDetailFileKey;


public interface ResourceSearchRequestDetailFileMapper {
    int deleteByPrimaryKey(ResourceSearchRequestDetailFileKey key);

    int insert(ResourceSearchRequestDetailFile record);

    int insertSelective(ResourceSearchRequestDetailFile record);

    ResourceSearchRequestDetailFile selectByPrimaryKey(ResourceSearchRequestDetailFileKey key);

    int updateByPrimaryKeySelective(ResourceSearchRequestDetailFile record);

    int updateByPrimaryKey(ResourceSearchRequestDetailFile record);
    
	public List<ResourceSearchRequestDetailFile> selectPendingProcessRecordBySequence(String processSequence);    
}