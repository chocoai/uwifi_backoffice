package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

@SuppressWarnings("serial")
public class Acconfig  extends Model<Acconfig>{
	
	public static Acconfig dao = new Acconfig();
	
	/**
	 * 获取ACconfig信息
	 * @return
	 */
	public Acconfig selectAcconfigByAcid(String acid){
		 
		 Acconfig acconfig = findFirst("select * from acconfig where acid = '"+acid+"' ");
		 return acconfig;
	}
	
	/**
	 * 删除Acconfig信息
	 * @param acid
	 * @return
	 */
	public boolean deleteAcconfigByAcid(String acid){
		
		Record acconfig = Db.findFirst("select * from acconfig where acid = ?", acid);
		
		if(acconfig != null) {
			return Db.delete("acconfig", acconfig);
		}
		return false;
	}
}
