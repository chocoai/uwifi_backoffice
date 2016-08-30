package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Cuser extends Model<Cuser>{

	public static final Cuser dao = new Cuser();
	
	
	public Page<Cuser> pageCuser(int pageNumber, int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("from cuser a, businessinfo b where a.businessid = b.id");
		
		sql.append(" and a.delflag = 0 order by a.id desc");
		Page<Cuser> sPage = paginate(pageNumber, pageSize, "select a.id, a.businessid, a.nickname, " +
				"a.phoneno, a.password, from_unixtime(a.addtime) addtime, from_unixtime(a.updatetime) updatetime, a.delflag , b.busname",
				sql.toString());
		
		if(sPage.getTotalPage()>0 && pageNumber>sPage.getTotalPage()){
			sPage = paginate(pageNumber, pageSize, "select a.id, a.businessid, a.nickname, " +
					"a.phoneno, a.password, from_unixtime(a.addtime) addtime, from_unixtime(a.updatetime) updatetime, a.delflag , b.busname",
					sql.toString());
		}
		
		return sPage;
	}


}
