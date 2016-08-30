package com.kdgz.uwifi.backoffice.model;


public class TempletpageLog {
	
	private String acId;
	
	private String templetId;
	
	private String temptype;
	
	private String mediaurl;
	
	private String outerurl;
	
	private String sort;
	
	private String optionTime;
	
	private String status;
	
	

	public String getAcId() {
		return acId;
	}

	public void setAcId(String acId) {
		this.acId = acId;
	}

	public String getTempletId() {
		return templetId;
	}

	public void setTempletId(String templetId) {
		this.templetId = templetId;
	}

	public String getTemptype() {
		return temptype;
	}

	public void setTemptype(String temptype) {
		this.temptype = temptype;
	}

	public String getMediaurl() {
		return mediaurl;
	}
	
	public void setMediaurl(String mediaurl) {
		this.mediaurl = mediaurl;
	}
	
	public String getOuterurl() {
		return outerurl;
	}

	public void setOuterurl(String outerurl) {
		this.outerurl = outerurl;
	}

	public String getOptionTime() {
		return optionTime;
	}

	public void setOptionTime(String optionTime) {
		this.optionTime = optionTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public static String[] getColumnArray() {

		String[] array = new String[] { "acId", "templetId", "temptype", "mediaurl", 
				"sort", "optionTime", "status", "outerurl"};

		return array;
	}
	
	
}
