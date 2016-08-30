package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Actypeinfo  extends Model<Actypeinfo>{
	
	public static Actypeinfo dao = new Actypeinfo();
	
	/**
	 * 获取品牌型号列表通过品牌id
	 * @return
	 */
	public List<Actypeinfo> selectActpeinfoListById(String acbrandId){
		
		List<Actypeinfo> list = find("select * from actypeinfo where acbrandid = "+acbrandId);
		return list;
	}
	
	public Actypeinfo selectActypeinfoByFirmwareId(int firmwareid){
		
		Actypeinfo actypeinfo = dao.findFirst("select * from actypeinfo where firmwareid = "+firmwareid);
		return actypeinfo;
	}
}
