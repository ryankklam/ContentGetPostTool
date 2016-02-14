package com.mybatis.model;

import java.util.Date;

public class JavPostInfo {
    private String javpostlink;

    private String javpostid;

    private String javposttitle;

    private Date javaposttimestamp;

    public String getJavpostlink() {
        return javpostlink;
    }

    public void setJavpostlink(String javpostlink) {
        this.javpostlink = javpostlink == null ? null : javpostlink.trim();
    }

    public String getJavpostid() {
        return javpostid;
    }

    public void setJavpostid(String javpostid) {
        this.javpostid = javpostid == null ? null : javpostid.trim();
    }

    public String getJavposttitle() {
        return javposttitle;
    }

    public void setJavposttitle(String javposttitle) {
        this.javposttitle = javposttitle == null ? null : javposttitle.trim();
    }

    public Date getJavaposttimestamp() {
        return javaposttimestamp;
    }

    public void setJavaposttimestamp(Date javaposttimestamp) {
        this.javaposttimestamp = javaposttimestamp;
    }
}