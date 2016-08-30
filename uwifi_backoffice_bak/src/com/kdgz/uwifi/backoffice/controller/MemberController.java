package com.kdgz.uwifi.backoffice.controller;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Cuser;

@Before(ShopLayoutInterceptor.class)
public class MemberController extends BaseController{

	public void index(){
		
//		Integer businessid = getSessionAttr("shopid");
		setAttr("cuserinfoPage", Cuser.dao.pageCuser(getParaToInt(0, 1), 10));
		flashRender("/admin/website/member/list_member.html");
	}
	
	public void editCuser(){
		
		Integer id = getParaToInt(0, 0);
		
		Cuser cuser = Cuser.dao.findById(id);
		
		Integer businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		
		if(getRequest().getMethod().equals("POST")) {
			Cuser param = getModel(Cuser.class);
			cuser.set("nickname", param.getStr("nickname"));
			cuser.set("phoneno", param.getLong("phoneno"));
			cuser.set("password", param.getStr("password"));
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			cuser.set("updatetime", time);
			Data rst = new Data();
			if(cuser.update()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("修改会员信息成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("修改会员信息失败！");
			}
			setFlashData(rst);
			renderJson(rst);			
		}else{
			setAttr("cuser", cuser);
			setAttr("currentPage",getParaToInt(1,1));//设置默认页码 
			render("/admin/website/member/edit_member.html");
		}
	}
	
	/**
	 * 删除用户信息
	 * 
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void delete() throws Exception {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			try {

				Cuser cuser = Cuser.dao.findById(id);
				if(cuser != null){
					cuser.set("delflag", 1);
					Data rst = new Data();
					if(cuser.update()) {
						rst.setStatus(Constants.OPERATION_SUCCEED);
						rst.setMsg("删除用户信息成功！");
					}else{
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("删除用户信息失败！");
					}
					setFlashData(rst);
				}

			} catch (Exception e) {
				throw e;
			}
			int currentPage = getParaToInt(1,1);
			redirect("/member/"+currentPage);
		}
	}

}
