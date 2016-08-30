package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Siteclassify extends Model<Siteclassify>{

	public static final Siteclassify dao = new Siteclassify();
	
	
	public Page<Siteclassify> pageSiteclassify(int pageNumber, int pageSize, Integer fid, Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from siteclassify a, businessinfo b where a.businessid = b.id");
		
		if(fid != null && fid > -1){
			sql.append(" and a.fid = "+fid);
		}
		if(businessid != null && businessid > -1){
			sql.append(" and a.businessid = "+businessid);
		}
		
		sql.append(" and a.delflag = 0 order by a.id desc");
		Page<Siteclassify> sPage = paginate(pageNumber, pageSize, "select a.*, b.busname",
				sql.toString());
		
		if(sPage.getTotalPage()>0 && pageNumber>sPage.getTotalPage()){
			sPage = paginate(1, pageSize, "select a.*, b.busname",
					sql.toString());
		}
		return sPage;
	}
	/**
	 * 查询分类列表信息
	 * @return
	 */
	public List<Siteclassify> selectclassifyList(Integer businessid){
		
		
		List<Siteclassify> list = dao.find("select * from siteclassify where delflag = 0 and businessid ="+businessid);
		return list;
	}
	
	/**
	 * 获取公告id
	 * @param businessid
	 * @return
	 * @author jason
	 */
	public List<Siteclassify> getAdidList(String businessid){
		StringBuffer sql = new StringBuffer();
		sql.append(" select id,name,addtime,updatetime,from_unixtime(addtime) addtime1,from_unixtime(updatetime) updatetime1,delflag from siteclassify ");
		if(businessid.length() > 0){
			sql.append(" where businessid = "+Integer.parseInt(businessid));
		}
		sql.append(" order by id ");
		List<Siteclassify> list = dao.find(sql.toString());
		return list;
	}
	/**
	 * 通过父id查找分类信息
	 * @param fid
	 * @return
	 */
	public List<Siteclassify> selectsubByFid(Integer fid){
		
		
		List<Siteclassify> list = dao.find("select * from siteclassify where delflag = 0 and fid ="+fid);
		return list;
	}
	
	
}
