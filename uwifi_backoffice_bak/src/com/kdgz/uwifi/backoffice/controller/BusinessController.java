package com.kdgz.uwifi.backoffice.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ChannelInterceptor;
import com.kdgz.uwifi.backoffice.model.Operatorlog;
import com.kdgz.uwifi.backoffice.model.Roleinfo;
import com.kdgz.uwifi.backoffice.model.Sysxzqh;
import com.kdgz.uwifi.backoffice.model.Userbiz;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;

@Before(ChannelInterceptor.class)
public class BusinessController extends BaseController {

	public void index(){
		
		String curuser = getPara("user","");
		if(curuser.length()>0){
			Record	user = Db
					.findFirst(
							"select * from userinfo where delflag = 0 and loginname = ? and status = 1 and checkstatus = 1",
							new Object[] { curuser });
			setSessionAttr("loginName", curuser);
			setSessionAttr("userid", user.getInt("id"));
			
			setSessionAttr("roleCode", user.getBoolean("rolecode"));
			setSessionAttr("isLogin", true);
			setSessionAttr("province", user.getStr("province"));
			setSessionAttr("city", user.getStr("city"));
			setSessionAttr("channelName", user.getStr("channelname"));
			Roleinfo roleinfo = Roleinfo.dao.selectRoleinfoByUserId(user.getInt("id"));
			String roleid = roleinfo.getInt("id").toString();
			setSessionAttr("roleid", roleid);
			removeSessionAttr("businessids");
			
			redirect("/business");
			return;
		}
		
		String userid = getSessionAttr("userid")+"";
		
		String searchName = getPara("searchName");
		Integer searchRole = getParaToInt("searchRole");
		Integer searchStatus =  getParaToInt("searchStatus");
		String curRoleid = getSessionAttr("roleid");
		
//		Roleinfo roleinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(curRoleid));
		List<Roleinfo> listRole = Roleinfo.dao.selectRoleListByPid(curRoleid);
		setAttr("listRole", listRole);
		setAttr("businessinfoPage", Userinfo.dao.pageUserinfo(getParaToInt(0, 1), 10, "", searchName,searchRole, searchStatus,curRoleid,userid));
		
		setAttr("searchRole", searchRole);
		setAttr("searchStatus", searchStatus);
		setAttr("searchName", searchName);
		flashRender("/admin/channel/business_manage/list_business.html");
		
	}
	
	public void addBusiness(){

		Userinfo user = new Userinfo();
		String roleid = getSessionAttr("roleid");
		//上级渠道
		superChannel();
		
		if (getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);
			String starttime = getPara("starttime","");
			String endtime = getPara("endtime","");
			Data rst = new Data();
			
			user.set("loginname", param.getStr("loginname"));

			String salt = CommonUtil.getRandomString(8);
			user.set("password", CommonUtil.generateSaltPassword(
					param.getStr("password"), salt));
			user.set("salt", salt);

			user.set("rolecode", 0);
			
			user.set("businessname", param.getStr("businessname"));
			user.set("roleid", Constants.ROLE_BUSINESSUSER);
			user.set("pid", getSessionAttr("userid"));
			Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid((Integer)getSessionAttr("userid"));
			user.set("pname", userinfo2.getStr("channelname"));
			user.set("contacts", param.getStr("contacts"));
			user.set("phone", param.getStr("phone"));
			user.set("qq", param.getStr("qq"));
			user.set("address", param.getStr("address"));
			user.set("province", param.getStr("province"));
			user.set("city", param.getStr("city"));
			
			if(roleid.equals(Constants.ROLE_ADMIN)){
				user.set("status", 1);		//启用
				user.set("checkstatus", 1);			//默认是已审核状态
				
			}else{
				user.set("status", 1);
				user.set("checkstatus", 0);	//默认是未审核状态
			}
			
			user.set("starttime", starttime);
			user.set("endtime", endtime);
			
			Date now = new Date();
			user.set("addtime", now);
			user.set("updatetime", now);
			if(user.save()){
				int userid = user.getInt("id");
				Record role = new Record();
				role.set("userid", userid);
				role.set("roleid", 2);//商家账号
				Db.save("userrole", role); // 保存商家用户所具有的权限
				
				Userbiz userbiz = new Userbiz();
				userbiz.set("userid", userid);
				userbiz.set("businessid", userid);
				userbiz.save();
				
				//保存操作记录
				Operatorlog operatorlog = new Operatorlog();
				operatorlog.set("logtype", 1);//开通账号
				operatorlog.set("userid", userid);
				operatorlog.set("pid", getSessionAttr("roleid"));
				operatorlog.set("roleid", Constants.ROLE_BUSINESSUSER);
				operatorlog.set("operator", getSessionAttr("loginName"));
				operatorlog.set("operatortime", now);
				operatorlog.set("starttime", starttime);
				operatorlog.set("endtime", endtime);
				operatorlog.set("username", user.getStr("businessname"));
				operatorlog.save();
				
				rst.setStatus(Constants.OPERATION_SUCCEED);
			    rst.setMsg("添加商家成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);	
				rst.setMsg("添加商家失败！");
			}
			setFlashData(rst);
			redirect("/business");
			
		}else{
			String startTime = getday(new Date(),0);
			String endTime = getday(new Date(),15);
			setAttr("startTime",startTime);
			setAttr("endTime",endTime);
			
			getProvince();
			render("/admin/channel/business_manage/add_business.html");
			
		}
	}
	
