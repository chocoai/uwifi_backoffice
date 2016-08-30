package com.kdgz.uwifi.backoffice.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Multiauthconfig extends Model<Multiauthconfig>{

	public static final Multiauthconfig dao = new Multiauthconfig();
	
	public Multiauthconfig selectMultiauthBymmsid(Integer smsid, String businessid){//acid to businessid
		StringBuffer sql = new StringBuffer();
		sql.append("select * from multiauthconfig where 1=1 ");
		if(smsid > -1){
			sql.append(" and smsid = "+smsid);
		}
		if(StrKit.notBlank(businessid)){
			sql.append(" and businessid = '"+businessid+"' ");
		}
		Multiauthconfig multiauthconfig = dao.findFirst(sql.toString());
		return multiauthconfig;
	}
}
