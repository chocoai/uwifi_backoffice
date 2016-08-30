package com.kdgz.uwifi.backoffice.bean;

/**
 * 在线状态详情Bean
 * 
 * @author lanbo
 * 
 */
public class OnlineStatusDetail {

	private String ssid;

	private String busName;

	private String acid;

	private String accumulatedusetime;

	private String lastdate;

	private boolean online;

	private long incoming;

	private long outcoming;

	private String utilizationrate;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public String getAcid() {
		return acid;
	}

	public void setAcid(String acid) {
		this.acid = acid;
	}

	public String getAccumulatedusetime() {
		return accumulatedusetime;
	}

	public void setAccumulatedusetime(String accumulatedusetime) {
		this.accumulatedusetime = accumulatedusetime;
	}

	public String getLastdate() {
		return lastdate;
	}

	public void setLastdate(String lastdate) {
		this.lastdate = lastdate;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public long getIncoming() {
		return incoming;
	}

	public void setIncoming(long incoming) {
		this.incoming = incoming;
	}

	public long getOutcoming() {
		return outcoming;
	}

	public void setOutcoming(long outcoming) {
		this.outcoming = outcoming;
	}

	public String getUtilizationrate() {
		return utilizationrate;
	}

	public void setUtilizationrate(String utilizationrate) {
		this.utilizationrate = utilizationrate;
	}

}
