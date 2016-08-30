package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Firmware extends Model<Firmware>{

	public static Firmware dao = new Firmware();
	
	
	public List<Firmware> getFirmwareList(){
		
		List<Firmware> list = dao.find("select * from firmware order by id desc");
		return list;
	}
	
	public long getFirmwareByType(String type){
		
		long count = Db.queryLong("select count(1) from firmware where type = ? ", new Object[] { type });
		return count;
		
		
	}
	public long getFirmwareByType(String type, int id){
		
		long count = Db.queryLong("select count(1) from firmware where type = ? and id <> ?", new Object[] { type, id });
		return count;
		
		
	}
}
