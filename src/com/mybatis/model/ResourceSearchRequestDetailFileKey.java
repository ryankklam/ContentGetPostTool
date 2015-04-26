package com.mybatis.model;

public class ResourceSearchRequestDetailFileKey {
    protected String processsequence;

    protected String searchkeyword;

    public String getProcesssequence() {
        return processsequence;
    }

    public void setProcesssequence(String processsequence) {
        this.processsequence = processsequence == null ? null : processsequence.trim();
    }

    public String getSearchkeyword() {
        return searchkeyword;
    }

    public void setSearchkeyword(String searchkeyword) {
        this.searchkeyword = searchkeyword == null ? null : searchkeyword.trim();
    }
}