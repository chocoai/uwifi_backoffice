package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Menuinfo extends Model<Menuinfo>{

	public static final Menuinfo dao = new Menuinfo();
	
	/**
	 * 通过角色id获取菜单信息
	 * @param roleid
	 * @return
	 */
	public List<Menuinfo> selectMenuinfoByRoleId(Integer roleid){
		
		List<Menuinfo> menuinfo = dao.find("select a.rolename, b.roleid, b.menuid, c.menuname, c.link from roleinfo a, rolemenu b, menuinfo c" +
				" where a.id = b.roleid and b.menuid = c.id and b.roleid = "+roleid);
		return menuinfo;
	
	}
}
