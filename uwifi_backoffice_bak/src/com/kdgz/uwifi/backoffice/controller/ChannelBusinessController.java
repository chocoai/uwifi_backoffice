package com.kdgz.uwifi.backoffice.controller;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
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

@Before(ChannelInterceptor.class)
public class ChannelBusinessController extends BaseController {

	public void index(){
		String curuser = getPara("user","");
		if(curuser.length()>0){
			Record	user = Db
					.findFirst(
							"select * from userinfo where loginname = ? and status='1'",
							new Object[] { curuser });
			setSessionAttr("loginName", user.getStr("loginname"));
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
			
			redirect("/channelBusiness");
			return;
		}
		
		String userid = getSessionAttr("userid")+"";
		
		String searchName = getPara("searchName");
		Integer searchChannelId = getParaToInt("searchChannelId");
		Integer searchStatus =  getParaToInt("searchStatus");
		String curRoleid = getSessionAttr("roleid");
		setAttr("roleid", curRoleid);
		
		Roleinfo roleinfo = Roleinfo.dao.selectRoleById(Integer.valueOf(curRoleid));
		List<Roleinfo> listRole = Roleinfo.dao.selectRoleListByPid(roleinfo.getStr("pid"));
		setAttr("listRole", listRole);
		
		setAttr("searchChannelId", searchChannelId);
		setAttr("searchStatus", searchStatus);
		setAttr("searchName", searchName);
		setAttr("businessinfoPage", Userinfo.dao.pageUserinfoChannel(getParaToInt(0, 1), 10, "", searchName, searchChannelId, searchStatus,curRoleid,userid));
		List<Userinfo> listchannelName  = Userinfo.dao.selectchannelNameByRoleidAndUid(curRoleid, userid);
		setAttr("listchannelName", listchannelName);
		flashRender("/admin/channel/channelBusiness_manage/list_channelBusiness.html");
	}
	
	public void editBusiness(){
		
		String id = getPara(0);
		Userinfo userinfo = Userinfo.dao.findById(id);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);
			String starttime = getPara("starttime","");
			String endtime = getPara("endtime","");
			
			userinfo.set("businessname", param.getStr("businessname"));
			userinfo.set("contacts", param.getStr("contacts"));
			userinfo.set("phone", param.getStr("phone"));
			userinfo.set("qq", param.getStr("qq"));
			userinfo.set("address", param.getStr("address"));
			userinfo.set("status", param.getInt("status"));
			userinfo.set("province", param.getStr("province"));
			userinfo.set("city", param.getStr("city"));
			userinfo.set("starttime", starttime);
			userinfo.set("endtime", endtime);
			
			Date now = new Date();
			userinfo.set("updatetime", now);
			Data rst = new Data();
			if(userinfo.update()){
				Operatorlog operatorlog = Operatorlog.dao.findFirst("select * from operatorlog where logtype="+Constants.LOGTYPEADD+" and userid = "+id);
				operatorlog.set("operatortime", now);
				operatorlog.update();
				rst.setStatus(Constants.OPERATION_SUCCEED);
			    rst.setMsg("编辑商家成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);	
				rst.setMsg("编辑商家失败！");
			}
			setFlashData(rst);
			redirect("/channelBusiness/"+currentPage);
			
		}else{
			getProvince();
			setAttr("userobj", userinfo);
			setAttr("currentPage",currentPage);//设置当前页码
			render("/admin/channel/channelBusiness_manage/edit_channelBusiness.html");
			
		}
	}

	public void getProvince(){
		List<Sysxzqh> provinceList = Sysxzqh.dao.getProvinceList();
		setAttr("provinceList", provinceList);
	}
	
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
						operatorlog.set("logtype", 2);
						operatorlog.set("operatortime", now);  
						operatorlog.set("delflag", 1);
						operatorlog.update();
						
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

			redirect("/channelBusiness/"+currentPage);
		}
	}
	
	public void toBackManager(){
		int id = getParaToInt(0, 0);
		
		String curUser = getSessionAttr("loginName");
		setSessionAttr("curUser", curUser);
		setSessionAttr("pageJump", "channelBusiness");
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
	
	public void checkBusinessStatus(){
		
		 Integer id = getParaToInt("id");
		 Userinfo userinfo = Userinfo.dao.findById(id);
		 userinfo.set("checkstatus", 1);
		 Data rst = new Data();
		 if (userinfo.update()) {
			 //添加操作记录
			 Date now = new Date();
			 Operatorlog operatorlog = new Operatorlog();
			 operatorlog.set("logtype", Constants.LOGTYPCHECK);	
			 //Userinfo userinfo2 = Userinfo.dao.selectChannelNameByPid(userinfo.getInt("pid"));
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
			rst.setMsg("审核状态成功！");
		 } else {
			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("审核状态失败！");
		 }
		 setFlashData(rst);	
		 renderJson(rst);
		
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
				 Operatorlog operatorlogObj = Operatorlog.dao.findFirst("select * from operatorlog where logtype="+Constants.LOGTYPEADD+" and userid = "+id);
				 operatorlogObj.set("operatortime", now);
				 operatorlogObj.update();
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
