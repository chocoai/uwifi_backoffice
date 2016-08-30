package com.kdgz.uwifi.backoffice.controller;

import java.util.List;
import java.util.ResourceBundle;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Siteflash;

@Before(ShopLayoutInterceptor.class)
public class SiteflashController extends BaseController{
	
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index(){
		Integer businessid = getSessionAttr("shopid");
		setAttr("siteflashinfoPage", Siteflash.dao.pageSiteflash(getParaToInt(0, 1), 10, 1, businessid));
		setAttr("uploadPath",bundle.getString("uploadPath"));
		flashRender("/admin/website/flash/list_flash.html");
	}
	
	public void addFlash(){
		
		Siteflash siteflash = new Siteflash();
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		if(getRequest().getMethod().equals("POST")) {
			Siteflash param = getModel(Siteflash.class);
			siteflash.set("businessid", businessid);
			siteflash.set("info", param.getStr("info"));
			siteflash.set("img", param.getStr("img"));
			siteflash.set("url", param.getStr("url"));
			siteflash.set("sort", param.getInt("sort"));
			siteflash.set("type", 1);	// 1  幻灯片 2 轮播背景图
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteflash.set("addtime", time);
			siteflash.set("updatetime", time);
			Data rst = new Data();
			if(siteflash.save()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("添加幻灯片成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("添加幻灯片失败！");
			}
			setFlashData(rst);
			redirect("/siteflash");
			
		}else{
			getSelectinfo();
			setAttr("uploadPath",bundle.getString("uploadPath"));
			render("/admin/website/flash/add_flash.html");
		}
	}
	
	public void editFlash(){
		Integer id = getParaToInt("id");
		Siteflash siteflash = Siteflash.dao.findById(id);
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			Siteflash param = getModel(Siteflash.class);
			siteflash.set("info", param.getStr("info"));
			siteflash.set("img", param.getStr("img"));
			siteflash.set("url", param.getStr("url"));
			siteflash.set("sort", param.getInt("sort"));
			siteflash.set("type", 1);	// 1  幻灯片 2 轮播背景图
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteflash.set("updatetime", time);
			Data rst = new Data();
			if(siteflash.update()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("修改幻灯片成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("修改幻灯片失败！");
			}
			setFlashData(rst);
			redirect("/siteflash/"+currentPage);
			
		}else{
			setAttr("siteflash", siteflash);
			getSelectinfo();
			setAttr("uploadPath",bundle.getString("uploadPath"));
			setAttr("currentPage",currentPage);//设置当前页码
			render("/admin/website/flash/edit_flash.html");
		}
	}
	
	/**
	 * 删除分类内容信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt("id");
		int type = getParaToInt("type");

		if (id != 0) {

			try {

				Siteflash siteflash = Siteflash.dao.findById(id);
				if(siteflash != null){
					siteflash.set("delflag", 1);
					Data rst = new Data();
					if(siteflash.update()) {
						rst.setStatus(Constants.OPERATION_SUCCEED);
						rst.setMsg("删除幻灯片信息成功！");
					}else{
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("删除幻灯片信息失败！");
					}
					setFlashData(rst);
				}

			} catch (Exception e) {
				throw e;
			}
			if(type == 1){
				int currentPage = getParaToInt("pageNo",1);//获取当前页码
				redirect("/siteflash/"+currentPage);
			}
			if(type == 2){
				
				redirect("/siteflash/carousel");
			}
			
		}
	}
	
	/**
	 * 获取商家名称列表
	 */
	public void getSelectinfo(){
		String businessids = getSessionAttr("businessids");
		//获取商家名称列表
		List<Businessinfo> businessinfoList = Businessinfo.dao.selectBussinessinfoList(businessids);
		setAttr("businessinfoList", businessinfoList);
		
	}
}
