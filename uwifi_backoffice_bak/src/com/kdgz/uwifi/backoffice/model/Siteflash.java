package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Siteflash extends Model<Siteflash>{

	public static final Siteflash dao = new Siteflash();
	
	
	public Page<Siteflash> pageSiteflash(int pageNumber, int pageSize, int type, Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from siteflash a, businessinfo b where a.businessid = b.id");
		if(type > -1){
			sql.append(" and a.type = "+type);
		}
		if(businessid != null && businessid > -1){
			sql.append(" and a.businessid = "+businessid);
		}
		sql.append(" and a.delflag = 0 order by a.id desc");
		Page<Siteflash> sPage = paginate(pageNumber, pageSize, "select a.*, b.busname",
				sql.toString());
		
		if(sPage.getTotalPage()>0 && pageNumber>sPage.getTotalPage()){

			sPage = paginate(1, pageSize, "select a.*, b.busname",
				sql.toString());
		}
		return sPage;
	}
	
}
