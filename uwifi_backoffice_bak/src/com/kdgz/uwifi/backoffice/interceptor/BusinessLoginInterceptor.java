package com.kdgz.uwifi.backoffice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

/**
 * 用户权限拦截器
 *
 */
public class BusinessLoginInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Boolean isBusiness = (Boolean) ai.getController()
				.getSessionAttr("isBusiness");
		

		if (isBusiness == null || isBusiness.equals(Boolean.FALSE.booleanValue()) ) {
			ai.getController().redirect("/site/index");
		} else {
			ai.getController().removeSessionAttr("isBusiness");
			ai.invoke();
		}
	}

}
