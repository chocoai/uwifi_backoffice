package com.kdgz.uwifi.backoffice.interceptor;

import com.jfinal.aop.InterceptorStack;

/**
 * 店铺拦截器
 * @author 
 *
 */
public class ShopLayoutInterceptor extends InterceptorStack{

	@Override
	public void config() {
		addInterceptors(new ShopInterceptor());
	}

}
