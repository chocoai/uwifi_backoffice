package com.kdgz.uwifi.backoffice.model;


import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Templetpage  extends Model<Templetpage>{
	
	public static Templetpage dao = new Templetpage();
	
	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Templetpage> pageinfoList(int pageNumber, int pageSize, int templetpageId, int temptype, Integer businessid, String acid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from templetpage t where t.delflag = 0");
		if(templetpageId > 0){
			sql.append(" and t.parentid = "+templetpageId);
		}
		if(temptype > 0){
			sql.append(" and t.temptype = "+temptype);
		}
		if(businessid > 0){
			sql.append(" and t.businessid = "+businessid);
		}
		if(acid != null){
			sql.append(" and t.acid = '"+acid+"' ");
		}
		sql.append(" order by id desc");
		Page<Templetpage> templetpage = paginate(pageNumber, pageSize, "select *",
				sql.toString());
		return templetpage;
	}
	
	public Templetpage selectTempletPageByParentId(int parentid){
		
		Templetpage templetpage = dao.findFirst("select * from templetpage t where parentid = "+parentid);
		return templetpage;
	}
	
	/**
	 * 分页显示列表信息
	 * @param pageNumber
	 * @param pageSize
	 * @param temptype
	 * @param businessid
	 * @return
	 * @author jason
	 */
	public Page<Templetpage> pageTempletpage(int pageNumber, int pageSize, int temptype, Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" from templetpage t where t.delflag = 0");
		if(temptype > 0){
			sql.append(" and t.temptype = "+temptype);
		}
		if(businessid > 0){
			sql.append(" and t.businessid = "+businessid);
		}
		sql.append(" order by id desc");
		Page<Templetpage> templetpage = paginate(pageNumber, pageSize, "select *",
				sql.toString());
		
		if(templetpage.getTotalPage()>0 && pageNumber>templetpage.getTotalPage()){
			templetpage = paginate(1, pageSize, "select *",
					sql.toString());
		}
		return templetpage;
	}
	
}
