package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Smsacconfig extends Model<Smsacconfig>{

	public static final Smsacconfig dao = new Smsacconfig();
	
	/**
	 * 查找Smsacconfig信息通过ACId
	 * @param acid
	 * @return
	 */
	public Smsacconfig selectSmsacconfigByAcId(String businessid){//acid to businessid
		
		Smsacconfig smsacconfig = dao.findFirst("select * from smsacconfig where businessid = '"+businessid+"' ");
		return smsacconfig;
	}
	
}
