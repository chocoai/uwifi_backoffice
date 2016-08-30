package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Siteinfo extends Model<Siteinfo>{

	public static final Siteinfo dao = new Siteinfo();
	
	public Siteinfo getSiteinfoByBusinessid(Integer businessid){
		
		Siteinfo siteinfo = dao.findFirst("select * from siteinfo where businessid = "+businessid);
		return siteinfo;
	}
	
}
