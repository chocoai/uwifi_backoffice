package com.kdgz.uwifi.backoffice.controller;

import java.util.ResourceBundle;
import com.jfinal.aop.Before;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Siteinfo;
import com.kdgz.uwifi.backoffice.render.MimeTypeRender;

/**
 * 模板首页
 */
@Before(ShopLayoutInterceptor.class)
public class ModuleController extends BaseController {
	
	ResourceBundle bundle = ResourceBundle.getBundle("common");
	
	/**
	 * 列表
	 */
	public void index() {
		
		Integer businessid = getSessionAttr("shopid");
		Siteinfo siteinfo = Siteinfo.dao.getSiteinfoByBusinessid(businessid);
		setAttr("siteinfo", siteinfo);
		render("/admin/website/module/list_module.html");
	}
	
	public void selectModule(){
		
		String tpltypeid = getPara("tpltypeid");
		Integer businessid = getSessionAttr("shopid");
		Siteinfo siteinfo = Siteinfo.dao.getSiteinfoByBusinessid(businessid);
		
		if(siteinfo != null){
			siteinfo.set("businessid", businessid);
			siteinfo.set("tpltypeid", tpltypeid);
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteinfo.set("updatetime", time);
			if(siteinfo.update()){
				renderJson("infoMsg", "修改模板成功");
			}else{
				renderJson("infoMsg", "修改模板失败");
			}
			
		}else{
			siteinfo = new Siteinfo();
			siteinfo.set("businessid", businessid);
			siteinfo.set("tpltypeid", tpltypeid);
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteinfo.set("addtime", time);
			siteinfo.set("updatetime", time);
			if(siteinfo.save()){
				renderJson("infoMsg", "保存模板成功");
			}else{
				renderJson("infoMsg", "保存模板失败");
			}
		}
	}
	
	public void generateCode(){
		int businessid = getSessionAttr("shopid");
		
		String url = bundle.getString("portalurl")+"?businessid="+businessid;
		
		render(new MimeTypeRender(url,true));
	}
	
}
