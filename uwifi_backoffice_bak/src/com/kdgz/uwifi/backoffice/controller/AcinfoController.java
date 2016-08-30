package com.kdgz.uwifi.backoffice.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.UserValidationInterceptor;
import com.kdgz.uwifi.backoffice.model.Acbrand;
import com.kdgz.uwifi.backoffice.model.Acconfig;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Acstatetemp;
import com.kdgz.uwifi.backoffice.model.Actypeinfo;
import com.kdgz.uwifi.backoffice.model.Businessinfo;

@Before(UserValidationInterceptor.class)
public class AcinfoController extends BaseController {
	
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	/**
	 * 列表
	 */
	public void index() {

		String busname = getPara("busname");
		String construtname = getPara("construtname");
		String businessids = getSessionAttr("businessids");
		setAttr("acinfoPage",
				Acinfo.dao.pageinfoList(getParaToInt(0, 1), 10, busname, construtname, businessids));
		setAttr("busname", busname);
		setAttr("construtname", construtname);
		render("/admin/businessinfo/ac_list.html");
	}
	
	public void checkName() {
		boolean valid = true;
		String acid = getPara("acinfo.acid","");
		Acinfo ac = Acinfo.dao.selectAcinfoByAcId(acid);
		if(ac != null){
			valid = false;
		}
		renderJson("valid",valid);
	}

	/**
	 * 添加AC信息
	 */
	public void addAcinfo() {

		Acinfo acinfo = new Acinfo();
		if (getRequest().getMethod().equals("POST")) {
			Acinfo param = getModel(Acinfo.class);
			acinfo.set("businessid", param.getInt("businessid"));
//			acinfo.set("construtid", param.getInt("construtid"));
			acinfo.set("acid", param.getStr("acid"));
			acinfo.set("acbrandid", param.getInt("acbrandid"));
			acinfo.set("actypeid", param.getInt("actypeid"));
			acinfo.set("eqptssid", param.getStr("eqptssid"));
			acinfo.set("acos", Constants.AC_TYPE_OPENWRT);
			Date now = new Date();
			acinfo.set("addtime", now);
			Acinfo ac = Acinfo.dao.selectAcinfoByAcId(param.getStr("acid"));
			
			Data rst = new Data();
			if(ac != null){
				
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该路由器编号已经存在！");
			}else{
				if (acinfo.save()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					String acid = param.getStr("acid");
					saveAcconfig(acid);
					setFlashData(rst);
					rst.setMsg("添加路由器信息成功！");
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加路由器信息失败！");
				}
			}
			
			setSessionAttr("isHomeActive", false);
			renderJson(rst);

		} else {
			setAttr("acinfo", acinfo);
			getSelectinfo();
			
			render("/admin/businessinfo/add_ac.html");
		}
	}
	/**
	 * 编辑AC信息
	 */
	public void editAcinfo() {

		if (getRequest().getMethod().equals("POST")) {
			Acinfo param = getModel(Acinfo.class);
//			int id = param.getInt("id");
			Acinfo acinfo = Acinfo.dao.findById(param.getInt("id"));
//			acinfo.set("businessid", param.getInt("businessid"));
//			acinfo.set("construtid", param.getInt("construtid"));
//			acinfo.set("acid", param.getStr("acid"));
			acinfo.set("acbrandid", param.getInt("acbrandid"));
			acinfo.set("actypeid", param.getInt("actypeid"));
			acinfo.set("eqptssid", param.getStr("eqptssid"));
//			acinfo.set("acos", param.getStr("acos"));

//			Acinfo ac = Acinfo.dao.selectAcinfoByIdAndAcId(id, param.getStr("acid") );
			
			Data rst = new Data();
//			if(ac != null){
//				rst.setStatus(Constants.OPERATION_FAILED);
//				rst.setMsg("该路由器编号已经存在！");
//			}else{
				if (acinfo.update()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("编辑路由器信息成功！");
					setFlashData(rst);
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("编辑路由器信息失败！");
				}
				setSessionAttr("isHomeActive", false);
				renderJson(rst);
//			}

		} else {
			int id = getParaToInt(0);
			Acinfo acinfo = Acinfo.dao.findById(id);
			setAttr("acinfo", acinfo);
			getSelectinfo();
			List<Actypeinfo> actypeinfoList = Actypeinfo.dao.selectActpeinfoListById(acinfo.get("acbrandid").toString());
			setAttr("actypeinfoList", actypeinfoList);
			
			//控制搜索后编辑
			String busName = "";
			String acName = "";
			try {
				busName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
				acName = java.net.URLDecoder.decode(getPara(3,""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			setAttr("busName",busName);//添加搜索条件的传递
			setAttr("acName",acName);//添加搜索条件的传递
			
			setAttr("currentPage",getParaToInt(1, 1));//添加当前页num
			render("/admin/businessinfo/edit_ac.html");
		}
	}
	
	/**
	 * 删除AC信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			Data rst = new Data();
			try {

				Acinfo acinfo = Acinfo.dao.findById(id);
				
				if(acinfo != null) {
					acinfo.delete();
					
					String acid = acinfo.getStr("acid");
					Acconfig.dao.deleteAcconfigByAcid(acid);
					
					Acstatetemp.dao.deleteAcstatetempByAcid(acid);
					
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("删除路由器信息成功！");
					
				}

			} catch (Exception e) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("删除路由器信息失败！");
				throw e;
			}
			setFlashData(rst);
			setSessionAttr("isHomeActive", false);
			
			//控制搜索后编辑
			String busName = "";
			String acName = "";
			try {
				busName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
				acName = java.net.URLDecoder.decode(getPara(3,""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			busName = java.net.URLEncoder.encode(busName, "UTF-8");
			acName = java.net.URLEncoder.encode(acName, "UTF-8");
			
			int currentPage = getParaToInt(1, 1);//获取当前页
			redirect("/businessinfo/"+currentPage+"-test-"+busName+"-"+acName);
		}
	}
	
	/**
	 * 获取商家名称列表
	 * 获取施工方名称列表
	 * 获取品牌列表
	 */
	public void getSelectinfo(){
		String businessids = getSessionAttr("businessids");
		//获取商家名称列表
		List<Businessinfo> businessinfoList = Businessinfo.dao.selectBussinessinfoList(businessids);
		setAttr("businessinfoList", businessinfoList);
		
		setAttr("busCount", businessinfoList.size());
		//获取施工方名称列表
//		List<Construtinfo> construtinfoList = Construtinfo.dao.selectConstrutinfoList();
//		setAttr("construtinfoList", construtinfoList);
		//获取品牌列表
		List<Acbrand> acbrandlist = Acbrand.dao.selectAcbrandList();
		setAttr("acbrandList", acbrandlist);
		
	}
	
	/**
	 * 获取品牌类型列表
	 */
	public void getActypeinfoList() {

		String acbrandId = getPara("acbrandId");
		List<Actypeinfo> actypeinfoList = Actypeinfo.dao.selectActpeinfoListById(acbrandId);
		renderJson(actypeinfoList);

	}
	/**
	 * 保存ac配置信息
	 */
	public void saveAcconfig(String acid){
		
		Acconfig acconfig = new Acconfig();
		acconfig.set("acid", acid);
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
		acconfig.save();
		
	}

}
