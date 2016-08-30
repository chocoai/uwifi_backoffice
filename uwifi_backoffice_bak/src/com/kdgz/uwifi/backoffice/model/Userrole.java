package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Userrole extends Model<Userrole>{

	public static final Userrole dao = new Userrole();
	
	/**
	 * 查找Acauth信息通过ACId
	 * @param acid
	 * @return
	 */
	public Userrole selectAcauthByAcId(String acid){
		
		Userrole acauth = dao.findFirst("select * from acauth where acid = '"+acid+"' ");
		return acauth;
	}
	
	public List<Userrole> selectUserroleByPid(String pid){
		
		if(pid == null){
			return null;
		}
		
		List<Userrole> list = dao.find("select a.userid, a.roleid, b.id,b.pid, b.channelname " +
				"from userrole a, userinfo b where b.delflag = 0 and a.userid = b.id and b.roleid in ("+pid+")");
		return list;
	}
	
	public Userrole selectUserroleByUid(Integer userid){
		Userrole userrole  = dao.findFirst("select * from userrole where userid ="+userid);
		return userrole;
	}
}
