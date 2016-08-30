package com.kdgz.uwifi.backoffice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

/**
 * 用户权限拦截器
 * @author lanbo
 *
 */
public class AdminRoleInterceptor implements Interceptor{

	@Override
	public void intercept(ActionInvocation ai) {

		Boolean isAdmin = (Boolean) ai.getController()
				.getSessionAttr("roleCode");

		// 只有管理员才能用系统用户功能
		if (!isAdmin) {
			ai.getController().redirect("/account");
		} else {
			ai.invoke();
		}
	}
}
