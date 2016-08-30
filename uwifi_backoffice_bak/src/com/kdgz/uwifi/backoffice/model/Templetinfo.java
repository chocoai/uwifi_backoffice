package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@SuppressWarnings("serial")
public class Templetinfo  extends Model<Templetinfo>{
	
	public static Templetinfo dao = new Templetinfo();
	
	/**
	 * 获取品牌列表
	 * @return
	 */
	public List<Templetinfo> selectAcbrandList(){
		
		List<Templetinfo> list = find("select * from acbrand");
		return list;
	}
	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Templetinfo> pageinfoList(int pageNumber, int pageSize, int temptype) {
		StringBuffer sql = new StringBuffer();
		sql.append("from templetinfo t where 1=1");
		if(temptype > 0){
			sql.append(" and t.temptype = "+temptype);
		}
//		sql.append("order by id desc");
		Page<Templetinfo> templetinfo = paginate(pageNumber, pageSize, "select *",
				sql.toString());
		return templetinfo;
	}
	
	/**
	 * 通过businessid获取店铺选中的模板信息
	 * @param businessid
	 * @return
	 * @author jason
	 */
	public Templetinfo getSeclectedTemplet(Integer businessid){
		
		Templetinfo templetinfo = dao.findFirst("select a.* from templetinfo a,businesstemplet b where a.id=b.authid and b.businessid="+businessid );
		return templetinfo;
	}
}
