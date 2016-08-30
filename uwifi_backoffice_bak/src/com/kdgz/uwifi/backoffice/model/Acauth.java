package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Acauth extends Model<Acauth>{

	public static final Acauth dao = new Acauth();
	
	/**
	 * 查找Acauth信息通过ACId
	 * @param acid
	 * @return
	 */
	public Acauth selectAcauthByAcId(String businessid){//acid to businessid
		
		Acauth acauth = dao.findFirst("select * from acauth where businessid = '"+businessid+"' ");
		return acauth;
	}
}
