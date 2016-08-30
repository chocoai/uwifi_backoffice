package com.kdgz.uwifi.backoffice.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Sysxzqh extends Model<Sysxzqh>{

	public static final Sysxzqh dao = new Sysxzqh();
	
	public List<Sysxzqh> getProvinceList() {
		List<Sysxzqh> list = new ArrayList<Sysxzqh>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from sysxzqh where cj=1");
		sql.append(" order by bm ");
		list = dao.find(sql.toString());
		return list;

	}
	
	public List<Sysxzqh> getCityList(String provinceId){
		List<Sysxzqh> list = new ArrayList<Sysxzqh>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from sysxzqh");
		if (StrKit.notBlank(provinceId)) {
			sql.append(" where 1=1 and bm like '" + provinceId + "__'");
		}else{
			sql.append(" where 1=2 ");
		}
		sql.append(" order by bm ");
		list = dao.find(sql.toString());
		return list;
	}
}
