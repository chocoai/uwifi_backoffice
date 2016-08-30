package com.kdgz.uwifi.backoffice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.BusinessLoginInterceptor;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;
import com.kdgz.uwifi.backoffice.util.WebServiceUtil;

/**
 * 工作台
 * 
 * @author lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class DashboardController extends BaseController {

	public void index() {
		

		// 最新消息
		String businessids = getSessionAttr("businessids");
		
		int userId = getSessionAttr("userid");
		String userRole = (Boolean)getSessionAttr("roleCode") ? Constants.ROLE_ADMIN:Constants.ROLE_NORMAL;
		
		// 首页信息
		List<Map<String, String>> dashData = WebServiceUtil.getDashboardData(businessids);
		
		List<Businessinfo> busList = Businessinfo.dao.selectNewstBussinessinfoList(businessids);
		
		List<Map<String, String>> news = new ArrayList<Map<String, String>>();
		
		// 广告统计
		long ad = 0;
		// 昨日到店
		long yesterdayusers = 0;
		// 最高在线
		long hightestusers = 0;
		// 昨日新增顾客
		long yesterdaynewusers = 0;
		// 超过7天未到店客户
		long outsevenday = 0;
		
		
		if(dashData.size() > 0) {
			
			for (Map<String, String> data : dashData) {
				if(StringUtils.isNotEmpty(data.get("ad"))) {
					ad += Long.valueOf(data.get("ad"));
				}
				if(StringUtils.isNotEmpty(data.get("yesterdayusers"))) {
					yesterdayusers += Long.valueOf(data.get("yesterdayusers"));
				}
				if(StringUtils.isNotEmpty(data.get("hightestusers"))) {
					hightestusers += Long.valueOf(data.get("hightestusers"));
				}
				if(StringUtils.isNotEmpty(data.get("yesterdaynewusers"))) {
					yesterdaynewusers += Long.valueOf(data.get("yesterdaynewusers"));
				}
				if(StringUtils.isNotEmpty(data.get("outsevenday"))) {
					outsevenday += Long.valueOf(data.get("outsevenday"));
				}
				Map<String, String> newsMap = new HashMap<String, String>();
				for (Businessinfo busInfo : busList) {
					
					if(busInfo.getInt("id") == Integer.parseInt(data.get("businessid"))) {
						newsMap.put("busname", busInfo.getStr("busname"));
						// 连续7天到店
						if(StringUtils.isNotEmpty(data.get("sevenday"))) {
							newsMap.put("sevenday", data.get("sevenday"));
						}
						// 累计用户
						if(StringUtils.isNotEmpty(data.get("sumusers"))) {
							newsMap.put("sumusers", data.get("sumusers"));
						}
						// 超过7天未到店
						if(StringUtils.isNotEmpty(data.get("outsevenday"))) {
							newsMap.put("outsevenday", data.get("outsevenday"));
						}
						Date addDate = busInfo.getDate("addtime");
						
						Date now = new Date();
						
						long daysBetween = DateUtil.getDaysBetween(DateUtils.toCalendar(addDate), DateUtils.toCalendar(now));
						newsMap.put("lastdays", String.valueOf(daysBetween+1));
						
						news.add(newsMap);
					}
				}
			}
		}
			
		setAttr("news", news);
		
		
		// 店铺及设备信息
		long shopCount = 0;
		if(StringUtils.isNotEmpty(businessids)) {
			shopCount = businessids.split(",").length;
		}
//		long shopCount = Businessinfo.dao.countShopNum(userId, userRole);
		
		long deviceCount = Businessinfo.dao.countWiFiDeviceNum(userId, userRole);
		
		// 广告统计信息
		setAttr("ad", ad);
		// 客流统计
		setAttr("yesterdayusers", yesterdayusers);
		// 最高顾客在线
		setAttr("hightestusers", hightestusers);
		// 当前在线客户数
		List<Map<String, String>> onlineUserData = WebServiceUtil.getOnlineUserCount(businessids);
		long presentUserCount = 0;
		if(onlineUserData.size() > 0) {
			presentUserCount = Long.valueOf(onlineUserData.get(0).get("count"));
		}
//		long presentUserCount = CommonUtil.countPresentWiFiUser(businessids);
		setAttr("presentUserCount", presentUserCount);
		
		// 昨日新增顾客
		setAttr("yesterdaynewusers", yesterdaynewusers);
		
		// 超过7天未到店客户
		setAttr("outsevenday", outsevenday);

		setAttr("shopCount", shopCount);
		setAttr("deviceCount", deviceCount);
		flashRender("/admin/site/index.html");
	}
}
