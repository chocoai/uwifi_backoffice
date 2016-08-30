package com.kdgz.uwifi.backoffice.interceptor;

import com.jfinal.aop.InterceptorStack;

/**
 * Layout拦截器
 * @author 
 *
 */
public class LayoutInterceptor extends InterceptorStack{

	@Override
	public void config() {
		addInterceptors(new UserValidationInterceptor(), new BreadcrumbAndMenuInterceptor());
	}

}
