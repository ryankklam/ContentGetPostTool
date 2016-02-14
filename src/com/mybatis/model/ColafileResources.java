package com.mybatis.model;

import client.config.Config;
import client.entity.WebStorageItemEntity;

public class ColafileResources extends WebStorageItemEntity{
    private String objectlink;

    private String storagedest;

    private String parentid;

    private String selffolderid;

    private String selffoldername;

    private String objectname;

    public String getObjectlink() {
        return objectlink;
    }

    public void setObjectlink(String objectlink) {
        this.objectlink = objectlink == null ? null : objectlink.trim();
    }

    public String getStoragedest() {
        return storagedest;
    }

    public void setStoragedest(String storagedest) {
        this.storagedest = storagedest == null ? null : storagedest.trim();
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid == null ? null : parentid.trim();
    }

    public String getSelffolderid() {
        return selffolderid;
    }

    public void setSelffolderid(String selffolderid) {
        this.selffolderid = selffolderid == null ? null : selffolderid.trim();
    }

    public String getSelffoldername() {
        return selffoldername;
    }

    public void setSelffoldername(String selffoldername) {
        this.selffoldername = selffoldername == null ? null : selffoldername.trim();
    }

    public String getObjectname() {
        return objectname;
    }

    public void setObjectname(String objectname) {
        this.objectname = objectname == null ? null : objectname.trim();
    }

	/* (non-Javadoc)
	 * @see client.entity.WebStorageItemEntity#getFileNameWithoutSuffix()
	 */
	@Override
	public String getFileNameWithoutSuffix() {
		return getObjectname();
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageItemEntity#getFullDownloadLinkWithDomain()
	 */
	@Override
	public String getFullDownloadLinkWithDomain() {
		StringBuffer sb = new StringBuffer(Config.COLAFILE_LINK_SUGGEST_FILE);
		sb.append(getObjectlink());
		return sb.toString() ;
	}

	/* (non-Javadoc)
	 * @see client.entity.WebStorageItemEntity#getStorageIdentityName()
	 */
	@Override
	public String getStorageIdentityName() {
		return "COLAFILE";
	}
}