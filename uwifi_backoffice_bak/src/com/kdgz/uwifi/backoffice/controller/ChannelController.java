package com.kdgz.uwifi.backoffice.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ChannelInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Operatorlog;
import com.kdgz.uwifi.backoffice.model.Roleinfo;
import com.kdgz.uwifi.backoffice.model.Sysxzqh;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.model.Userrole;
import com.kdgz.uwifi.backoffice.util.CommonUtil;

@Before(ChannelInterceptor.class)
public class ChannelController extends BaseController{
	
	public void index(){
		String userid = getSessionAttr("userid")+"";
		String roleid = getSessionAttr("roleid");
		
//		Roleinfo roleinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(roleid));
		List<Roleinfo> listRole = Roleinfo.dao.selectRoleListByPid(roleid);
		setAttr("listRole", listRole);
		
		String searchName = getPara("searchName");
		Integer searchRole = getParaToInt("searchRole");
		Integer searchStatus =  getParaToInt("searchStatus");
		setAttr("channelinfoPage", Userinfo.dao.pageUserinfo(getParaToInt(0, 1), 10,searchName, searchRole, searchStatus,roleid,userid));
		setAttr("roleid", roleid);
		setAttr("admin", Constants.ROLE_ADMIN);
		//动态查询
		setAttr("searchRole", searchRole);
		setAttr("searchStatus", searchStatus);
		setAttr("searchName", searchName);
		flashRender("/admin/channel/channel_manage/list_channel.html");
		
	}
	
