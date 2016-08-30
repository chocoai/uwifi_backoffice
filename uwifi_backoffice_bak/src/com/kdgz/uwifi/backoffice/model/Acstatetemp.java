package com.kdgz.uwifi.backoffice.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@SuppressWarnings("serial")
public class Acstatetemp  extends Model<Acstatetemp>{
	
	public static Acstatetemp dao = new Acstatetemp();
	
	private String status;
	
	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Acstatetemp> pageinfoList(int pageNumber, int pageSize, String busname, String construtname, String businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, acstatetemp c, acauth d ");
		sql.append("where a.businessid = b.id and a.acid = c.acid and a.acid = d.acid");
		if(StrKit.notBlank(busname)){
			sql.append(" and b.busname like '%"+busname+"%' ");
		}
		if(StrKit.notBlank(construtname)){
			sql.append(" and c.construtname like '%"+construtname+"%'");
		}
		if(StrKit.notBlank(businessid)){
			sql.append(" and b.id in ("+businessid+")");
		}
		Page<Acstatetemp> acstatetempList = paginate(pageNumber, pageSize, "select a.acid, a.eqptssid, b.busname, c.lastpingtime, d.authtype",
				sql.toString());
		if(acstatetempList != null && acstatetempList.getList().size() > 0)
			for(int i = 0; i < acstatetempList.getList().size(); i++){
				Acstatetemp acstatetemp = acstatetempList.getList().get(i);
					long pingL = acstatetemp.getTimestamp("lastpingtime").getTime();
					
					long nowL = System.currentTimeMillis();
					
					if(nowL - pingL > 60*15*1000) {
						
						acstatetemp.setStatus("离线");
					} else {
						acstatetemp.setStatus("在线");
					}
					
			}
		return acstatetempList;
	}
	
	/**
	 * 删除Acconfig信息
	 * @param acid
	 * @return
	 */
	public boolean deleteAcstatetempByAcid(String acid){
		
		Record acstatetemp = Db.findFirst("select * from acstatetemp where acid = ?", acid);
		
		if(acstatetemp != null) {
			return Db.delete("acstatetemp", acstatetemp);
		}
		return false;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
