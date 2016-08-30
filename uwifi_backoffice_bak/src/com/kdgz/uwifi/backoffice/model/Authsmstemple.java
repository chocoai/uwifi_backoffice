package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Authsmstemple extends Model<Authsmstemple>{

	public static final Authsmstemple dao = new Authsmstemple();
	
	public List<Authsmstemple> selectAuthsms(){
		
		List<Authsmstemple> list = dao.find("select * from authsmstemple");
		return list;
	}
	/**
	 * 通过id获取短信模板
	 * @param id
	 * @return
	 */
	public Authsmstemple selectSmstempleById(Integer id){
		
		Authsmstemple authsmstemple = dao.findById(id);
		return authsmstemple;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
