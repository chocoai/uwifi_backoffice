package com.kdgz.uwifi.backoffice.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Userbiz;

/**
 * 店铺模块
 * 
 * @author Lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class BusinessinfoController extends BaseController {

	/**
	 * 列表
	 */
	public void index() {

		String busname1 = getPara("busname1");
		String busname2 = getPara("busname2");
		String acid = getPara("acid");
		String searchType = getPara("searchType");

		boolean isHomeActive = true;
		if (StringUtils.isBlank(searchType)) {
			setAttr("searchType", "");
			if (getSessionAttr("isHomeActive") != null) {
				isHomeActive = getSessionAttr("isHomeActive");
				setSessionAttr("isHomeActive", null);
			}
		} else {
			if ("2".equals(searchType)) {
				isHomeActive = false;
			}
			setAttr("searchType", searchType);
		}

		int busPage = 1;
		int acPage = 1;
		
		//控制搜索后编辑店铺
		String busName = "";
		String acName = "";
		try {
			busName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
			acName = java.net.URLDecoder.decode(getPara(3,""),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(acName.length() > 0){
			acid = acName;
		}
		// 路由器列表分页Page
		if (getPara(1) == null || "dp".equals(getPara(1))) {
			if(busName.length() > 0){
				busname1 = busName;
			}
			busPage = getParaToInt(0, 1);
		} else {
			if(busName.length() > 0){
				busname2 = busName;
			}
			isHomeActive = false;
			acPage = getParaToInt(0, 1);
		}
		setAttr("isHomeActive", isHomeActive);

		// 店铺列表
		String businessids = getSessionAttr("businessids");

		setAttr("businessinfoPage", Businessinfo.dao.pageBusinessinfo(busPage,
				10, busname1, businessids));

		setAttr("acinfoPage", Acinfo.dao.pageAcInfoList(acPage, 10, busname2,
				acid, businessids));

		setAttr("busname1", busname1);
		setAttr("busname2", busname2);
		setAttr("acid", acid);
		setAttr("acPage", acPage);

		flashRender("/admin/businessinfo/shop_list.html");
	}

	public void checkName() {
		boolean valid = true;
		String busname = getPara("businessinfo.busname","");
		Businessinfo businfo = Businessinfo.dao.selectBusinessinfoByName(busname, 0);
		if (businfo != null) {
			valid = false;
		}
		renderJson("valid",valid);
	}
	/**
	 * 添加店铺信息
	 */
	public void addBusinessinfo() {

		Businessinfo businessinfo = new Businessinfo();

		if (getRequest().getMethod().equals("POST")) {
			Businessinfo param = getModel(Businessinfo.class);

			businessinfo.set("busname", param.getStr("busname"));
			businessinfo.set("person", param.getStr("person"));
			businessinfo.set("phone", param.getStr("phone"));
			businessinfo.set("address", param.getStr("address"));

			Date now = new Date();
			businessinfo.set("addtime", now);
			businessinfo.set("updatetime", now);

			Data rst = new Data();
			Businessinfo businfo = Businessinfo.dao.selectBusinessinfoByName(
					param.getStr("busname"), 0);

			if (businfo != null) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该店铺已经存在！");
			} else {
				if (businessinfo.save()) {
					int id = businessinfo.getInt("id");
					int userid = getSessionAttr("userid");
					Userbiz userbiz = new Userbiz();
					userbiz.set("userid", userid);
					userbiz.set("businessid", id);
					userbiz.save();
					
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("添加店铺成功！");
					setFlashData(rst);
					removeSessionAttr("businessids");
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加店铺失败！");
				}
			}

			renderJson(rst);
		} else {
			setAttr("businessinfo", businessinfo);
			render("/admin/businessinfo/add_shop.html");
		}
	}

	/**
	 * 编辑店铺信息
	 */
	public void editBusinessinfo() {

		if (getRequest().getMethod().equals("POST")) {
			Businessinfo param = getModel(Businessinfo.class);

			Businessinfo businessinfo = Businessinfo.dao.findById(param
					.getInt("id"));

			businessinfo.set("busname", param.getStr("busname"));
			businessinfo.set("person", param.getStr("person"));
			businessinfo.set("phone", param.getStr("phone"));
			businessinfo.set("address", param.getStr("address"));
			Date now = new Date();
			businessinfo.set("updatetime", now);
			Data rst = new Data();

			Businessinfo businfo = Businessinfo.dao.selectBusinessinfoByName(
					param.getStr("busname"), param.getInt("id"));

			if (businfo != null) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该店铺已经存在！");
			} else {
				if (businessinfo.update()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("编辑店铺信息成功！");
					setFlashData(rst);
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("编辑店铺信息失败！");
				}
			}

			renderJson(rst);
		} else {
			Businessinfo businessinfo = Businessinfo.dao
					.findById(getParaToInt(0));
			setAttr("businessinfo", businessinfo);
			//控制搜索后编辑
			String searchName = "";
			try {
				searchName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			setAttr("searchName",searchName);//添加搜索条件的传递
			setAttr("currentPage",getParaToInt(1, 1));//添加当前页num
			render("/admin/businessinfo/edit_shop.html");
		}
	}

	/**
	 * 删除店铺信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			Data rst = new Data();
			try {

				int userid = getSessionAttr("userid");

				Businessinfo.dao.deleteBusinessInfo(id, userid);

				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除店铺信息成功！");
				removeSessionAttr("businessids");

			} catch (Exception e) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除店铺信息失败！");
				throw e;
			}
			setFlashData(rst);
			setSessionAttr("isHomeActive", true);
			//控制搜索后删除
			String searchName = "";
			try {
				searchName = java.net.URLDecoder.decode(getPara(2,""),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			searchName = java.net.URLEncoder.encode(searchName, "UTF-8");  
			int currentPage = getParaToInt(1, 1);//获取当前页
			redirect("/businessinfo/"+currentPage+"-dp-"+searchName);
		}
	}

	/**
	 * 店铺装修
	 */
	public void editShop() {
		Integer businessid = getParaToInt("businessid", -1);
		String acid = getPara("acid", "");
		setAttr("businessid", businessid);
		setSessionAttr("shopid", businessid);
		setAttr("acid", acid);
		forwardAction("/authStyle");

	}

	public void getBusinessinfoList() {

		List<Businessinfo> list = Businessinfo.dao
				.selectBussinessinfoList(null);
		renderJson(list);
	}

}