	public void channelIndex(){
		
		String userRole = getSessionAttr("roleid");
		String userid = getSessionAttr("userid")+"";
		String province = getSessionAttr("province");
		String city = getSessionAttr("city");
		
		String queryCondition = getPara("channelORbusname","");
		
		//数据统计
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_ADMIN+" THEN 1 ELSE 0 END) AS 'admin', ");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_GOLDCHANNEL+" THEN 1 ELSE 0 END) AS 'goldchannel', ");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_SILVERCHANNEL+" THEN 1 ELSE 0 END) AS 'silverchannel', ");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_BUSINESSUSER+" THEN 1 ELSE 0 END) AS 'businessuser' ");
		sb.append("FROM userinfo ");
		if(!userRole.equals(Constants.ROLE_ADMIN)){
			sb.append("where province='"+province+"' and city='"+city+"'");
		}
		Record record = Db.findFirst(sb.toString());
		if (record != null) {
			setAttr("goldchannel", record.get("goldchannel"));
			setAttr("silverchannel", record.get("silverchannel"));
			setAttr("businessuser", record.get("businessuser"));
		}
		long onlineDeviceCount=0;
		onlineDeviceCount = Businessinfo.dao.countOnlineDeviceNum5(userRole,userid);
		setAttr("onlineDeviceCount", onlineDeviceCount);
		setAttr("userRole", userRole);
		
		//商家过期提醒
		List<Operatorlog> operatorList = Operatorlog.dao.getOperatorlogList(userRole, userid, province, city);
		setAttr("operatorList", operatorList);
		
		//渠道监控
		setAttr("channelORbusname", queryCondition);
		setAttr("operatorlogPage",Operatorlog.dao.operatorlogList(getParaToInt(0, 1), 10, queryCondition, userRole, userid, province, city));
		setAttr("admin", Constants.ROLE_ADMIN);
		setAttr("gold", Constants.ROLE_GOLDCHANNEL);
		setAttr("silver", Constants.ROLE_SILVERCHANNEL);
		setAttr("business", Constants.ROLE_BUSINESSUSER);
		render("/admin/site/channel_index.html");
	}

	
	/**
	 * 添加渠道用户
	 */
	public void addChannel(){

		Userinfo user = new Userinfo();
		String roleid = getSessionAttr("roleid");
		String channelname = getSessionAttr("channelName");
		Integer uid = getSessionAttr("userid");
//		Roleinfo roleinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(roleid));
		List<Roleinfo> listRole = Roleinfo.dao.selectRoleListByPid(roleid);
		setAttr("listRole", listRole);
		setAttr("roleid", roleid);
		setAttr("channelname", channelname);
		setAttr("uid", uid);
		if (getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);

			Data rst = new Data();
			
			user.set("loginname", param.getStr("loginname"));
			String salt = CommonUtil.getRandomString(8);
			user.set("password", CommonUtil.generateSaltPassword(
					param.getStr("password"), salt));
			user.set("salt", salt);

			user.set("rolecode", 0);
			user.set("channelname", param.getStr("channelname"));
			user.set("roleid", param.getInt("roleid"));
			user.set("pid", param.getInt("pid"));
			Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid(param.getInt("pid"));
			user.set("pname", userinfo2.getStr("channelname"));
			user.set("contacts", param.getStr("contacts"));
			user.set("phone", param.getStr("phone"));
			user.set("qq", param.getStr("qq"));
			user.set("province", param.getStr("province"));
			user.set("city", param.getStr("city"));
			user.set("address", param.getStr("address"));
			if(roleid.equals(Constants.ROLE_ADMIN)){
				user.set("status", 1);		//启用
				user.set("checkstatus", 1);			//默认是已审核状态
				
			}else{
				user.set("status", 1);
				user.set("checkstatus", 0);	//默认是未审核状态
			}
			
			
			Date now = new Date();
			user.set("addtime", now);
			user.set("updatetime", now);
			if(user.save()){
				int userid = user.getInt("id");
				Record role = new Record();
				role.set("userid", userid);
				role.set("roleid", param.getInt("roleid"));		//金牌渠道或者银牌渠道
				Db.save("userrole", role); // 保存渠道用户所具有的权限
				
				rst.setStatus(Constants.OPERATION_SUCCEED);
			    rst.setMsg("添加渠道商成功！");
	   //添加操作记录
			 Operatorlog operatorlog = new Operatorlog();
			 operatorlog.set("logtype", Constants.LOGTYPEADD);		
			 operatorlog.set("pid", getSessionAttr("roleid"));
			 operatorlog.set("roleid", param.getInt("roleid"));
			 operatorlog.set("userid", userid);
			 String operName = getSessionAttr("loginName");
			 operatorlog.set("operator", operName);
			 operatorlog.set("username", param.getStr("channelname"));
			 operatorlog.set("operatortime", now);   
			 operatorlog.set("delflag", 0); 
			 operatorlog.save();
			    
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);	
				rst.setMsg("添加渠道商失败！");
			}
			setFlashData(rst);
			redirect("/channelManager");
			
		}else{
			getProvince();
			setAttr("roleid", roleid);
			render("/admin/channel/channel_manage/add_channel.html");
			
		}
	}
	
	
	public void editChannel(){
			
			Integer id = getParaToInt("id");
			
			Userinfo userinfo = Userinfo.dao.findById(id);
			String roleid = getSessionAttr("roleid");
			String channelname = getSessionAttr("channelName");
			Integer uid = getSessionAttr("userid");
//			Roleinfo roleinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(roleid));
			List<Roleinfo> listRole = Roleinfo.dao.selectRoleListByPid(roleid);
			setAttr("listRole", listRole);
			setAttr("roleid", roleid);
			setAttr("channelname", channelname);
			setAttr("uid", uid);
			
			int currentPage = getParaToInt("pageNo",1);//当前页码
			
			if(getRequest().getMethod().equals("POST")) {
				Userinfo param = getModel(Userinfo.class);
				userinfo.set("channelname", param.getStr("channelname"));
				userinfo.set("roleid", param.getInt("roleid"));
				userinfo.set("pid", param.getInt("pid"));
				//更新父渠道名称时，通过pid 更新所有下级渠道的pname
				if(!roleid.equals(Constants.ROLE_SILVERCHANNEL)){
					Userinfo.dao.updatePnameByidAndChannelname(id,param.getStr("channelname"));
				}
				//更新当前渠道的上级渠道名称
				Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid(param.getInt("pid"));
				userinfo.set("pname", userinfo2.getStr("channelname"));
				
				userinfo.set("contacts", param.getStr("contacts"));
				userinfo.set("phone", param.getStr("phone"));
				userinfo.set("qq", param.getStr("qq"));
				userinfo.set("province", param.getStr("province"));
				userinfo.set("city", param.getStr("city"));
				userinfo.set("address", param.getStr("address"));
				
				Date now = new Date();
				userinfo.set("updatetime", now);
				Data rst = new Data();
				if(userinfo.update()){
					
					Userrole role = Userrole.dao.selectUserroleByUid(id);
					role.set("roleid", param.getInt("roleid"));		//金牌渠道或者银牌渠道
					role.update();
					rst.setStatus(Constants.OPERATION_SUCCEED);
				    rst.setMsg("编辑渠道商成功！");
				    
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);	
					rst.setMsg("编辑渠道商失败！");
				}
				setFlashData(rst);
				redirect("/channelManager/"+currentPage);
				
			}else{
				
				Integer rid = getParaToInt("roleid");
				String provinceId = getPara("provinceId");
				if(StrKit.notBlank(roleid) && StrKit.notBlank(provinceId)){
					Roleinfo rinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(rid));
					List<Userrole> listuser = Userrole.dao.selectUserroleByPid(rinfo.getStr("pid"));
					List<Sysxzqh> cityList = Sysxzqh.dao.getCityList(provinceId);
					setAttr("listuser", listuser);
					setAttr("cityList", cityList);
				}
				
				setAttr("userinfo", userinfo);
				getProvince();
				setAttr("roleid", roleid);
				setAttr("currentPage",currentPage);//设置当前页码
				render("/admin/channel/channel_manage/edit_channel.html");
				
			}
			
		}
	/**
	 * 修改渠道状态
	 */
	public void modifyChannelStatus(){
		
		final Integer status = getParaToInt("status");
		final String[] bwIds = getParaValues("userIds");

		boolean succeed = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				int count = 0;

				for (String id : bwIds) {
					Userinfo userinfo = Userinfo.dao.findById(id);
					userinfo.set("status", status);
					userinfo.update();
					count++;
				}
				return count == bwIds.length;
			}
		});
		Data rst = new Data();
		if (succeed) {
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("批量更新状态成功！");
			setFlashData(rst);
		} else {
			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("批量更新状态失败！");
		}
		
		renderJson(rst);
		
	}
	/**
	 * 修改单个渠道用户的状态
	 */
	public void modifyStatusById(){
		
		 Integer id = getParaToInt("id");
		 Integer status = getParaToInt("status");
		 Userinfo userinfo = Userinfo.dao.findById(id);
		 Data rst = new Data();
		 if(status == 1){
			 userinfo.set("status", 1);
			 if (userinfo.update()) {
				//添加操作记录
				 Date now = new Date();
				 //Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid(userinfo.getInt("pid"));
				 Operatorlog operatorlog = new Operatorlog();
				 operatorlog.set("logtype", Constants.LOGTYPENABLE);		
				 operatorlog.set("pid", getSessionAttr("roleid"));
				 operatorlog.set("roleid", userinfo.getInt("roleid"));
				 operatorlog.set("userid", id);
				 String operName = getSessionAttr("loginName");
				 operatorlog.set("operator", operName);
				 operatorlog.set("username", userinfo.getStr("channelname"));
				 operatorlog.set("operatortime", now);   
				 operatorlog.set("delflag", 0); 
				 operatorlog.save();
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("启用用户成功！");
				 } else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("启用用户失败！");
				 }
		 }
		 if(status == 0){
			 userinfo.set("status", 0);
			 if (userinfo.update()) {
				//添加操作记录
				 Date now = new Date();
				 Operatorlog operatorlog = new Operatorlog();
				 //Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid(userinfo.getInt("pid"));
				 operatorlog.set("logtype", Constants.LOGTYPDISENABLE);		
				 //operatorlog.set("pid", userinfo2.getInt("roleid"));
				 operatorlog.set("pid", getSessionAttr("roleid"));
				 operatorlog.set("roleid", userinfo.getInt("roleid"));
				 operatorlog.set("userid", id);
				 String operName = getSessionAttr("loginName");
				 operatorlog.set("operator", operName);
				 operatorlog.set("username", userinfo.getStr("channelname"));
				 operatorlog.set("operatortime", now);   
				 operatorlog.set("delflag", 0); 
				 operatorlog.save();
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("禁用用户成功！");
				 } else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("禁用用户失败！");
				 }
		 }
		 setFlashData(rst);	
		 renderJson(rst);
	}
	
	/**
	 * 审核渠道状态
	 */
	public void checkChannelStatus(){
		
		 Integer id = getParaToInt("id");
		 Userinfo userinfo = Userinfo.dao.findById(id);
		 userinfo.set("checkstatus", 1);
		 Data rst = new Data();
		 if (userinfo.update()) {
			 //添加操作记录
			 Date now = new Date();
			 //Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid(userinfo.getInt("pid"));
			 Operatorlog operatorlog = new Operatorlog();
			 operatorlog.set("logtype", Constants.LOGTYPCHECK);		
			 operatorlog.set("pid", getSessionAttr("roleid"));
			 operatorlog.set("roleid", userinfo.getInt("roleid"));
			 operatorlog.set("userid", id);
			 String operName = getSessionAttr("loginName");
			 operatorlog.set("operator", operName);
			 operatorlog.set("username", userinfo.getStr("channelname"));
			 operatorlog.set("operatortime", now);   
			 operatorlog.set("delflag", 0); 
			 operatorlog.save();
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("审核状态成功！");
		 } else {
			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("审核状态失败！");
		 }
		 setFlashData(rst);	
		 renderJson(rst);
		
	}
	/**
	 * 删除渠道用户
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);
		
		int currentPage = getParaToInt("pageNo",1);//获取当前页码

		if (id != 0) {

			try {
				Userinfo userinfo = Userinfo.dao.findById(id);
				if(userinfo != null){
					
					Userinfo.dao.updatePnameByidAndRoleid(id, userinfo.getInt("pid"), userinfo.getStr("pname"));
					
					userinfo.set("delflag", 1);
					Data rst = new Data();
					if(userinfo.update()) {
						
						rst.setStatus(Constants.OPERATION_SUCCEED);
						rst.setMsg("删除渠道用户成功！");
						
						 //添加操作记录
						 Date now = new Date();
						 Operatorlog operatorlog = Operatorlog.dao.getOperatorinfoByUserid(id);
						 if(operatorlog != null){
							 operatorlog.set("logtype", Constants.LOGTYPEDELETE);
							 operatorlog.set("operatortime", now);   
							 operatorlog.set("delflag", 1); 
							 operatorlog.update();
						 }
						 
					}else{
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("删除渠道用户失败！");
					}
					setFlashData(rst);
				}
				

			} catch (Exception e) {
				throw e;
			}

			redirect("/channelManager/"+currentPage);
		}
	}
	public void checkNameDouble(){
		
		// 验证是否已经添加过
		String id = getPara("id");
		if(id == null){
			long count = Userinfo.dao.countExistUser(getPara("userinfo.loginname"));
			long countChannel = Userinfo.dao.countExistChannelName(getPara("userinfo.channelname"));

			if (count > 0) {
				renderJson("false");
				return;
			}
			if (countChannel > 0) {
				renderJson("false");
				return;
			}
		}
		
		renderJson("true");
					
	}
	
	public void changePass() {

		if (getRequest().getMethod().equals("POST")) {
			Userinfo user = getModel(Userinfo.class);

			Userinfo currentUser = Userinfo.dao.findById(user.get("id"));

			String salt = CommonUtil.getRandomString(8);

			currentUser.set("salt", salt);

			currentUser.set("password", CommonUtil.generateSaltPassword(
					user.getStr("password"), salt));

			currentUser.set("updatetime", new Date());
			Data rst = new Data();
			if(currentUser.update()){
				//添加操作记录
				 Date now = new Date();
				 Operatorlog operatorlog = new Operatorlog();
				 operatorlog.set("logtype", Constants.LOGTYPPASSWORD);		
				 operatorlog.set("pid", getSessionAttr("roleid"));
				 operatorlog.set("roleid", currentUser.getInt("roleid"));
				 operatorlog.set("userid", user.get("id"));
				 String operName = getSessionAttr("loginName");
				 operatorlog.set("operator", operName);
				 operatorlog.set("username", currentUser.getStr("channelname"));
				 operatorlog.set("operatortime", now);   
				 operatorlog.set("delflag", 0); 
				 operatorlog.save();
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("修改密码成功！");
			}
			renderJson(rst);
		} else {

			Userinfo user = Userinfo.dao.findById(getParaToInt(0));

			if (user != null) {

				setAttr("userinfo", user);
				setAttr("userid",getParaToInt(0));
				render("/admin/channel/channel_manage/change_pass.html");
			}
		}
	}
	
	/**
	 * 获取上级渠道
	 */
	public void getChannelRole(){
		
		Integer roleid = getParaToInt("roleid");
		
		if(roleid != null){
			Roleinfo roleinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(roleid));
			List<Userrole> listuser = Userrole.dao.selectUserroleByPid(roleinfo.getStr("pid"));
			renderJson(listuser);
		}else{
			renderJson(0);
		}
		
	}
	/**
	 * 获取省份
	 */
	public void getProvince(){
		List<Sysxzqh> provinceList = Sysxzqh.dao.getProvinceList();
		setAttr("provinceList", provinceList);
	}
	
	public void getCity(){
		String provinceId = getPara("provinceId");
		List<Sysxzqh> cityList = Sysxzqh.dao.getCityList(provinceId);
		renderJson(cityList);
	}

	
}
