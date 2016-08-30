package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Syscode extends Model<Syscode>{

	public static final Syscode dao = new Syscode();
	
	public List<Syscode> syscodeList(){
		
		List<Syscode> list = dao.find("select * from syscode where dimcode='authtype'");
		return list;
	}
}