	public String getday(Date timeStr, int num) {  
        String strday = "";  
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");  
          
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(timeStr);
        calendar.add(Calendar.DATE, num);
        strday = sdFormat.format(calendar.getTime());  
        return strday;  
    } 

	private void superChannel() {
		String roleid = getSessionAttr("roleid");
		List<Userinfo> list = Userinfo.dao.getUserinfoList(roleid);
		setAttr("channelList", list);
	}
	
	public void editBusiness(){
		
		//上级渠道
		superChannel();
		
		String id = getPara(0);
		Userinfo userinfo = Userinfo.dao.findById(id);
		
		int currentPage = getParaToInt("pageNo",1);//获取当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);
			String starttime = getPara("starttime","");
			String endtime = getPara("endtime","");
			
			userinfo.set("businessname", param.getStr("businessname"));
//			userinfo.set("roleid", Constants.ROLE_BUSINESSUSER);
//			userinfo.set("pid", getSessionAttr("userid"));
			userinfo.set("contacts", param.getStr("contacts"));
			userinfo.set("phone", param.getStr("phone"));
			userinfo.set("qq", param.getStr("qq"));
			userinfo.set("address", param.getStr("address"));
			userinfo.set("province", param.getStr("province"));
			userinfo.set("city", param.getStr("city"));
			userinfo.set("starttime", starttime);
			userinfo.set("endtime", endtime);
			
			Date now = new Date();
			userinfo.set("updatetime", now);
			Data rst = new Data();
			if(userinfo.update()){
				Operatorlog operatorlog = Operatorlog.dao.findFirst("select * from operatorlog where logtype="+Constants.LOGTYPEADD+" and userid = "+id);
				if(operatorlog != null){
					operatorlog.set("operatortime", now);
					operatorlog.update();
				}
				rst.setStatus(Constants.OPERATION_SUCCEED);
			    rst.setMsg("编辑商家成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);	
				rst.setMsg("编辑商家失败！");
			}
			setFlashData(rst);
			redirect("/business/"+currentPage);
			
		}else{
			getProvince();
			setAttr("userobj", userinfo);
			setAttr("currentPage",currentPage);//设置当前页码
			render("/admin/channel/business_manage/edit_business.html");
			
		}
		
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
				Date now = new Date();
				Operatorlog operatorlog = new Operatorlog();
				operatorlog.set("logtype", Constants.LOGTYPPASSWORD);
				operatorlog.set("pid", getSessionAttr("roleid"));
				operatorlog.set("roleid", currentUser.getInt("roleid"));
				operatorlog.set("userid", user.get("id"));
				String operName = getSessionAttr("loginName");
				operatorlog.set("operator", operName);
				operatorlog.set("username", currentUser.getStr("businessname"));
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
				render("/admin/channel/business_manage/change_pass.html");
			}
		}
	}
	
	/**
	 * 获取省份
	 */
	public void getProvince(){
		List<Sysxzqh> provinceList = Sysxzqh.dao.getProvinceList();
		setAttr("provinceList", provinceList);
	}
	
	/**
	 * 获取城市
	 */
	public void getCity(){
		String provinceId = getPara("provinceId");
		List<Sysxzqh> cityList = Sysxzqh.dao.getCityList(provinceId);
		renderJson(cityList);
	}
	
	/**
	 * 批量启用禁用
	 */
	public void modifyBusinessStatus(){
		
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
	 * 删除商家用户
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
					
					userinfo.set("delflag", 1);
					Data rst = new Data();
					if(userinfo.update()) {
						Date now = new Date();
						Operatorlog operatorlog = Operatorlog.dao.findFirst("select * from operatorlog where userid="+id);
						if(operatorlog != null){
							operatorlog.set("logtype", 2);
							operatorlog.set("operatortime", now);  
							operatorlog.set("delflag", 1);
							operatorlog.update();
						}
						
						Userbiz userBiz = Userbiz.dao.findFirst(" select * from userbiz where businessid="+id);
						if(userBiz != null) {
							userBiz.delete();
						}
						
						rst.setStatus(Constants.OPERATION_SUCCEED);
						rst.setMsg("删除商家成功！");
					}else{
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("删除商家失败！");
					}
					setFlashData(rst);
				}
				

			} catch (Exception e) {
				throw e;
			}

