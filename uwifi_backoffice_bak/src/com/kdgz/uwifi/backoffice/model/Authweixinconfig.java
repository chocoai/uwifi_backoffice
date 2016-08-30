package com.kdgz.uwifi.backoffice.model;


import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Authweixinconfig extends Model<Authweixinconfig>{

	public static final Authweixinconfig dao = new Authweixinconfig();
	
	/**
	 * 查找Authweixinconfig信息通过businessid
	 * @param acid
	 * @return
	 */
	public Authweixinconfig selectAuthweixinByBusinessId(String businessid){
		
		Authweixinconfig authweixinconfig = dao.findFirst("select * from authweixinconfig where businessid = '"+businessid+"' ");
		return authweixinconfig;
	}
	
	
	
}
