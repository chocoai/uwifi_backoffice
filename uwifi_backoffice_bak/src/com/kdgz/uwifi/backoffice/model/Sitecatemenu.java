package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Sitecatemenu extends Model<Sitecatemenu>{

	public static final Sitecatemenu dao = new Sitecatemenu();
	
	public Page<Sitecatemenu> pageSitecatemenu(int pageNumber, int pageSize, Integer fid, Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from sitecatemenu a, businessinfo b where a.businessid = b.id");
		
		if(fid != null && fid > -1){
			sql.append(" and a.fid = "+fid);
		}
		if(businessid != null && businessid > -1){
			sql.append(" and a.businessid = "+businessid);
		}
		
		sql.append(" and a.delflag = 0 order by a.id desc");
		Page<Sitecatemenu> sPage = paginate(pageNumber, pageSize, "select a.*, b.busname",
				sql.toString());
		
		if(sPage.getTotalPage()>0 && pageNumber>sPage.getTotalPage()){
			sPage = paginate(1, pageSize, "select a.*, b.busname",
					sql.toString());
		}
		return sPage;
	}
	
	/**
	 * 通过父id查找菜单信息
	 * @param fid
	 * @return
	 */
	public List<Sitecatemenu> selectsubByFid(Integer fid){
		
		
		List<Sitecatemenu> list = dao.find("select * from sitecatemenu where delflag = 0 and fid ="+fid);
		return list;
	}
}
