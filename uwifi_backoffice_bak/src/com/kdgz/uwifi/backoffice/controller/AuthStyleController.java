package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopInterceptor;
import com.kdgz.uwifi.backoffice.model.Acauth;
import com.kdgz.uwifi.backoffice.model.Authsmstemple;
import com.kdgz.uwifi.backoffice.model.Authweixinconfig;
import com.kdgz.uwifi.backoffice.model.Multiauthconfig;
import com.kdgz.uwifi.backoffice.model.Smsacconfig;

@Before(ShopInterceptor.class)
public class AuthStyleController  extends BaseController {

	ResourceBundle bundle = ResourceBundle.getBundle("common");
	
	/**
	 * 认证方式设置
	 */
	public void index(){
		
		String acid = getPara("acid","");
		int businessid = getSessionAttr("shopid");
		String authtypeId = getPara("acauth.authtype");
		Acauth acauth = Acauth.dao.selectAcauthByAcId(businessid+"");//acid to businessid
		setAttr("acid", acid);
		setAttr("businessid", businessid);
		
		setAttr("uploadPath",bundle.getString("uploadPath"));
		
		setAttr("weixinUrl",bundle.getString("weixinUrl").trim());
		
		// 短信认证
		Smsacconfig smsacconfig = Smsacconfig.dao.selectSmsacconfigByAcId(businessid+"");// businessid
		Integer  smsid = getParaToInt("smsid");;

		// 微信认证
		Authweixinconfig authweixinconfig = Authweixinconfig.dao.selectAuthweixinByBusinessId(businessid+"");
		
		//多认证方式
		Multiauthconfig multiauthconfig = null;
		if(smsacconfig != null){
			 multiauthconfig = Multiauthconfig.dao.selectMultiauthBymmsid(smsacconfig.getInt("smsid"), businessid+"");//acId to businessid
		}
		if (getRequest().getMethod().equals("POST")) {
			Acauth param = getModel(Acauth.class);
			int afterauth = getParaToInt("afterauth",1);
			String portalurl = getPara("protalUrl","");
			if(2==afterauth){
				portalurl = getPara("zdurl","");
			}
			
			Data retMsg = new Data();
			if(acauth == null){
				acauth = new Acauth();
				//acauth.set("acid", acid);
				acauth.set("businessid", businessid);
				acauth.set("authtype", param.getInt("authtype"));
				Date now = new Date();
				acauth.set("addtime", now);
				acauth.set("updatetime", now);
				acauth.set("afterauth", afterauth);
				acauth.set("portalurl", portalurl);
				if (acauth.save()) {
					retMsg.setStatus(Constants.OPERATION_SUCCEED);
					retMsg.setMsg("配置认证方式成功！");
				} else {
					retMsg.setStatus(Constants.OPERATION_FAILED);
					retMsg.setMsg("配置认证方式失败！");
				}
			}else{
				acauth.set("authtype", param.getInt("authtype"));
				Date now = new Date();
				acauth.set("updatetime", now);
				acauth.set("afterauth", afterauth);
				acauth.set("portalurl", portalurl);
				if (acauth.update()) {
					retMsg.setStatus(Constants.OPERATION_SUCCEED);
					retMsg.setMsg("配置认证方式成功！");
				} else {
					retMsg.setStatus(Constants.OPERATION_FAILED);
					retMsg.setMsg("配置认证方式失败！");
				}
			}
			// 短信配置
			if(authtypeId.equals("1") || authtypeId.equals("3")){
				if(smsacconfig != null){
					smsacconfig.set("smsid", smsid);
					if (smsacconfig.update()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}else{
					smsacconfig = new Smsacconfig();
					//smsacconfig.set("acid", acid);
					smsacconfig.set("businessid", businessid);
					smsacconfig.set("smsid", smsid);
					if (smsacconfig.save()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}
			}
			
			//微信配置
			if(authtypeId.equals("2") || authtypeId.equals("3")){
				Authweixinconfig param1 = getModel(Authweixinconfig.class);
				if(authweixinconfig != null){
					//authweixinconfig.set("businessid", businessid);
					authweixinconfig.set("weixinname", param1.getStr("weixinname"));
					authweixinconfig.set("weixinno", param1.getStr("weixinno"));
					authweixinconfig.set("beforeauthtime", param1.getInt("beforeauthtime"));
					authweixinconfig.set("url", param1.getStr("url"));
					Date now = new Date();
					authweixinconfig.set("updatetime", now);
					if (authweixinconfig.update()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}else{
					authweixinconfig = new Authweixinconfig();
					authweixinconfig.set("businessid", businessid);
					authweixinconfig.set("weixinname", param1.getStr("weixinname"));
					authweixinconfig.set("weixinno", param1.getStr("weixinno"));
					authweixinconfig.set("beforeauthtime", param1.getInt("beforeauthtime"));
					authweixinconfig.set("url", param1.getStr("url"));
					authweixinconfig.set("delflag", 0);
					Date now = new Date();
					authweixinconfig.set("addtime", now);
					authweixinconfig.set("updatetime", now);
					
					if (authweixinconfig.save()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}
				
			}
			//多种认证方式配置
			if(authtypeId.equals("3")){
				Multiauthconfig param2 = getModel(Multiauthconfig.class);
				String[] tipcontent = getParaValues("tipcontent");
				
				if(multiauthconfig != null){
					multiauthconfig.set("smsid", smsid);
					multiauthconfig.set("employeepwd", param2.getStr("employeepwd"));
					multiauthconfig.set("freetime", param2.getInt("freetime"));
					multiauthconfig.set("tiptitle", param2.getStr("tiptitle"));
					if(tipcontent != null){
						for(int i = 0; i < tipcontent.length; i++){
							int j = i+1;
							multiauthconfig.set("tipcontent"+j, tipcontent[i]);
						}
					}
					
					Date now = new Date();
					multiauthconfig.set("updatetime", now);
					if (multiauthconfig.update()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}else{
					multiauthconfig = new Multiauthconfig();
					multiauthconfig.set("smsid", smsid);
					multiauthconfig.set("businessid", businessid);
					multiauthconfig.set("employeepwd", param2.getStr("employeepwd"));
					multiauthconfig.set("freetime", param2.getInt("freetime"));
					multiauthconfig.set("tiptitle", param2.getStr("tiptitle"));
					if(tipcontent != null){
						for(int i = 0; i < tipcontent.length; i++){
							int j = i+1;
							multiauthconfig.set("tipcontent"+j, tipcontent[i]);
						}
					}
					Date now = new Date();
					multiauthconfig.set("addtime", now);
					multiauthconfig.set("updatetime", now);
					
					if (multiauthconfig.save()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}
				
			}
			
			//renderJson("msg", infoMsg);
			renderJson(retMsg);
			
		} else {
			setAttr("acauth", acauth);
			
			//短信认证
			List<Authsmstemple> authsmslist = Authsmstemple.dao.selectAuthsms();
			Authsmstemple authsmstemple = null;
			if(smsacconfig != null){
				 authsmstemple = Authsmstemple.dao.selectSmstempleById(smsacconfig.getInt("smsid"));
			}else{
				for(int i = 0; i < authsmslist.size(); ){
					authsmstemple  =  authsmslist.get(i);
					break;
				}
			}
			setAttr("authsmslist", authsmslist);
			setAttr("acid", acid);
			setAttr("smsacconfig", smsacconfig);
			setAttr("authsmstemple", authsmstemple);
		
			//微信认证
			setAttr("authweixinconfig", authweixinconfig);
			
			//多认证方式
			setAttr("multiauthconfig", multiauthconfig);
			
			render("/admin/shop_manage/AuthenticateStyle/AuthenticatesStyle.html");
		}
		
	}
	
	public void download(){
		File file = new File(JFinal.me().getServletContext().getRealPath("/")+"download/wxrzpzsc/微信认证配置帮助文档.doc");

		if(file.exists()) {

			renderFile(file);

			return ;

		} 
		index();
	}
}
