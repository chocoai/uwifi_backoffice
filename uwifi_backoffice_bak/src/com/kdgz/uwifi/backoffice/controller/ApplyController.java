package com.kdgz.uwifi.backoffice.controller;
import java.util.Date;
import java.util.ResourceBundle;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ChannelInterceptor;
import com.kdgz.uwifi.backoffice.model.Apply;

@Before(ChannelInterceptor.class)
public class ApplyController extends BaseController {
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index(){
		
		String appName = getPara("appName");
		
		setAttr("applyList", Apply.dao.pageinfoList(getParaToInt(0, 1), 10, appName));
		setAttr("appName", appName);
		setAttr("uploadPath",bundle.getString("uploadPath"));
		flashRender("/admin/apply/apply_list.html");
	}
	
	public void addApply(){
		
		Apply apply = new Apply();
		
		if (getRequest().getMethod().equals("POST")) {
			
			Apply param = getModel(Apply.class);
			
			apply.set("appname", param.getStr("appname"));
			apply.set("appversion", param.getStr("appversion"));
			apply.set("imgurl", param.getStr("imgurl"));
			apply.set("fileurl", param.getStr("fileurl"));
			apply.set("applicationname", param.getStr("applicationname"));
			apply.set("appdescription", param.getStr("appdescription"));
			apply.set("appsize", param.getStr("appsize"));
			apply.set("delflag", 0);
			Date now = new Date();
			apply.set("addtime", now);
			apply.set("updatetime", now);
			
			Data retMsg = new Data();
			if (apply.save()) {
				
				retMsg.setStatus(Constants.OPERATION_SUCCEED);
				retMsg.setMsg("添加应用成功！");
				setFlashData(retMsg);
				renderJson(retMsg);
			}
			
			
		}else {
			setAttr("uploadPath",bundle.getString("uploadPath"));
			render("/admin/apply/add_apply.html");
		}
	}
	
	public void editApply(){
		
		Apply apply = Apply.dao.findById(getParaToInt(0));
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		setAttr("currentPage", currentPage);
		if (getRequest().getMethod().equals("POST")) {
			
			Apply param = getModel(Apply.class);
			
			apply.set("appname", param.getStr("appname"));
			apply.set("appversion", param.getStr("appversion"));
			apply.set("imgurl", param.getStr("imgurl"));
			apply.set("fileurl", param.getStr("fileurl"));
			apply.set("applicationname", param.getStr("applicationname"));
			apply.set("appdescription", param.getStr("appdescription"));
			apply.set("appsize", param.getStr("appsize"));
			
			Data retMsg = new Data();
			if (apply.update()) {
				
				retMsg.setStatus(Constants.OPERATION_SUCCEED);
				retMsg.setMsg("修改应用成功！");
				setFlashData(retMsg);
				renderJson(retMsg);
			}
			
		}else {
			setAttr("apply", apply);
			setAttr("uploadPath",bundle.getString("uploadPath"));
			render("/admin/apply/edit_apply.html");
		}
	}
	
	/**
	 * 删除应用信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码

		if (id != 0) {

			try {

				Apply apply = Apply.dao.findById(id);
				if(apply != null){
					
					apply.set("delflag", 1);
					Data rst = new Data();
					if(apply.update()) {
						
						rst.setStatus(Constants.OPERATION_SUCCEED);
						rst.setMsg("删除应用成功！");
					}else{
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("删除应用失败！");
					}
					setFlashData(rst);
				}
				

			} catch (Exception e) {
				throw e;
			}

			redirect("/apply/"+currentPage);
		}
	}
}
