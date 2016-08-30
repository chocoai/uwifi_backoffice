package com.kdgz.uwifi.backoffice.controller;

import java.util.List;
import java.util.ResourceBundle;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Sitecatemenu;
import com.kdgz.uwifi.backoffice.model.Siteinfo;

@Before(ShopLayoutInterceptor.class)
public class NavigateMenuController extends BaseController{
	
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index(){
		Integer businessid = getSessionAttr("shopid");
		Siteinfo siteinfo = Siteinfo.dao.getSiteinfoByBusinessid(businessid);
		setAttr("siteinfo", siteinfo);
		setAttr("sitecatemenuinfoPage", Sitecatemenu.dao.pageSitecatemenu(getParaToInt(0, 1), 10, 0, businessid));
		setAttr("uploadPath",bundle.getString("uploadPath"));
		setAttr("businessid", businessid);
		flashRender("/admin/website/menu/list_menu.html");
	}
	
	/**
	 * 子菜单
	 */
	public void subMenuList(){
		
		Integer menuId = 0;
		//Integer id = getParaToInt("id");
		Integer id = getParaToInt(1,0);
		Integer fid = getParaToInt("fid");
		menuId = id;
		if(fid != null){
			menuId = fid;
		}
		Integer businessid = getSessionAttr("shopid");
		Sitecatemenu sitecatemenu = Sitecatemenu.dao.findById(fid==null?id:fid);
		setAttr("sitecatemenuinfoPage", Sitecatemenu.dao.pageSitecatemenu(getParaToInt(0, 1), 10, menuId, businessid));
		setAttr("uploadPath",bundle.getString("uploadPath"));
		setAttr("sitecatemenu", sitecatemenu);
		setAttr("isSubMenu","subMenuList/");
		setAttr("id",fid==null?id:fid);
		flashRender("/admin/website/menu/list_menu.html");
	}
	
	public void addBottomMenu(){
		

		Integer id = getParaToInt("id");
		Sitecatemenu sitecatemenu = new Sitecatemenu();
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		if(getRequest().getMethod().equals("POST")) {
			Sitecatemenu param = getModel(Sitecatemenu.class);
			sitecatemenu.set("businessid", businessid);
			if(id == null){
				sitecatemenu.set("fid", 0);
			}else{
				sitecatemenu.set("fid", id);
			}
			sitecatemenu.set("name", param.getStr("name"));
			sitecatemenu.set("img", param.getStr("img"));
			sitecatemenu.set("url", param.getStr("url"));
			sitecatemenu.set("sort", param.getInt("sort"));
			sitecatemenu.set("status", param.getInt("status"));
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			sitecatemenu.set("addtime", time);
			sitecatemenu.set("updatetime", time);
			Data rst = new Data();
			if(sitecatemenu.save()){
				if(id == null){
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("添加菜单成功！");
				}else{
					rst.setStatus(Constants.OPERATION_SUCCEED);
				    rst.setMsg("添加子菜单成功！");
				}
			}else{
				if(id == null){
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加菜单失败！");
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加子菜单失败！");
				}
				
			}
			setFlashData(rst);
			if(id == null){
				redirect("/navigatemenu");
			}else{
				redirect("/navigatemenu/subMenuList?fid="+id);
			}
			
			
		}else{
			getSelectinfo();
			setAttr("uploadPath",bundle.getString("uploadPath"));
			setAttr("subid", id);
			render("/admin/website/menu/add_menu.html");
		}
		
	}
	
