package com.mybatis.model;

import client.config.Config;
import client.entity.WebStorageItemEntity;


public class YunFileInfoItemEntity extends WebStorageItemEntity{
    private String fileId;

    private String createTime;

    private String lastModified;

    private String userId;

    private String name;

    private String downLink;

    private String parent;

    private String isShared;

    private String type;

    private String size;

    private String storage;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId == null ? null : fileId.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified == null ? null : lastModified.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDownLink() {
        return downLink;
    }

    public void setDownLink(String downLink) {
        this.downLink = downLink == null ? null : downLink.trim();
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent == null ? null : parent.trim();
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared == null ? null : isShared.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage == null ? null : storage.trim();
    }
	
	/* (non-Javadoc)
	 * @see client.entity.WebStorageItemEntity#getFileNameWithoutSuffix()
	 */
	@Override
	public String getFileNameWithoutSuffix (){
		return getName().substring(0,getName().lastIndexOf("."));
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageItemEntity#getFullDownloadLinkWithDomain()
	 */
	@Override
	public String getFullDownloadLinkWithDomain() {
		return Config.YUNFILE_DOMAIN_ARRAY[0] + "fs/" + downLink + "/";
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageItemEntity#getStorageIdentityName()
	 */
	@Override
	public String getStorageIdentityName() {
		return "YUNFILE";
	}
}