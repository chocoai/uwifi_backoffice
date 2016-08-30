package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Construtinfo  extends Model<Construtinfo>{
	
	public static Construtinfo dao = new Construtinfo();
	
	/**
	 * 获取施工方列表信息
	 * @return
	 */
	public List<Construtinfo> selectConstrutinfoList(){
		
		List<Construtinfo> list = find("select * from construtinfo");
		return list;
	}
}