//			int currentPage = getParaToInt(1, 1);//获取当前页码
			redirect("/business/"+currentPage);
		}
	}
	
	/**
	 * 跳转到管理平台
	 */
	public void toBackManager(){
		int id = getParaToInt(0, 0);
		
		String curUser = getSessionAttr("loginName");
		setSessionAttr("curUser", curUser);
		setSessionAttr("pageJump", "business");
		Userinfo user = Userinfo.dao.findById(id);
		setSessionAttr("loginName", user.getStr("loginname"));
		setSessionAttr("roleCode", user.getBoolean("rolecode"));
		setSessionAttr("isLogin", true);
		setSessionAttr("userid", user.getInt("id"));
		setSessionAttr("province", user.getStr("province"));
		setSessionAttr("city", user.getStr("city"));
		setSessionAttr("isBusiness", true);
		redirect("/dashboard");
	}
	
	public void checkNameDouble(){
		
		// 验证是否已经添加过
		String id = getPara("id");
		if(id == null){
			long count = Userinfo.dao.countExistUser(getPara("userinfo.loginname"));
			long countChannel = Userinfo.dao.countExistBusinessName(getPara("userinfo.businessname"));

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
	
	public void modifyStatusById(){
		
		 Integer id = getParaToInt("id");
		 Integer status = getParaToInt("status");
		 Userinfo userinfo = Userinfo.dao.findById(id);
		 Data rst = new Data();
		 if(status == 1){
			 userinfo.set("status", 1);
			 if (userinfo.update()) {
				 Date now = new Date();
				 
				//修改操作记录
//				 Operatorlog operatorlogObj = Operatorlog.dao.findFirst("select * from operatorlog where logtype="+Constants.LOGTYPEADD+" and userid = "+id);
//				 operatorlogObj.set("operatortime", now);
//				 operatorlogObj.update();
				//添加操作记录
				 Operatorlog operatorlog = new Operatorlog();
				 operatorlog.set("logtype", Constants.LOGTYPENABLE);		
				 operatorlog.set("pid", getSessionAttr("roleid"));
				 operatorlog.set("roleid", userinfo.getInt("roleid"));
				 operatorlog.set("userid", id);
				 String operName = getSessionAttr("loginName");
				 operatorlog.set("operator", operName);
				 operatorlog.set("username", userinfo.getStr("businessname"));
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
				 operatorlog.set("logtype", Constants.LOGTYPDISENABLE);		
				 operatorlog.set("pid", getSessionAttr("roleid"));
				 operatorlog.set("roleid", userinfo.getInt("roleid"));
				 operatorlog.set("userid", id);
				 String operName = getSessionAttr("loginName");
				 operatorlog.set("operator", operName);
				 operatorlog.set("username", userinfo.getStr("businessname"));
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
}
