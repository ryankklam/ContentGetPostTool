package com.mybatis.model;

public class ResourceSearchRequestFile {
    private String processsequence;

    private String searchkeyword;

    private String requestprocessstatus;
    
    private String requesttype;

    /**
	 * @return the requesttype
	 */
	public String getRequesttype() {
		return requesttype;
	}

	/**
	 * @param requesttype the requesttype to set
	 */
	public void setRequesttype(String requesttype) {
		this.requesttype = requesttype;
	}

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

    public String getRequestprocessstatus() {
        return requestprocessstatus;
    }

    public void setRequestprocessstatus(String requestprocessstatus) {
        this.requestprocessstatus = requestprocessstatus == null ? null : requestprocessstatus.trim();
    }
}