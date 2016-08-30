package com.kdgz.uwifi.backoffice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Siteclassify;
import com.kdgz.uwifi.backoffice.model.Sitecontent;

@Before(ShopLayoutInterceptor.class)
public class ContentController extends BaseController{
	
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index(){
		String classids = getClassifyIds();
//		String title = getPara("title");
		setAttr("sitecontentinfoPage", Sitecontent.dao.pageSitecontent(getParaToInt(0, 1), 10, classids));
		setAttr("uploadPath",bundle.getString("uploadPath"));
//		setAttr("title", title);
		flashRender("/admin/website/content/list_content.html");
	}
	
	public void addContent(){
		
		Sitecontent sitecontent = new Sitecontent();
		
		if(getRequest().getMethod().equals("POST")) {
			Sitecontent param = getModel(Sitecontent.class);
			sitecontent.set("classid", param.getInt("classid"));
			sitecontent.set("title", param.getStr("title"));
//			sitecontent.set("text", param.getStr("text"));
			sitecontent.set("info", param.getStr("info"));
//			sitecontent.set("pic", param.getStr("pic"));
//			sitecontent.set("showpic", param.getInt("showpic"));
//			sitecontent.set("url", param.getStr("url"));
			sitecontent.set("sort", param.getInt("sort"));
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			sitecontent.set("addtime", time);
			sitecontent.set("updatetime", time);
			Data rst = new Data();
			if(sitecontent.save()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
			    rst.setMsg("添加文章内容成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);	
				rst.setMsg("添加文章内容失败！");
			}
			setFlashData(rst);
			redirect("/content");
			
		}else{
			getSelectinfo();
			render("/admin/website/content/add_content.html");
		}
	}
	
	public void editContent(){
		Integer id = getParaToInt("id");
		Sitecontent sitecontent = Sitecontent.dao.findById(id);
		
		int currentPage = getParaToInt(0,1);//获取当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			Sitecontent param = getModel(Sitecontent.class);
			sitecontent.set("classid", param.getInt("classid"));
			sitecontent.set("title", param.getStr("title"));
//			sitecontent.set("text", param.getStr("text"));
			sitecontent.set("info", param.getStr("info"));
//			sitecontent.set("pic", param.getStr("pic"));
//			sitecontent.set("showpic", param.getInt("showpic"));
//			sitecontent.set("url", param.getStr("url"));
			sitecontent.set("sort", param.getInt("sort"));
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			sitecontent.set("updatetime", time);
			Data rst = new Data();
			if(sitecontent.update()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("修改文章内容成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("修改文章内容失败！");
			}
			setFlashData(rst);
			redirect("/content/"+currentPage);
			
		}else{
			setAttr("sitecontent", sitecontent);
			geteditSelectinfo(sitecontent.getInt("classid"));
			setAttr("currentPage", currentPage);
			render("/admin/website/content/edit_content.html");
		}
	}
	
	/**
	 * 删除分类内容信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			try {

				Sitecontent sitecontent = Sitecontent.dao.findById(id);
				if(sitecontent != null){
					
					sitecontent.set("delflag", 1);
					Data rst = new Data();
					if(sitecontent.update()) {
						
						rst.setStatus(Constants.OPERATION_SUCCEED);
						rst.setMsg("删除文章信息成功！");
					}else{
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("删除文章信息失败！");
					}
					setFlashData(rst);
				}
				

			} catch (Exception e) {
				throw e;
			}

			int currentPage = getParaToInt(1, 1);//获取当前页码
			redirect("/content/"+currentPage);
		}
	}
	/**
	 * 递归过滤
	 */
	public List<List<Siteclassify>> getClassify(){
	
		Integer businessid = getSessionAttr("shopid");
		List<Siteclassify> listAll = Siteclassify.dao.selectclassifyList(businessid);
		List<List<Siteclassify>> classifyList = new ArrayList<List<Siteclassify>>();
		if(listAll != null && listAll.size() > 0){
			for(int i = 0; i< listAll.size(); i++){
				Siteclassify siteclassify = listAll.get(i);
				List<Siteclassify> list = filterClassify(siteclassify.getInt("id"));
				classifyList.add(list);
			}
		}
		return classifyList;
		
	}
	public List<Siteclassify> filterClassify(Integer id){
		
		List<Siteclassify> classifyList = new ArrayList<Siteclassify>();
		List<Siteclassify> list = Siteclassify.dao.selectsubByFid(id);
		if(list != null && list.size() > 0){
			for(int i = 0;i < list.size(); i++){
				Siteclassify siteclassify2 = list.get(i);
				filterClassify(siteclassify2.getInt("id"));
			}
			
		}else{
			Siteclassify siteclassify = Siteclassify.dao.findById(id);
			classifyList.add(siteclassify);
		}
		return classifyList;
		
	}
	
	/**
	 * 获取分类列表
	 */
	public void getSelectinfo(){
		
		List<List<Siteclassify>> list = getClassify();
		List<Siteclassify> classifyList = new ArrayList<Siteclassify>();
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List<Siteclassify> siteclassifylist = (List<Siteclassify>)list.get(i);
				if(siteclassifylist != null && siteclassifylist.size() > 0){
					for(int j = 0; j< siteclassifylist.size(); j++){
						Siteclassify siteclassify = siteclassifylist.get(j);
						Sitecontent sitecontent = Sitecontent.dao.selectContent(siteclassify.getInt("id"));
						if(sitecontent != null ){
							continue;
						}
						classifyList.add(siteclassify);
					}
				}
			
			}
		}
		setAttr("classifyList", classifyList);
		
	}
	public void geteditSelectinfo(Integer classid){
		
		List<List<Siteclassify>> list = getClassify();
		List<Siteclassify> classifyList = new ArrayList<Siteclassify>();
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List<Siteclassify> siteclassifylist = (List<Siteclassify>)list.get(i);
				if(siteclassifylist != null && siteclassifylist.size() > 0){
					for(int j = 0; j < siteclassifylist.size(); j++){
						Siteclassify siteclassify = siteclassifylist.get(j);
						Sitecontent sitecontent = Sitecontent.dao.selectContent(siteclassify.getInt("id"));
						if(sitecontent != null ){
							continue;
						}
						classifyList.add(siteclassify);
					}
				}
				
			}
		}
		Siteclassify siteclassify = Siteclassify.dao.findById(classid);
		setAttr("classifyList", classifyList);
		setAttr("siteclassify", siteclassify);
		
	}
	/**
	 * 获取分类列表id
	 */
	public String getClassifyIds(){
		
		List<List<Siteclassify>> list = getClassify();
		StringBuilder sb = new StringBuilder();
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				List<Siteclassify> siteclassifylist = (List<Siteclassify>)list.get(i);
				if(siteclassifylist != null && siteclassifylist.size() > 0){
					for(int j = 0; j < siteclassifylist.size(); j++){
						Siteclassify siteclassify = siteclassifylist.get(j);
						sb.append(siteclassify.getInt("id"));
						sb.append(",");
					}
					
				}
			}
		}
		String classid = sb.toString();
		if(sb.length() > 0){
			classid = classid.substring(0, classid.length() - 1);
		}
		return classid;
	}
}
