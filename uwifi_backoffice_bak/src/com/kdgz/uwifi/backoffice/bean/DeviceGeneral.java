package com.kdgz.uwifi.backoffice.bean;

import java.io.Serializable;

public class DeviceGeneral implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4758108468323259699L;

	private float utilizationrate;

	private int incoming;

	private int outcoming;

	public float getUtilizationrate() {
		return utilizationrate;
	}

	public void setUtilizationrate(float utilizationrate) {
		this.utilizationrate = utilizationrate;
	}

	public int getIncoming() {
		return incoming;
	}

	public void setIncoming(int incoming) {
		this.incoming = incoming;
	}

	public int getOutcoming() {
		return outcoming;
	}

	public void setOutcoming(int outcoming) {
		this.outcoming = outcoming;
	}

}