	public void editBottomMenu(){
		Integer id = getParaToInt("id");
		Integer fid = getParaToInt("fid");
		Sitecatemenu sitecatemenu = Sitecatemenu.dao.findById(id);
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			Sitecatemenu param = getModel(Sitecatemenu.class);
			sitecatemenu.set("name", param.getStr("name"));
			sitecatemenu.set("img", param.getStr("img"));
			sitecatemenu.set("url", param.getStr("url"));
			sitecatemenu.set("sort", param.getInt("sort"));
			sitecatemenu.set("status", param.getInt("status"));
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			sitecatemenu.set("updatetime", time);
			Data rst = new Data();
			if(sitecatemenu.update()){
				if(fid != null && fid == 0){
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("修改菜单成功！");
				}else{
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("修改子菜单成功！");
				}
			}else{
				if(fid != null && fid == 0){
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("修改菜单失败！");
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("修改子菜单失败！");
				}
			}
			setFlashData(rst);
			if(fid != null && fid == 0){
				redirect("/navigatemenu/"+currentPage);
			}else{
				redirect("/navigatemenu/subMenuList/"+currentPage+"?fid="+fid);
			}
			
		}else{
			setAttr("sitecatemenu", sitecatemenu);
			getSelectinfo();
			setAttr("uploadPath",bundle.getString("uploadPath"));
			setAttr("currentPage",currentPage);//设置当前页码
			render("/admin/website/menu/edit_menu.html");
		}
	}
	
	public void selectMenuStyle(){
		
		String radiogroup = getPara("radiogroup");
		Integer businessid = getSessionAttr("shopid");
		Siteinfo siteinfo = Siteinfo.dao.getSiteinfoByBusinessid(businessid);
		
		if(siteinfo != null){
			siteinfo.set("businessid", businessid);
			siteinfo.set("radiogroup", radiogroup);
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteinfo.set("updatetime", time);
			if(siteinfo.update()){
				renderJson("infoMsg", "修改菜单风格成功");
			}else{
				renderJson("infoMsg", "修改菜单风格失败");
			}
			
		}else{
			siteinfo = new Siteinfo();
			siteinfo.set("businessid", businessid);
			siteinfo.set("radiogroup", radiogroup);
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteinfo.set("addtime", time);
			siteinfo.set("updatetime", time);
			if(siteinfo.save()){
				renderJson("infoMsg", "保存菜单风格成功");
			}else{
				renderJson("infoMsg", "保存菜单风格失败");
			}
		}
	}
	
	public void modifyCopyright(){
		
		String copyright = getPara("copyright");
		String plugmenucolor = getPara("plugmenucolor");
		Integer businessid = getSessionAttr("shopid");
		Siteinfo siteinfo = Siteinfo.dao.getSiteinfoByBusinessid(businessid);
		
		if(siteinfo != null){
			siteinfo.set("businessid", businessid);
			siteinfo.set("copyright", copyright);
			siteinfo.set("plugmenucolor", plugmenucolor);
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteinfo.set("updatetime", time);
			if(siteinfo.update()){
				renderJson("infoMsg", "修改菜单颜色与版权成功");
			}else{
				renderJson("infoMsg", "修改菜单颜色与版权失败");
			}
			
		}else{
			siteinfo = new Siteinfo();
			siteinfo.set("businessid", businessid);
			siteinfo.set("copyright", copyright);
			siteinfo.set("plugmenucolor", plugmenucolor);
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteinfo.set("addtime", time);
			siteinfo.set("updatetime", time);
			if(siteinfo.save()){
				renderJson("infoMsg", "保存菜单颜色与版权成功");
			}else{
				renderJson("infoMsg", "保存菜单颜色与版权失败");
			}
		}
	}
	
	/**
	 * 删除菜单信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt("id");
		Integer fid = getParaToInt("fid");
		if (id != 0) {

			try {

				deleteSitecatemenu(id);

			} catch (Exception e) {
				throw e;
			}
			int currentPage = getParaToInt("pageNo",1);//获取当前页码
			if(fid != null && fid == 0){
				redirect("/navigatemenu/"+currentPage);
			}else{
				redirect("/navigatemenu/subMenuList/"+currentPage+"?fid="+fid);
			}
		}
	}
	
	/**
	 * 递归删除
	 */
	public void deleteSitecatemenu(Integer id){
	
		//删除分类
		Data rst = new Data();
		Sitecatemenu sitecatemenu = Sitecatemenu.dao.findById(id);
		if(sitecatemenu != null){
			sitecatemenu.set("delflag", 1);
			if(sitecatemenu.update()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除菜单信息成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("删除菜单信息失败！");
			}
		}
		List<Sitecatemenu> list = Sitecatemenu.dao.selectsubByFid(id);
		if(list != null && list.size() > 0){
			for(int i = 0;i < list.size(); i++){
				Sitecatemenu sitecatemenu2 = list.get(i);
				
				deleteSitecatemenu(sitecatemenu2.getInt("id"));
				
			}
			
		}
		setFlashData(rst);
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
