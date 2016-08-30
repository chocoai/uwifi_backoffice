package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Acbrand  extends Model<Acbrand>{
	
	public static Acbrand dao = new Acbrand();
	
	/**
	 * 获取品牌列表
	 * @return
	 */
	public List<Acbrand> selectAcbrandList(){
		
		List<Acbrand> list = find("select * from acbrand");
		return list;
	}
	
	/**
	 * 根据店铺id获取设备列表
	 * @param businessId
	 * @return
	 */
	public List<Acbrand> selectAcbrandListBybusinessId(int businessId){
		StringBuffer sb = new StringBuffer();
		sb.append(" select distinct a.id, a.acbrandname from acbrand a ");
		sb.append(" left join acinfo b on a.id=b.acbrandid left join businessinfo c on b.businessid = c.id ");
		sb.append(" where c.id=");
		sb.append(businessId);
		
		List<Acbrand> list = find(sb.toString());
		return list;
	}
}
