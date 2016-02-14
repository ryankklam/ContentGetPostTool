package com.mybatis.model;

public class JavPostStatus {
    private String javpostlink;

    private String javpoststoragedest;

    private String javpoststoragelink;

    private String javpostid;

    private String javpoststatus;

    public String getJavpostlink() {
        return javpostlink;
    }

    public void setJavpostlink(String javpostlink) {
        this.javpostlink = javpostlink == null ? null : javpostlink.trim();
    }

    public String getJavpoststoragedest() {
        return javpoststoragedest;
    }

    public void setJavpoststoragedest(String javpoststoragedest) {
        this.javpoststoragedest = javpoststoragedest == null ? null : javpoststoragedest.trim();
    }

    public String getJavpoststoragelink() {
        return javpoststoragelink;
    }

    public void setJavpoststoragelink(String javpoststoragelink) {
        this.javpoststoragelink = javpoststoragelink == null ? null : javpoststoragelink.trim();
    }

    public String getJavpostid() {
        return javpostid;
    }

    public void setJavpostid(String javpostid) {
        this.javpostid = javpostid == null ? null : javpostid.trim();
    }

    public String getJavpoststatus() {
        return javpoststatus;
    }

    public void setJavpoststatus(String javpoststatus) {
        this.javpoststatus = javpoststatus == null ? null : javpoststatus.trim();
    }
    
	public boolean equals(Object obj) {
		if (obj instanceof JavPostStatus) {
			JavPostStatus jps = (JavPostStatus) obj;
			return this.javpostlink.equals(jps.javpostlink)
					&& this.javpoststoragedest.equals(jps.javpoststoragedest)
					&& this.javpoststoragelink.equals(jps.javpoststoragelink)
					&& this.javpostid.equals(jps.javpostid)
					&& this.javpoststatus.equals(jps.javpoststatus)
					;
		}
		return super.equals(obj);
	}
}