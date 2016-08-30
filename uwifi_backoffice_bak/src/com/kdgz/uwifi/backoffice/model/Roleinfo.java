package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Roleinfo extends Model<Roleinfo>{

	public static final Roleinfo dao = new Roleinfo();
	
	/**
	 * 通过用户id获取角色信息
	 * @return
	 */
	public Roleinfo selectRoleinfoByUserId(Integer userid){
		
		Roleinfo roleinfo = dao.findFirst("select a.loginname, b.userid, b.roleid, c.id, c.pid, c.rolename from userinfo a, userrole b, roleinfo c" +
				" where a.id = b.userid and b.roleid = c.id and a.id = "+userid);
		return roleinfo;
	}
	/**
	 * 通过pid过滤渠道级别关系
	 * @param pid
	 * @return
	 */
	public List<Roleinfo> selectRoleListByPid(String roleid){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from roleinfo where 1=1 ");
		if(roleid != null && roleid.equals("1")){	//管理员 过滤自己
			sql.append(" and pid != 0");
		}else{
			sql.append(" and pid = "+roleid);
		}
		
		List<Roleinfo> rolelist = dao.find(sql.toString());
		
		return rolelist;
	}
	
	public Roleinfo selectRoleById(Integer roleid){
		
		if(roleid == null){
			return null;
		}
		Roleinfo roleinfo = dao.findFirst("select * from roleinfo where id = "+roleid);
		return roleinfo;
	}
}
