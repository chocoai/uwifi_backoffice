package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Apply extends Model<Apply>{

	public static Apply dao = new Apply();
	
	/**
	 * 获取列表
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Apply> pageinfoList(int pageNumber, int pageSize, String appName) {
		StringBuffer sql = new StringBuffer();
		sql.append("from apply where delflag = 0");
		if(StrKit.notBlank(appName)){
			sql.append(" and appname like '%"+appName+"%' ");
		}
		sql.append(" order by id desc");
		Page<Apply> apply = paginate(pageNumber, pageSize,
				"select * ",
				sql.toString());
		return apply;
	}
	
	
	public List<Apply> getAppList(Integer busid){
		StringBuffer sql = new StringBuffer();
		sql.append("select a.*,b.bindflag from apply a left join businessapp b on a.id = b.appid");
		sql.append(" where a.delflag=0 and b.businessid="+busid);
		sql.append(" order by a.id desc");
		
		List<Apply> list = dao.find(sql.toString());
		return list;
	}
	
	public Page<Apply> getAppListByPage(int pageNumber, int pageSize, Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" from apply a left join businessapp b on a.id = b.appid ");
		sql.append(" where a.delflag=0 ");
		sql.append(" and b.businessid = " + businessid);
		sql.append(" order by a.id desc");
		StringBuffer select = new StringBuffer();
		select.append("select a.*,b.bindflag ");
		Page<Apply> apply_list = paginate(pageNumber, pageSize, select.toString(), sql.toString());
		if(apply_list.getTotalPage()>0 && pageNumber>apply_list.getTotalPage()){
			apply_list = paginate(1, pageSize, select.toString(), sql.toString());
		}
		return apply_list;
	}
	
	public List<Apply> getBindedAppList(Integer busid){
		StringBuffer sql = new StringBuffer();
		sql.append("select a.*,b.bindflag from apply a, businessapp b where a.id = b.appid");
		sql.append(" and b.businessid="+busid+" and b.bindflag=1 and a.delflag=0 ");
		sql.append(" order by a.id desc");
		
		List<Apply> list = dao.find(sql.toString());
		return list;
	}
	
	public Page<Apply> getBindedAppListByPage(int pageNumber, int pageSize, Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" from apply a, businessapp b where a.id = b.appid ");
		sql.append(" and b.bindflag=1 and a.delflag=0 ");
		sql.append(" and b.businessid = " + businessid);
		sql.append(" order by a.id desc");
		StringBuffer select = new StringBuffer();
		select.append("select a.*,b.bindflag ");
		Page<Apply> apply_list = paginate(pageNumber, pageSize, select.toString(), sql.toString());
		
		if(apply_list.getTotalPage()>0 && pageNumber>apply_list.getTotalPage()){
			apply_list = paginate(1, pageSize, select.toString(), sql.toString());
		}
		return apply_list;
	}
	
	public List<Apply> getAppIdList(){
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id from apply a ");
		sql.append(" where a.delflag=0 ");
		sql.append(" order by a.id");
		
		List<Apply> list = dao.find(sql.toString());
		return list;
	}
}
