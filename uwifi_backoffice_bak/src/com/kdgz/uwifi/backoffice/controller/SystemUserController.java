package com.kdgz.uwifi.backoffice.controller;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.AdminRoleInterceptor;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Userinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;

/**
 * 系统用户
 * 
 * @author Lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class SystemUserController extends BaseController {

	/**
	 * 系统用户一览
	 */
	@Before(AdminRoleInterceptor.class)
	public void index() {
		
		int pageNum = 1;
		
		if(getPara(0) != null) {
			if(!getPara(0).equals("index")) {
				pageNum = getParaToInt(0, 1);
			}
		}

		setAttr("userPage", Userinfo.dao.paginate(pageNum, 10));

		if (getPara(1) != null) {
			setAttr("infoMsg", getPara(1));
		}
		flashRender("/admin/system_user/user_list.html");
	}
	
	/**
	 * 我的账户
	 */
	public void account() {
		
		String username = getSessionAttr("loginName");
		setAttr("userinfo", Userinfo.dao.getUserInfo(username));

		flashRender("/admin/user/myaccount.html");
	}
	
	/**
	 * 更新我的账户
	 */
	public void update() {

		Userinfo user = getModel(Userinfo.class);

		String salt = CommonUtil.getRandomString(8);

		user.set("salt", salt);

		user.set("password",
				CommonUtil.generateSaltPassword(user.getStr("password"), salt));

		user.set("updatetime", DateUtil.toYYYMMStr(new Date()));

		user.update();
		
		removeSessionAttr("pageJump");

//		setFlashData(Constants.OPERATION_SUCCEED, "保存成功！");

		redirect("/site/logout");
	}
	
	/**
	 * 删除用户
	 */
	@Before(AdminRoleInterceptor.class)
	public void delete() {

		int id = getParaToInt(0, 0);

		if (id != 0) {

			Userinfo user = Userinfo.dao.findById(id);

			Data rst = new Data();

			if (user.getStr("loginname").equals("admin")) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("管理员账户不能删除！");
			} else {
				
				
				
				
				Userinfo.dao.deleteById(id);
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("删除账号成功！");

			}
			setFlashData(rst);
			
			int currentPage = getParaToInt(1, 1);// 获取当前页数

			redirect("/sysUser/"+currentPage);
		}
	}

	/**
	 * 修改密码
	 */
	@Before(AdminRoleInterceptor.class)
	public void changePass() {

		if (getRequest().getMethod().equals("POST")) {
			Userinfo user = getModel(Userinfo.class);

			Userinfo currentUser = Userinfo.dao.findById(user.get("id"));

			String salt = CommonUtil.getRandomString(8);

			currentUser.set("salt", salt);

			currentUser.set("password", CommonUtil.generateSaltPassword(
					user.getStr("password"), salt));

			currentUser.set("updatetime", new Date());

			currentUser.update();

			Data rst = new Data();
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("修改密码成功！");
			setFlashData(rst);
			renderJson(rst);
		} else {

			Userinfo user = Userinfo.dao.findById(getParaToInt(0));

			if (user != null) {

				setAttr("userinfo", user);
				setAttr("currentPage",getParaToInt(1, 1));//设置当前页数
				render("/admin/system_user/change_pass.html");
			}
		}
	}

	/**
	 * 添加系统用户
	 */
	@Before(AdminRoleInterceptor.class)
	public void addUser() {

		Userinfo user = new Userinfo();

		if (getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);

			Data rst = new Data();
			
			// 验证是否已经添加过
			long count = Userinfo.dao.countExistUser(param.getStr("loginname"));

			if (count > 0) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("该账号已经存在，请重新输入！");
			}
			
			if (Constants.OPERATION_FAILED.equals(rst.getStatus())) {
				renderJson(rst);
				return;
			}
			
			user.set("loginname", param.getStr("loginname"));

			String salt = CommonUtil.getRandomString(8);
			user.set("password", CommonUtil.generateSaltPassword(
					param.getStr("password"), salt));
			user.set("salt", salt);

			user.set("rolecode", 0);
			user.set("nickname", param.getStr("nickname"));
			user.set("status", param.get("status") != null ? 1 : 0);
			Date now = new Date();
			user.set("addtime", now);
			user.set("updatetime", now);

			if (user.save()) {
				int userid = user.getInt("id");
				Record role = new Record();
				role.set("userid", userid);
				role.set("roleid", 2);
				Db.save("userrole", role); // 保存用户所具有的权限


				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("添加系统用户成功！");
				setFlashData(rst);
			} else {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("添加系统用户失败！");
			}
			renderJson(rst);
		} else {
			setAttr("userinfo", user);
			render("/admin/system_user/add_user.html");
		}
	}

	/**
	 * 编辑用户
	 */
	@Before(AdminRoleInterceptor.class)
	public void editUser() {
		if (getRequest().getMethod().equals("POST")) {
			Userinfo param = getModel(Userinfo.class);

			Data rst = new Data();
			if (Constants.ADMIN_USERNAME.equals(param.getStr("loginname"))
					&& !param.getBoolean("status").booleanValue()) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("管理员账户不能禁用！");
			} else {
				Userinfo user = Userinfo.dao.findById(param.getInt("id"));
				user.set("nickname", param.getStr("nickname"));
				user.set("status",
						param.get("status") != null? 1 : 0);
				Date now = new Date();
				user.set("updatetime", now);

				
				if (user.update()) {
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("编辑系统用户成功！");
				} else {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("编辑系统用户失败！");
				}
			}
			setFlashData(rst);
			renderJson(rst);

		} else {
			Userinfo user = Userinfo.dao.findById(getParaToInt(0));
			setAttr("userinfo", user);
			setAttr("currentPage",getParaToInt(1, 1));//设置当前页数
			render("/admin/system_user/edit_user.html");
		}

	}

	/**
	 * 更改用户状态
	 */
	@Before(AdminRoleInterceptor.class)
	public void changeStatus() {

		int id = getParaToInt(0);

		Userinfo user = Userinfo.dao.findById(id);

		Data rst = new Data();
		if (Constants.ADMIN_USERNAME.equals(user.getStr("username"))) {

			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("管理员账户的状态不能修改！");
		} else {
			user.set("status", user.getBoolean("status") ? 0 : 1);
			if (user.update()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("账号状态修改成功！");
			}
		}
		setFlashData(rst);
		int currentPage = getParaToInt(1, 1);// 获取当前页数
		redirect("/sysUser/"+currentPage);
	}

}
