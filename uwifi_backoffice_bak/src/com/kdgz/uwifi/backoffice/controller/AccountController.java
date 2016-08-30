package com.kdgz.uwifi.backoffice.controller;

import java.util.Date;

import com.jfinal.aop.Before;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;

/**
 * 我的账户
 * 
 * @author lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class AccountController extends BaseController {

	public void index() {
		getUserInfo();

		flashRender("/admin/user/myaccount.html");
	}

	public void update() {

		Userinfo user = getModel(Userinfo.class);

		String salt = CommonUtil.getRandomString(8);

		user.set("salt", salt);

		user.set("password",
				CommonUtil.generateSaltPassword(user.getStr("password"), salt));

		user.set("updatetime", DateUtil.toYYYMMStr(new Date()));

		user.update();

		setFlashData(Constants.OPERATION_SUCCEED, "保存成功！");

		redirect("/account");
	}

	private void getUserInfo() {
		String username = getSessionAttr("loginName");
		setAttr("userinfo", Userinfo.dao.getUserInfo(username));
	}
	
	public void changePass() {
		
		if (getRequest().getMethod().equals("POST")) {
			Userinfo user = getModel(Userinfo.class);

			Userinfo currentUser = Userinfo.dao.findById(user.get("id"));

			String salt = CommonUtil.getRandomString(8);

			currentUser.set("salt", salt);

			currentUser.set("password", CommonUtil.generateSaltPassword(
					user.getStr("password"), salt));

			currentUser.set("updatetime", new Date());

			Data rst = new Data();
			if(currentUser.update()){
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("修改密码成功！");
			}
			renderJson(rst);
		} else {
			getUserInfo();
			setAttr("userid",getParaToInt(0));
			render("/admin/user/channelAccount.html");
			//flashRender("/admin/user/channelAccount.html");
		}
	}
}
