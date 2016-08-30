package com.kdgz.uwifi.backoffice.model;


import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Businesstemplet  extends Model<Businesstemplet>{
	
	public static Businesstemplet dao = new Businesstemplet();
	
	/**
	 * 通过acId和businessid查询商家模板信息
	 * @param acid
	 * @param businessid
	 * @return
	 */ 
	public Businesstemplet selectByAcidAndBusinessId(String acid, Integer businessid){
		
		Businesstemplet businesstemplet = dao.findFirst("select * from businesstemplet b where b.acid = '"+acid+"' and b.businessid = "+businessid );
		return businesstemplet;
	}
	/**
	 * 通过商家id和acid获取模板id信息
	 * @param businessid
	 * @param acid
	 * @return
	 */
	public Businesstemplet selectTempletId(int businessid, String acid){
		
		Businesstemplet businesstemplet = dao.findFirst("select * from businesstemplet b where b.acid = '"+acid+"' and b.businessid = '"+businessid+"'");
		return businesstemplet;
	}
	
	/**
	 * 通过businessid获取商家模板信息
	 * @param businessid
	 * @return
	 * @author jason
	 */
	public Businesstemplet getBusinesstempletByBusinessId(Integer businessid){
		
		Businesstemplet businesstemplet = dao.findFirst("select * from businesstemplet b where b.businessid = "+businessid );
		return businesstemplet;
	}
}
