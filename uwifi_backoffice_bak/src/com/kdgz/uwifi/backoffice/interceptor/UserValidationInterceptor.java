package com.kdgz.uwifi.backoffice.interceptor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Userbiz;

/**
 * 用户权限拦截器
 *
 */
public class UserValidationInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Boolean islogin = (Boolean) ai.getController()
				.getSessionAttr("isLogin");
		

		if (islogin == null || islogin.equals(Boolean.FALSE.booleanValue()) ) {
			ai.getController().redirect("/site/index");
		} else {
			// 将账号关联的店铺Id存入Session
			// 登录昵称
			String loginName = ai.getController().getSessionAttr("loginName");
			String sessionBusIds = ai.getController().getSessionAttr("businessids");
			if(StringUtils.isEmpty(sessionBusIds)) {
				StringBuilder sb = new StringBuilder();
				// 超级管理员账号 
				if(Constants.ADMIN_USERNAME.equals(loginName)) {
					List<Businessinfo> businessList = Businessinfo.dao.getBussinessIdList();
					
					for (Businessinfo businessinfo : businessList) {
						sb.append(businessinfo.getInt("id"));
						sb.append(",");
					}
					
				} else {
					
					Integer userid = ai.getController().getSessionAttr("userid");
					List<Userbiz> listBus = Userbiz.dao.selectBusinessByUid(userid);
					if (listBus != null && listBus.size() > 0) {
						for (int i = 0; i < listBus.size(); i++) {
							Userbiz userbiz = listBus.get(i);
							sb.append(userbiz.getInt("businessid"));
							sb.append(",");
						}
					}
				}
				String businessids = sb.toString();
				if (sb.length() > 0) {
					businessids = businessids.substring(0, businessids.length() - 1);
				}
				ai.getController().setSessionAttr("businessids", businessids);
			}
			
			ai.invoke();
		}
	}

}
