package com.mybatis.model;

public class MagnetInfo {
    private String magnetinfohashinhex;

    private String resourcename;

    private String resourcedescription;

    private String fullmagnetinfolink;

    public String getMagnetinfohashinhex() {
        return magnetinfohashinhex;
    }

    public void setMagnetinfohashinhex(String magnetinfohashinhex) {
        this.magnetinfohashinhex = magnetinfohashinhex == null ? null : magnetinfohashinhex.trim();
    }

    public String getResourcename() {
        return resourcename;
    }

    public void setResourcename(String resourcename) {
        this.resourcename = resourcename == null ? null : resourcename.trim();
    }

    public String getResourcedescription() {
        return resourcedescription;
    }

    public void setResourcedescription(String resourcedescription) {
        this.resourcedescription = resourcedescription == null ? null : resourcedescription.trim();
    }

    public String getFullmagnetinfolink() {
        return fullmagnetinfolink;
    }

    public void setFullmagnetinfolink(String fullmagnetinfolink) {
        this.fullmagnetinfolink = fullmagnetinfolink == null ? null : fullmagnetinfolink.trim();
    }
}