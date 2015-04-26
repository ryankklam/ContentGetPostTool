package com.mybatis.model;

public class ResourceSearchRequestDetailFile extends ResourceSearchRequestDetailFileKey {
    private String requestprocessstatus;

    private String magnetinfohashinhex;

    public String getRequestprocessstatus() {
        return requestprocessstatus;
    }

    public void setRequestprocessstatus(String requestprocessstatus) {
        this.requestprocessstatus = requestprocessstatus == null ? null : requestprocessstatus.trim();
    }

    public String getMagnetinfohashinhex() {
        return magnetinfohashinhex;
    }

    public void setMagnetinfohashinhex(String magnetinfohashinhex) {
        this.magnetinfohashinhex = magnetinfohashinhex == null ? null : magnetinfohashinhex.trim();
    }
    
    public void setProcesssequence(String processsequence) {
        super.setProcesssequence(processsequence);
    }
}