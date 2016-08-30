package com.kdgz.uwifi.backoffice.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ChannelInterceptor;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Roleinfo;
import com.kdgz.uwifi.backoffice.model.Userinfo;

@Before(ChannelInterceptor.class)
public class RouterController extends BaseController {

	public void index(){
		String roleid = getSessionAttr("roleid");
		String userid = getSessionAttr("userid")+"";
		String province = getSessionAttr("province");
		String city = getSessionAttr("city");
		//List<Userinfo> list = Userinfo.dao.getChannelList(roleid,province,city,userid);
		List<Userinfo> list  = Userinfo.dao.selectchannelNameByRoleidAndUidForRouter(roleid, userid);
		setAttr("channelList", list);
		
		
		String acbh = getPara("searchAcbh");
		Integer searchRole = getParaToInt("searchRole");
		Integer searchStatus =  getParaToInt("searchStatus");
		setAttr("searchAcbh", acbh);
		setAttr("searchRole", searchRole);
		setAttr("searchStatus", searchStatus);
		
		setAttr("ROLE", Constants.ROLE_SILVERCHANNEL);
		setAttr("CURROLE", roleid);
		setAttr("acinfoPage",
				Acinfo.dao.pageinfoList5(getParaToInt(0, 1), 10, roleid, acbh, searchRole, searchStatus, userid));
		
		flashRender("/admin/channel/router/list_router.html");
	}
}
