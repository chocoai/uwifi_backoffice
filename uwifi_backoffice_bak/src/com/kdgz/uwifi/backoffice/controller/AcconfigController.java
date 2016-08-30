package com.kdgz.uwifi.backoffice.controller;
import java.util.ResourceBundle;

import com.jfinal.aop.Before;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.UserValidationInterceptor;
import com.kdgz.uwifi.backoffice.model.Acconfig;
import com.kdgz.uwifi.backoffice.model.Acinfo;

@Before(UserValidationInterceptor.class)
public class AcconfigController extends BaseController {

	/**
	 * 列表
	 */
	public void index() {

		String busname = getPara("busname");
		String construtname = getPara("construtname");
		String businessids = getSessionAttr("businessids");
		setAttr("acinfoPage",
				Acinfo.dao.pageinfoList(getParaToInt(0, 1), 10, busname, construtname, businessids));
		render("/admin/route_manage/ac_list.html");
	}

	/**
	 * AC配置信息
	 */
	public void editAcconfig() {
		String acid = getPara(0);
		Acconfig acconfig = Acconfig.dao.selectAcconfigByAcid(acid);
		ResourceBundle bundle = ResourceBundle.getBundle("common");
		setAttr("acid", acid);
		Data rst = new Data();
		setAttr("currentPage",getParaToInt(1, 1));//添加当前页num
		//更新
		if(acconfig != null){
			if(getRequest().getMethod().equals("POST")){
				Acconfig param = getModel(Acconfig.class);
				acconfig.set("gatewayaddress", param.getStr("gatewayaddress"));
				acconfig.set("maxuser", param.getInt("maxuser"));
				acconfig.set("checkinterval", param.getInt("checkinterval"));
				acconfig.set("clienttimeout", param.getInt("clienttimeout"));
				acconfig.set("dhcplease", param.getInt("dhcplease"));
				acconfig.set("clientspeed", param.getInt("clientspeed"));
				acconfig.set("totalupload", param.getInt("totalupload"));
				acconfig.set("totaldownload", param.getInt("totaldownload"));
				
				if (acconfig.update()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("更新AC配置信息成功！");
					setFlashData(rst);
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("更新AC配置信息失败！");
				}
				
				setSessionAttr("isHomeActive", false);
				renderJson(rst);
				return;
			}else{
				
				setAttr("acconfig", acconfig);
				render("/admin/businessinfo/ac_config.html");
				return;
			}
			
		}else{		//添加
		     acconfig = new Acconfig();
			if(getRequest().getMethod().equals("POST")){
				Acconfig param = getModel(Acconfig.class);
				acconfig.set("acid", param.getStr("acid"));
				acconfig.set("gatewayid", param.getStr("acid"));
				acconfig.set("gatewayaddress", param.getStr("gatewayaddress"));
				acconfig.set("gatewayport", bundle.getString("gatewayport"));
				acconfig.set("maxuser", param.getInt("maxuser"));
				acconfig.set("checkinterval", param.getInt("checkinterval"));
				acconfig.set("clienttimeout", param.getInt("clienttimeout"));
				acconfig.set("authserverhostname", bundle.getString("authserverhostname"));
				acconfig.set("authserverport", bundle.getString("authserverport"));
				acconfig.set("authserverpath", bundle.getString("authserverpath"));
				acconfig.set("clientspeed", param.getInt("clientspeed"));
				acconfig.set("totalupload", param.getInt("totalupload"));
				acconfig.set("totaldownload", param.getInt("totaldownload"));
//				acconfig.set("authserverhostname", param.getStr("authserverhostname"));
//				acconfig.set("authserverssl", param.getInt("authserverssl"));
//				acconfig.set("authserverport", param.getInt("authserverport"));
//				acconfig.set("authserverpath", param.getStr("authserverpath"));
				acconfig.set("dhcplease", param.getInt("dhcplease"));
				
				if (acconfig.save()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("添加AC配置信息成功！");
					setFlashData(rst);
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加AC配置信息失败！");
				}
				
				setSessionAttr("isHomeActive", false);
				renderJson(rst);
			}else{
				acconfig.set("gatewayid", acid);
				acconfig.set("gatewayaddress", bundle.getString("gatewayaddress"));
				acconfig.set("gatewayport", bundle.getString("gatewayport"));
				acconfig.set("maxuser", bundle.getString("maxuser"));
				acconfig.set("checkinterval", bundle.getString("checkinterval"));
				acconfig.set("clienttimeout",bundle.getString("clienttimeout"));
				acconfig.set("authserverhostname", bundle.getString("authserverhostname"));
				acconfig.set("authserverport", bundle.getString("authserverport"));
				acconfig.set("authserverpath", bundle.getString("authserverpath"));
				acconfig.set("dhcplease", bundle.getString("dhcplease"));
				acconfig.set("clientspeed", bundle.getString("clientspeed"));
				acconfig.set("totalupload", bundle.getString("totalupload"));
				acconfig.set("totaldownload", bundle.getString("totaldownload"));
				
				setAttr("acconfig", acconfig);
				render("/admin/businessinfo/ac_config.html");
			}
		}
	}

	/**
	 * 启动脚本(隐藏)
	 */
	public void startUp(){
		
		ResourceBundle bundle = ResourceBundle.getBundle("common");
		String acid = getPara("acid");
		String acos = getPara("acos");
		String hostname = bundle.getString("hostname");
		String path = bundle.getString("path");
		
		setAttr("acid", acid);
		setAttr("acos", acos);
		setAttr("hostname", hostname);
		setAttr("path", path);
		
		
		render("/admin/route_manage/startup.html");
		
	}
	
	
}
