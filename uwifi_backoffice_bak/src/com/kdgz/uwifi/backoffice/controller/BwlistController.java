package com.kdgz.uwifi.backoffice.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Bwlist;
import com.kdgz.uwifi.backoffice.validator.BwlistValidator;

public class BwlistController extends Controller{

	public void index(){
		String acid = getPara("acid");
		String bwtype = getPara("bwtype");
		String controltype = getPara("controltype");
		String businessids = getSessionAttr("businessids");
		setAttr("bwlistPage",
				Bwlist.dao.pageinfoList(getParaToInt(0, 1), 10, acid, bwtype, controltype, businessids ));
		setAttr("bwtype", bwtype);
		setAttr("controltype", controltype);
		setAttr("acid", acid);
		render("/admin/route_manage/bwlist_list.html");
	}
	
	/**
	 * 添加黑白名单信息
	 */
	@Before(BwlistValidator.class)
	public void addBwlist() {

		Bwlist bwlist = new Bwlist();
		if (getRequest().getMethod().equals("POST")) {
			Bwlist param = getModel(Bwlist.class);
			bwlist.set("acid", param.getStr("acid"));
			bwlist.set("whtype", param.getInt("whtype"));
			bwlist.set("controltype", param.getInt("controltype"));
			bwlist.set("controlvalue", param.getStr("controlvalue"));

			if (bwlist.save()) {
//				renderJson("infoMsg", "添加黑白名单信息成功！");
				renderJson();
			} else {
				renderJson("infoMsg", "添加黑白名单信息失败！");
			}

		} else {
			setAttr("bwlist", bwlist);
			getSelectinfo();
			render("/admin/route_manage/bwlist_add.html");
		}
	}
	/**
	 * 编辑AC信息
	 */
	@Before(BwlistValidator.class)
	public void editBwlist() {

		Bwlist bwlist = Bwlist.dao.findById(getPara(0));
		if (getRequest().getMethod().equals("POST")) {
			Bwlist param = getModel(Bwlist.class);
			bwlist.set("acid", param.getStr("acid"));
			bwlist.set("whtype", param.getInt("whtype"));
			bwlist.set("controltype", param.getInt("controltype"));
			bwlist.set("controlvalue", param.getStr("controlvalue"));

			if (bwlist.update()) {
				setAttr("infoMsg", "编辑黑白名单信息成功！");
			} else {
				setAttr("infoMsg", "编辑黑白名单信息失败！");
			}
			forwardAction("/bwlist");

		} else {
			setAttr("bwlist", bwlist);
			getSelectinfo();
			render("/admin/route_manage/bwlist_edit.html");
		}
	}
	/**
	 * 删除黑白名单信息
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			try {

				Bwlist bwlist = Bwlist.dao.findById(id);
				
				if(bwlist != null) {
					
					bwlist.delete();
					
					setAttr("infoMsg", "删除黑白名单信息成功！");
				}

			} catch (Exception e) {
				setAttr("errorMsg", "删除黑白名单信息失败！");
				throw e;
			}

			forwardAction("/bwlist");
		}
	}
	
	public void getSelectinfo(){
		
		String businessids = getSessionAttr("businessids");
		
		List<Acinfo> acidList = Acinfo.dao.selectAcidList(businessids);
		setAttr("acidList", acidList);
	}
}
