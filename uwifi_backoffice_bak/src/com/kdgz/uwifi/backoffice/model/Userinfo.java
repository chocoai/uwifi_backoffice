package com.kdgz.uwifi.backoffice.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Userinfo extends Model<Userinfo> {

	/**
	 * 
	 */
	public static final Userinfo dao = new Userinfo();

	/**
	 * 获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	public Userinfo getUserInfo(String username) {

		return dao.findFirst(
				"select * from userinfo where loginname = ? and status= '1'",
				new Object[] { username });

	}

	/**
	 * 获取用户列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Userinfo> paginate(int pageNumber, int pageSize) {
		Page<Userinfo> pageInfo = paginate(pageNumber, pageSize, "select *",
				"from userinfo order by id desc");
		if(pageInfo.getTotalPage()>0 && pageNumber>pageInfo.getTotalPage()){
			pageInfo = paginate(1, pageSize, "select *",
					"from userinfo order by id desc");
		}
		return pageInfo;
	}
	
	public Page<Userinfo> pageUserinfo(int pageNumber, int pageSize, String channelName, Integer roleid, Integer status, String curRoleid, String userid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from userinfo a, roleinfo b where a.roleid not in (1, 2) and  a.roleid = b.id");
		
		if(StrKit.notBlank(channelName)){
			sql.append(" and a.channelname like '%"+channelName+"%'");
		}
		if(roleid != null && roleid > -1){
			sql.append(" and a.roleid = "+roleid);
		}
		if(status != null && status > -1){
			sql.append(" and a.checkstatus = 1 and a.status = "+status);
		}
		if(curRoleid.equals(Constants.ROLE_GOLDCHANNEL) || curRoleid.equals(Constants.ROLE_SILVERCHANNEL)){
			sql.append(" and a.pid = "+userid);
		}
		sql.append(" and a.delflag = 0 order by a.checkstatus, a.addtime desc");
		
		Page<Userinfo> sPage = paginate(pageNumber, pageSize, "select a.id, a.channelname, a.roleid, a.pid, " +
				"a.pname, a.contacts, a.phone, a.qq, a.province, a.city, a.address," +
				" a.loginname, a.password, a.status, a.checkstatus, b.rolename,(select b.mc from sysxzqh b where a.province=b.bm) sf,(select b.mc from sysxzqh b where a.city=b.bm) cs ",
				sql.toString());
		
//		if(sPage.getTotalPage()>0 && pageNumber>sPage.getTotalPage()){
//			sPage = paginate(1, pageSize, "select a.*, b.busname",
//					sql.toString());
//		}
		return sPage;
	}

	/**
	 * 删除用户
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteUser(int id) {

		return dao.deleteById(id);
	}
	
	/**
	 * 验证是否已经存在的用户
	 * 
	 * @param whtype
	 * @param controltype
	 * @param businessid
	 * @return
	 */
	public long countExistUser(String loginname) {

		long count = Db
				.queryLong(
						"SELECT count(1) From userinfo where delflag = 0 and loginname = ?",
						new Object[] { loginname });

		return count;
	}
	
	public long countExistChannelName(String channelname) {

		long count = Db
				.queryLong(
						"SELECT count(1) From userinfo where delflag = 0 and channelname = ?",
						new Object[] { channelname });

		return count;
	}
	
	public long countExistBusinessName(String businessname) {

		long count = Db
				.queryLong(
						"SELECT count(1) From userinfo where delflag = 0 and businessname = ?",
						new Object[] { businessname });

		return count;
	}
	
	/**
	 * 获取商家用户
	 * @param pageNumber
	 * @param pageSize
	 * @param flage
	 * @return
	 * @author jason
	 */
	public Page<Userinfo> pageUserinfo(int pageNumber, int pageSize, String flage, String channelName, Integer roleid, Integer status, String curRoleid, String userid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from userinfo a where a.roleid="+Constants.ROLE_BUSINESSUSER);
		if(StrKit.notBlank(channelName)){
			sql.append(" and a.businessname like '%"+channelName.trim()+"%'");
		}
		if(roleid != null && roleid > -1){
			sql.append(" and a.roleid = "+roleid);
		}
		if(status != null && status > -1){
			sql.append(" and a.checkstatus = 1 and a.status = "+status);
		}
		if(userid != null){
			sql.append(" and a.pid = "+userid);
		}
		sql.append(" and a.delflag = 0 order by a.checkstatus, a.addtime desc");
		
		Page<Userinfo> sPage = paginate(pageNumber, pageSize, "select a.*,(select b.mc from sysxzqh b where a.province=b.bm) sf,(select b.mc from sysxzqh b where a.city=b.bm) cs ",
				sql.toString());
		
		return sPage;
	}
	
	
	/**
	 * 获取渠道商家用户
	 * @param pageNumber
	 * @param pageSize
	 * @param flage
	 * @return
	 * @author jason
	 */
	public Page<Userinfo> pageUserinfoChannel(int pageNumber, int pageSize, String flage, String channelName, Integer channelid, Integer status, String curRoleid, String userid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from userinfo a where a.roleid="+Constants.ROLE_BUSINESSUSER);
		if(StrKit.notBlank(channelName)){
			sql.append(" and a.businessname like '%"+channelName.trim()+"%'");
		}
		if(status != null && status > -1){
			sql.append(" and a.checkstatus = 1 and a.status = "+status);
		}
		if(curRoleid != null && curRoleid.equals(Constants.ROLE_ADMIN)){
			if(channelid != null && channelid > -1){
				sql.append(" and a.pid = "+channelid);
			}else{
				sql.append(" and a.pid != "+userid);
			}
		}
		if(curRoleid != null && curRoleid.equals(Constants.ROLE_GOLDCHANNEL)){
			List<Userinfo> list = dao.find("select id from userinfo where pid = '"+userid+"' and roleid = "+Constants.ROLE_SILVERCHANNEL);
			if(list != null && list.size() > 0){
				String uid = "";
				for(int i = 0; i < list.size(); i++){
					Userinfo userinfo = list.get(i);
					uid += userinfo.getInt("id")+",";
				}
				uid = uid.substring(0, uid.lastIndexOf(","));
				if(channelid != null && channelid > -1){
					sql.append(" and a.pid = "+channelid);
				}else{
					sql.append(" and a.pid in ("+uid+") ");
				}
			}else{
				return null;
			}
		}
		sql.append(" and a.delflag = 0 order by a.checkstatus, a.addtime desc");
		
		Page<Userinfo> sPage = paginate(pageNumber, pageSize, "select a.*,(select b.mc from sysxzqh b where a.province=b.bm) sf,(select b.mc from sysxzqh b where a.city=b.bm) cs ",
				sql.toString());
		
		return sPage;
	}
	
	/**
	 * 通过上级id查找上级渠道名称
	 * @param pid
	 * @return
	 */
	public Userinfo selectChannelNameByPid(Integer pid){
		
		Userinfo userinfo = dao.findFirst("select a.id, a.channelname,a.roleid from userinfo a where id = "+pid);
		return userinfo;
	}
	/**
	 * 根据不同的角色获取上级渠道名称
	 * @param roleid
	 * @return
	 * @author jason
	 */
	public List<Userinfo> getUserinfoList(String roleid){
		List<Userinfo> list = new ArrayList<Userinfo>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from userinfo");
		if(Constants.ROLE_ADMIN.equals(roleid)){
			sql.append(" where 1=1 and roleid="+Constants.ROLE_ADMIN);
		}
		if(Constants.ROLE_GOLDCHANNEL.equals(roleid)){
			sql.append(" where 1=1 and roleid in ("+Constants.ROLE_ADMIN+")");
		}
		if(Constants.ROLE_SILVERCHANNEL.equals(roleid)){
			sql.append(" where 1=1 and roleid in ("+Constants.ROLE_ADMIN+","+Constants.ROLE_GOLDCHANNEL+")");
		}
		if(Constants.ROLE_BUSINESSUSER.equals(roleid)){
			sql.append(" where 1=1 and roleid in ("+Constants.ROLE_ADMIN+","+Constants.ROLE_GOLDCHANNEL+","+Constants.ROLE_SILVERCHANNEL+")");
		}
		sql.append(" order by id ");
		list = dao.find(sql.toString());
		
		return list;
	}
	
	/**
	 * 根据不同角色获取下级渠道
	 * @param roleid
	 * @return
	 * @author jason
	 */
	public List<Userinfo> getChannelList(String roleid, String province, String city, String id){
		List<Userinfo> list = new ArrayList<Userinfo>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from userinfo");
		if(Constants.ROLE_ADMIN.equals(roleid)){
			sql.append(" where roleid in ("+Constants.ROLE_ADMIN+","+Constants.ROLE_GOLDCHANNEL+","+Constants.ROLE_SILVERCHANNEL+")");
		}
		if(Constants.ROLE_GOLDCHANNEL.equals(roleid)){
			sql.append(" where roleid in ("+Constants.ROLE_GOLDCHANNEL+","+Constants.ROLE_SILVERCHANNEL+")");
			sql.append(" and province = "+province + " and city = "+city);
		}
		if(Constants.ROLE_SILVERCHANNEL.equals(roleid)){
			sql.append(" where roleid = "+Constants.ROLE_SILVERCHANNEL);
			sql.append(" and province = "+province + " and city = "+city);
		}
		if(Constants.ROLE_BUSINESSUSER.equals(roleid)){
			sql.append(" where id = (select pid from userinfo where id="+id+")");
		}
		sql.append(" order by id ");
		list = dao.find(sql.toString());
		
		return list;
	}
	/**
	 * 根据上级渠道id修改下级渠道pid的pname
	 * @param id
	 */
	public void updatePnameByidAndChannelname(Integer id, String channelname){
		
		List<Userinfo> list = Userinfo.dao.find("select * from userinfo where roleid != 2 and delflag = 0 and pid = "+id);
		if(list != null && list.size() > 0){
			for(int i =0; i< list.size(); i++){
				Userinfo userinfo = list.get(i);
				userinfo.set("pname", channelname);
				userinfo.update();
			}
			
		}
		
	}
	/**
	 * 删除当前渠道时，修改该渠道的下级渠道的角色
	 * 为该渠道的角色
	 * @param id
	 */
	public void updatePnameByidAndRoleid(Integer id, Integer pid, String pname){
		
		List<Userinfo> list = Userinfo.dao.find("select * from userinfo where  delflag = 0 and pid = "+id);
		if(list != null && list.size() > 0){
			for(int i =0; i< list.size(); i++){
				Userinfo userinfo = list.get(i);
				userinfo.set("pid", pid);
				userinfo.set("pname", pname);
				userinfo.update();
			}
			
		}
		
	}
	/**
	 * 根据登录用户的角色和用户id查询该用户的pid
	 * 通过pid查询该用户下的所有渠道用户
	 * @param roleid
	 * @param userid
	 */
	public List<Userinfo> selectchannelNameByRoleidAndUid(String curRoleid, String userid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select a.pid from userinfo a where a.roleid="+Constants.ROLE_BUSINESSUSER);
		if(curRoleid != null && curRoleid.equals(Constants.ROLE_ADMIN)){
			sql.append(" and a.pid != "+userid);
		}
		if(curRoleid != null && curRoleid.equals(Constants.ROLE_GOLDCHANNEL)){
			List<Userinfo> list = dao.find("select id from userinfo where pid = '"+userid+"' and roleid = "+Constants.ROLE_SILVERCHANNEL);
			String uid = "";
			if(list != null && list.size() > 0){
				for(int i = 0; i < list.size(); i++){
					Userinfo userinfo = list.get(i);
					uid += userinfo.getInt("id")+",";
				}
				uid = uid.substring(0, uid.lastIndexOf(","));
				sql.append(" and a.pid in ("+uid+") ");
			}else{
				//没有下级渠道
				return null;
			}
		}
		sql.append(" and a.delflag = 0 order by a.id desc");
		String pids = "";
		List<Userinfo> list = dao.find(sql.toString());
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				Userinfo userinfo = list.get(i);
				
				pids += userinfo.getInt("pid") + ",";
			}
			pids = pids.substring(0, pids.lastIndexOf(","));
		}
		List<Userinfo> listP = null;
		if(StrKit.notBlank(pids)){
			 listP = dao.find("select id, channelname from userinfo where delflag = 0 and id in ("+pids+")");
		}
		
		return listP;
	}
	
	public List<Userinfo> selectchannelNameByRoleidAndUidForRouter(String curRoleid, String userid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select a.pid from userinfo a where a.roleid="+Constants.ROLE_BUSINESSUSER);
		if(curRoleid != null && curRoleid.equals(Constants.ROLE_ADMIN)){
			sql.append(" and a.pid != "+userid);
		}
		if(curRoleid != null && curRoleid.equals(Constants.ROLE_GOLDCHANNEL)){
			List<Userinfo> list = dao.find("select id from userinfo where pid = '"+userid+"' and roleid = "+Constants.ROLE_SILVERCHANNEL);
			if(list != null && list.size() > 0){
				String uid = "";
				for(int i = 0; i < list.size(); i++){
					Userinfo userinfo = list.get(i);
					uid += userinfo.getInt("id")+",";
				}
				uid = uid.substring(0, uid.lastIndexOf(","));
				sql.append(" and a.pid in ("+uid+") ");
			}
		}
		sql.append(" and a.delflag = 0 order by a.id desc");
		String pids = "";
		List<Userinfo> list = dao.find(sql.toString());
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				Userinfo userinfo = list.get(i);
				
				pids += userinfo.getInt("pid") + ",";
			}
			pids = pids.substring(0, pids.lastIndexOf(","));
		}
		List<Userinfo> listP = null;
		if(StrKit.notBlank(pids)){
			String sqlChannelName = "";
			if(curRoleid.equals(Constants.ROLE_ADMIN)){
				sqlChannelName = "select id, channelname from userinfo where delflag = 0 and id in ("+Constants.ROLE_ADMIN+","+pids+")";
			}else{
				sqlChannelName = "select id, channelname from userinfo where delflag = 0 and id in ("+pids+")";
			}
			listP = dao.find(sqlChannelName);
		}
		
		return listP;
	}
}
