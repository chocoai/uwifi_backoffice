package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Userbiz extends Model<Userbiz>{

	public static final Userbiz dao = new Userbiz();
	
	/**
	 * 通过用户id获取该用户所拥有的商家
	 * @param userid
	 */
	public List<Userbiz> selectBusinessByUid(Integer userid){
		
		List<Userbiz>  userbiz = dao.find("select * from userbiz where businessid <> 0 and userid <> businessid and userid = "+userid);
		return userbiz;
	}
}
