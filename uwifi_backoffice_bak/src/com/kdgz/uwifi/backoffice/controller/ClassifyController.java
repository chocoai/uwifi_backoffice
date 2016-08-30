package com.kdgz.uwifi.backoffice.controller;

import java.util.List;
import java.util.ResourceBundle;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Lottery;
import com.kdgz.uwifi.backoffice.model.Siteclassify;

@Before(ShopLayoutInterceptor.class)
public class ClassifyController extends BaseController{
	
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index(){
		Integer businessid = getSessionAttr("shopid");
		setAttr("siteclassifyinfoPage", Siteclassify.dao.pageSiteclassify(getParaToInt(0, 1), 10, 0, businessid));
		setAttr("uploadPath",bundle.getString("uploadPath"));
		setAttr("businessid", businessid);
		flashRender("/admin/website/classify/list_classify.html");
	}
	/**
	 * 子分类列表
	 */
	public void subClassList(){
		Integer classId = 0;
		Integer id = getParaToInt(1,0);
		Integer fid = getParaToInt("fid");
		Integer businessid = getSessionAttr("shopid");
		classId = id;
		if(fid != null){
			classId = fid;
		}
		Siteclassify siteclassify = Siteclassify.dao.findById(fid==null?id:fid);
		setAttr("siteclassifyinfoPage", Siteclassify.dao.pageSiteclassify(getParaToInt(0, 1), 10, classId, businessid));
		setAttr("uploadPath",bundle.getString("uploadPath"));
		setAttr("siteclassify", siteclassify);
		setAttr("businessid", businessid);
		setAttr("isSubClass","subClassList/");
		setAttr("id",fid==null?id:fid);
		flashRender("/admin/website/classify/list_classify.html");
	}
	
	public void addClassify(){
		
		Integer id = getParaToInt("id");
		Siteclassify siteclassify = new Siteclassify();
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		if(getRequest().getMethod().equals("POST")) {
			Siteclassify param = getModel(Siteclassify.class);
			siteclassify.set("businessid", businessid);
			if(id == null){
				siteclassify.set("fid", 0);
			}else{
				siteclassify.set("fid", id);
			}
			siteclassify.set("name", param.getStr("name"));
			siteclassify.set("info", param.getStr("info"));
			siteclassify.set("img", param.getStr("img"));
			siteclassify.set("url", param.getStr("url"));
			siteclassify.set("sort", param.getInt("sort"));
			siteclassify.set("status", param.getInt("status"));
			siteclassify.set("tpid", param.getInt("tpid"));
			siteclassify.set("conttpid", param.getInt("conttpid"));
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteclassify.set("addtime", time);
			siteclassify.set("updatetime", time);
			Data rst = new Data();
			if(siteclassify.save()){
				if(id == null){
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("添加分类成功！");
				}else{
					rst.setStatus(Constants.OPERATION_SUCCEED);
				    rst.setMsg("添加子分类成功！");
				}
			}else{
				if(id == null){
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加分类失败！");
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("添加子分类失败！");
				}
				
			}
			setFlashData(rst);
			
			if(id == null){
				redirect("/classify"); 
			}else{
				redirect("/classify/subClassList?fid="+id);
			}
			
			
		}else{
			setAttr("uploadPath",bundle.getString("uploadPath"));
			setAttr("subid", id);
			render("/admin/website/classify/add_classify.html");
		}
	}
	
	public void editClassify(){
		Integer id = getParaToInt("id");
		Integer fid = getParaToInt("fid");
		Siteclassify siteclassify = Siteclassify.dao.findById(id);
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			Siteclassify param = getModel(Siteclassify.class);
			siteclassify.set("name", param.getStr("name"));
			siteclassify.set("info", param.getStr("info"));
			siteclassify.set("img", param.getStr("img"));
			siteclassify.set("url", param.getStr("url"));
			siteclassify.set("sort", param.getInt("sort"));
			siteclassify.set("status", param.getInt("status"));
			if(param.getInt("tpid") != null || param.getInt("conttpid") != null){
				siteclassify.set("tpid", param.getInt("tpid"));
				siteclassify.set("conttpid", param.getInt("conttpid"));
			}
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			siteclassify.set("updatetime", time);
			Data rst = new Data();
			
			if(siteclassify.update()){
				if(fid != null && fid == 0){
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("修改分类成功！");
				}else{
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("修改子分类成功！");
				}
			}else{
				if(fid != null && fid == 0){
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("修改分类失败！");
				}else{
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("修改子分类失败！");
				}
			}
			setFlashData(rst);
			
			if(fid != null && fid == 0){
				redirect("/classify/"+currentPage);
			}else{
				redirect("/classify/subClassList/"+currentPage+"-"+id+"?fid="+fid);
			}
			
		}else{
			setAttr("siteclassify", siteclassify);
			setAttr("uploadPath",bundle.getString("uploadPath"));
			setAttr("currentPage",currentPage);//设置当前页码
			render("/admin/website/classify/edit_classify.html");
		}
	}

	/**
	 * 删除分类信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		Integer id = getParaToInt("id");
		Integer fid = getParaToInt("fid");
		
		if (id != 0) {
			try {
				
				deleteClassify(id);

			} catch (Exception e) {
				throw e;
			}
			int currentPage = getParaToInt("pageNo",1);//获取当前页码
			if(fid != null && fid == 0){
				redirect("/classify/"+currentPage);
			}else{
				redirect("/classify/subClassList/"+currentPage+"?fid="+fid);
			}
		}
	}
	/**
	 * 递归删除
	 */
	public void deleteClassify(Integer id){
	
		//删除分类
		Data rst = new Data();
		Siteclassify siteclassify = Siteclassify.dao.findById(id);
		if(siteclassify != null){
			siteclassify.set("delflag", 1);
			if(siteclassify.update()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除分类信息成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("删除分类信息失败！");
			}
		}
		List<Siteclassify> list = Siteclassify.dao.selectsubByFid(id);
		if(list != null && list.size() > 0){
			for(int i = 0;i < list.size(); i++){
				Siteclassify siteclassify2 = list.get(i);
				
				deleteClassify(siteclassify2.getInt("id"));
				
			}
			
		}
		setFlashData(rst);
	}
	/**
	 * 获取分类模板
	 */
	public void getClassifyModule(){
		
		render("/admin/website/classify/classify_module.html");
	}
	/**
	 * 获取内容模板
	 */
	public void getContentModule(){
		
		render("/admin/website/classify/content_module.html");
	}
	/**
	 * 获取活动功能列表
	 */
	public void getActivityList(){
		
		Integer lotteryId = getParaToInt("lotteryId",0);
		Integer type = 0;
		if (lotteryId != 0){
			StringBuilder selectSql = new StringBuilder();
			selectSql.append(" select type from lottery where id=? ");
			Record typeRecord = Db.findFirst(selectSql.toString(), lotteryId);
			if (typeRecord != null) {
				type = typeRecord.getInt("type");
			}
		}
		setAttr("lotteryId", lotteryId);
		setAttr("lotteryType", type);
		Integer businessid = getSessionAttr("shopid");
		List<Lottery> listLottery = Lottery.dao.getLotteryListByBusinessid(businessid);
		setAttr("listLottery", listLottery);
		setAttr("activityUrl",bundle.getString("activityUrl"));
		render("/admin/website/classify/activity_list.html");
	}
}
