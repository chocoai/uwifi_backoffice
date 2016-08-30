package com.kdgz.uwifi.backoffice.model;

import java.io.Serializable;

/**
 * 共通配置参数
 * 
 * @author lanbo
 * 
 */
public class Property implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538007733805703526L;

	private String csvLogPath;

	private String defaultPortalTpl;

	private String fileProxyDomain;

	public String getCsvLogPath() {
		return csvLogPath;
	}

	public void setCsvLogPath(String csvLogPath) {
		this.csvLogPath = csvLogPath;
	}

	public String getFileProxyDomain() {
		return fileProxyDomain;
	}

	public void setFileProxyDomain(String fileProxyDomain) {
		this.fileProxyDomain = fileProxyDomain;
	}

	public String getDefaultPortalTpl() {
		return defaultPortalTpl;
	}

	public void setDefaultPortalTpl(String defaultPortalTpl) {
		this.defaultPortalTpl = defaultPortalTpl;
	}
}
