package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.bean.OnlineStatusDetail;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Bwlist;
import com.kdgz.uwifi.backoffice.model.Userbiz;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.StringUtil;
import com.kdgz.uwifi.backoffice.util.WebServiceUtil;

/**
 * 运行状态
 * 
 * @author lanbo
 * 
 */
@Before(LayoutInterceptor.class)
public class MyRouterController extends BaseController {

	private static final String MAC_REGEX = "^[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}$";

	private static final String IP_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

	private static final String DOMAIN_REGEX = "^[0-9a-zA-Z]+[0-9a-zA-Z\\.-]*\\.[a-zA-Z]{2,4}$";

	public void onlineStatus() {

		// 在线路由
		int userId = getSessionAttr("userid");
		String businessids = getSessionAttr("businessids");

		String userRole = (Boolean) getSessionAttr("roleCode") ? Constants.ROLE_ADMIN
				: Constants.ROLE_NORMAL;

		long deviceCount = Businessinfo.dao
				.countWiFiDeviceNum(userId, userRole);

		long onlineDeviceCount = 0;
		if(StringUtils.isNotEmpty(businessids)) {
			onlineDeviceCount = Businessinfo.dao.countOnlineDeviceNum(
					userRole, businessids);
		}

		setAttr("deviceCount", deviceCount);
		setAttr("onlineDeviceCount", onlineDeviceCount);

		getSelectinfo();

		String busId = getPara("busId", "");
		String acId = getPara("acId", "");
		String start = getPara("start", "");
		String end = getPara("end", "");
		Date now = new Date();
		if (StringUtils.isEmpty(start)) {
			Date lastSevenDate = DateUtils.addDays(now, -6);
			start = DateFormatUtils.ISO_DATE_FORMAT.format(lastSevenDate);
		}
		if (StringUtils.isEmpty(end)) {
			end = DateFormatUtils.ISO_DATE_FORMAT.format(now);
		}
		setAttr("busId", StringUtils.isEmpty(busId)?0:Integer.parseInt(busId));
		setAttr("acId", acId);
		setAttr("start", start);
		setAttr("end", end);

		if (StringUtils.isEmpty(busId)) {
			busId = businessids;
		}
		// 获取设备运行状态概况
		Map<String, String> result = (LinkedHashMap<String, String>) WebServiceUtil
				.getDeviceGeneralData(busId);

		if (result != null) {
			// 平均设备利用率
			setAttr("utilizationrate", new Formatter().format("%.2f",
					Float.parseFloat(result.get("utilizationrate")) * 100));
			// 平均流入流量
			setAttr("incoming", result.get("incoming"));
			// 平均流出流量
			setAttr("outcoming", result.get("outcoming"));
		} else {
			// 平均设备利用率
			setAttr("utilizationrate", 0.00);
			// 平均流入流量
			setAttr("incoming", 0);
			// 平均流出流量
			setAttr("outcoming", 0);
		}

		// 获取设备运营状态详情
		List<Map<String, String>> statusList = WebServiceUtil
				.getDeviceDetailData(busId, acId, start, end);

		List<OnlineStatusDetail> detailList = new ArrayList<OnlineStatusDetail>();
		for (Map<String, String> status : statusList) {
			OnlineStatusDetail detail = new OnlineStatusDetail();
			// ACId
			String acid = status.get("deviceid");
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT");
			sb.append(" A.eqptssid, B.busname, A.acid, TIMESTAMPDIFF(MINUTE, C.lastpingtime, now()) < 15 as online ");
			sb.append("FROM");
			sb.append(" acinfo A ");
			sb.append("LEFT JOIN businessinfo B ON A.businessid = B.id ");
			sb.append("LEFT JOIN acstatetemp C ON A.acid = C.acid ");
			sb.append("WHERE ");
			sb.append("A.acid = ?");

			Record acRecord = Db.findFirst(sb.toString(), acid);
			if (acRecord != null) {
				detail.setSsid(acRecord.getStr("eqptssid"));
				detail.setBusName(acRecord.getStr("busname"));

				if (acRecord.get("online") != null) {
					detail.setOnline(acRecord.getLong("online") == 1 ? true
							: false);
				} else {
					detail.setOnline(false);
				}
			}
			detail.setAcid(acid);
			// 累计使用时间
			if (StringUtils.isNotEmpty(status.get("accumulatedusetime"))) {
				long userTime = Long.valueOf(status.get("accumulatedusetime")) / 3600;
				detail.setAccumulatedusetime(String.valueOf(userTime));
			}
			if (StringUtils.isNotEmpty(status.get("lastdate"))) {
				detail.setLastdate(status.get("lastdate"));
			}
			if (StringUtils.isNotEmpty(status.get("incoming"))) {
				detail.setIncoming(Long.valueOf(status.get("incoming")));
			}
			if (StringUtils.isNotEmpty(status.get("outcoming"))) {
				detail.setOutcoming(Long.valueOf(status.get("outcoming")));
			}
			if (StringUtils.isNotEmpty(status.get("utilizationrate"))) {

				detail.setUtilizationrate(new Formatter().format("%.2f",
						Float.parseFloat(status.get("utilizationrate")) * 100)
						.toString());
			}

			detailList.add(detail);
		}

		int len = detailList.size();
		if(len >= 0 && len <= 10){
			setAttr("detailList", detailList);
		}else if (len>10){
			List<OnlineStatusDetail> tempList = new ArrayList<OnlineStatusDetail>();
			for (int i=0; i<10; i++){
				tempList.add(detailList.get(i));
			}
			setAttr("detailList", tempList);
		}

		setAttr("userId", userId);
		render("/admin/route_manage/online_status.html");
	}

	/**
	 * 在线用户
	 */
	public void onlineUser() {

		String busId = getPara("busId", "");
		String acId = getPara("acId", "");

		String serieName = "合计";
		if (StringUtils.isNotEmpty(busId) && StringUtils.isEmpty(acId)) {
			Businessinfo busInfo = Businessinfo.dao.findById(busId);

			if (busInfo != null) {
				serieName = busInfo.getStr("busname");
			}
		} else if (StringUtils.isNotEmpty(busId)
				&& StringUtils.isNotEmpty(acId)) {
			serieName = acId;
		}
		setAttr("serieName", serieName);
		
		setAttr("busId", StringUtils.isEmpty(busId)?0:Integer.parseInt(busId));
		setAttr("acId", acId);

		if (StringUtils.isEmpty(busId)) {
			busId = getSessionAttr("businessids");
		}

		// 获取今日在线客户概要分析
		Map<String, String> userStatus = WebServiceUtil
				.getUserdataTodayGenaral(busId, acId);

		boolean noData = false;
		if (userStatus != null) {
			
			if(StringUtils.isEmpty(userStatus.get("sixty")) && StringUtils.isEmpty(userStatus.get("ten")) && 
					StringUtils.isEmpty(userStatus.get("thirty")) && StringUtils.isEmpty(userStatus.get("zero"))) {
				noData = true;
			} else {
				if (StringUtils.isNotEmpty(userStatus.get("firstusercount"))) {
					setAttr("firstusercount", userStatus.get("firstusercount"));
				}
				int over30Time = 0;
				if (StringUtils.isNotEmpty(userStatus.get("sixty"))) {
					setAttr("sixty", userStatus.get("sixty"));
					
					over30Time+= Integer.parseInt(userStatus.get("sixty"));
				}
				if (StringUtils.isNotEmpty(userStatus.get("ten"))) {
					setAttr("ten", userStatus.get("ten"));
				}
				if (StringUtils.isNotEmpty(userStatus.get("thirty"))) {
					setAttr("thirty", userStatus.get("thirty"));
					
					over30Time+= Integer.parseInt(userStatus.get("thirty"));
				}
				if (StringUtils.isNotEmpty(userStatus.get("zero"))) {
					setAttr("zero", userStatus.get("zero"));
				}
				
				setAttr("over30Time", over30Time);
			}
			
		} else {
			noData = true;
		}
		setAttr("noData", noData);
		
		// 当前在线客户数
		List<Map<String, String>> onlineUserData = WebServiceUtil.getOnlineUserCount((String)getSessionAttr("businessids"));
		long presentUserCount = 0;
		if(onlineUserData.size() > 0) {
			presentUserCount = Long.valueOf(onlineUserData.get(0).get("count"));
		}
		//long presentUserCount = CommonUtil.countPresentWiFiUser((String)getSessionAttr("businessids"));
		setAttr("presentUserCount", presentUserCount);

		// 获取今日手机客户详细分析
		List<Map<String, String>> phoneUserList = WebServiceUtil
				.getUserdataTodayphone(busId, acId);

		getSelectinfo();
		setAttr("detailList", phoneUserList);
		render("/admin/route_manage/online_user.html");
	}

	/**
	 * 上网管理
	 */
	public void bwList() {

		String businessName = getPara("businessName");
		int whtype = getParaToInt("whtype", 0);
		Integer type = getParaToInt(1);
		if(type != null){
			whtype = type;
		}
		setSessionAttr("whtype", whtype);
		setAttr("whtype", whtype);
		String businessids = getSessionAttr("businessids");
		if (StringUtils.isNotBlank(businessName)) {
			setAttr("businessName", businessName);
		}
		
		int pageNumber = getParaToInt(0, 1);//
		setAttr("bwlistPage", Bwlist.dao.pageinfoList(pageNumber, 10,
				whtype,
				new int[] { Constants.CTLTYPE_MAC, Constants.CTLTYPE_IP },
				businessids, businessName));

		// 设置黑白名单数量
		setCommonBwCount(businessids);

		flashRender("/admin/route_manage/bwlist_list.html");
	}

	/**
	 * 添加黑白名单
	 */
	public void addBwList() {

		int whtype = getSessionAttr("whtype");

		Bwlist bwlist = new Bwlist();
		if (getRequest().getMethod().equals("POST")) {

			Bwlist param = getModel(Bwlist.class);
			bwlist.set("businessid", param.get("businessid"));
			bwlist.set("whtype", whtype);

			int controltype = param.getInt("controltype");
			String controlvalue = param.getStr("controlvalue");

			Data rst = new Data();
			if (Constants.CTLTYPE_MAC == controltype) {
				// 用正则表达式验证Mac是否有效
				Pattern pa = Pattern.compile(MAC_REGEX);
				if (!pa.matcher(controlvalue).find()) {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("输入的MAC地址不符合要求,如(48:5a:b6:9e:47:ff)");
				}
			} else if (Constants.CTLTYPE_IP == controltype) {
				// 用正则表达式验证IP是否有效
				Pattern pa = Pattern.compile(IP_REGEX);
				if (!pa.matcher(controlvalue).find()) {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("输入的IP地址不符合要求,如(192.168.10.30)");
				}
			}

			// 验证是否已经添加过
			long count = Bwlist.dao.countExistBwlist(whtype, controltype,
					controlvalue, param.getInt("businessid"), "");

			if (count > 0) {
				String msg = "该地址在黑名单中已经存在！";
				if(0 == whtype){
					msg = "该地址在白名单中已经存在！";
				}
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg(msg);
			}

			if (Constants.OPERATION_FAILED.equals(rst.getStatus())) {
				renderJson(rst);
				return;
			}

			bwlist.set("controltype", controltype);
			bwlist.set("controlvalue", controlvalue);
			bwlist.set("comment", param.getStr("comment"));

			if (bwlist.save()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("添加信息成功！");
				setFlashData(rst);
			} else {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("添加信息失败！");
			}
			
			renderJson(rst);
		} else {
			getSelectinfo();
			setAttr("whtype", whtype);
			setAttr("bwlist", bwlist);
			render("/admin/route_manage/add_bwlist.html");
		}
	}
	
	public void checkMacOrIp() {
		boolean valid = true;
		
//		int whtype = getSessionAttr("whtype");
		Integer controltype = getParaToInt("controltype",0);
		String controlvalue = getPara("bwlist.controlvalue","");
//		String businessid = getPara("businessid","");
//		long count = Bwlist.dao.countExistBwlist(whtype, controltype,
//				controlvalue, Integer.valueOf(businessid), "");
//		if(count > 0){
//			valid = false;
//		}
		
		if (Constants.CTLTYPE_MAC == controltype) {
			// 用正则表达式验证Mac是否有效
			Pattern pa = Pattern.compile(MAC_REGEX);
			if (!pa.matcher(controlvalue).find()) {
				valid = false;
			}
		} else if (Constants.CTLTYPE_IP == controltype) {
			// 用正则表达式验证IP是否有效
			Pattern pa = Pattern.compile(IP_REGEX);
			if (!pa.matcher(controlvalue).find()) {
				valid = false;
			}
		}
		renderJson("valid",valid);
	}

	/**
	 * 编辑黑白名单
	 */
	public void editBwList() {

		if (getRequest().getMethod().equals("POST")) {
			Bwlist param = getModel(Bwlist.class);

			Bwlist bwlist = Bwlist.dao.findById(param.getInt("id"));

			//15-01-23 jason 添加修改
			int whtype = param.get("whtype");
			
			bwlist.set("businessid", param.getInt("businessid"));
			bwlist.set("whtype", whtype);

			int controltype = param.getInt("controltype");
			String controlvalue = param.getStr("controlvalue");

			Data rst = new Data();
			if (Constants.CTLTYPE_MAC == controltype) {
				// 用正则表达式验证Mac是否有效
				Pattern pa = Pattern.compile(MAC_REGEX);
				if (!pa.matcher(controlvalue).find()) {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("输入的MAC地址不符合要求,如(48:5a:b6:9e:47:ff)");
				}
			} else if (Constants.CTLTYPE_IP == controltype) {
				// 用正则表达式验证IP是否有效
				Pattern pa = Pattern.compile(IP_REGEX);
				if (!pa.matcher(controlvalue).find()) {
					rst.setStatus(Constants.OPERATION_FAILED);
					rst.setMsg("输入的IP地址不符合要求,如(192.168.10.30)");
				}
			}
			// 验证是否已经添加过
			long count = Bwlist.dao.countExistBwlist(param.getInt("whtype"), controltype,
					controlvalue, param.getInt("businessid"), String.valueOf(param.getInt("id")));

			if (count > 0) {
				String msg = "该地址在黑名单中已经存在！";
				if(0 == whtype){
					msg = "该地址在白名单中已经存在！";
				}
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg(msg);
			}
						
			if (Constants.OPERATION_FAILED.equals(rst.getStatus())) {
				renderJson(rst);
				return;
			}

			bwlist.set("controltype", controltype);
			bwlist.set("controlvalue", controlvalue);
			bwlist.set("comment", param.getStr("comment"));

			if (bwlist.update()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("编辑信息成功！");
			} else {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("编辑信息失败！");
			}
			setFlashData(rst);
			renderJson(rst);
		} else {
			getSelectinfo();
			int id = getParaToInt(0);
			Bwlist bwlist = Bwlist.dao.findById(id);
			setAttr("bwlist", bwlist);
			setAttr("whtype", bwlist.get("whtype"));
			setAttr("currentPage",getParaToInt(1, 1));
			render("/admin/route_manage/edit_bwlist.html");
		}
	}

	/**
	 * 从客流导入
	 */
	public void importBwlist() {
		getSelectinfo();

		int businessid = getParaToInt("businessid", 0);

		int whtype = getParaToInt(0, Constants.WHTYPE_WHITE);
		setAttr("whtype", whtype);

		setAttr("businessid", businessid);

		String businessids = "";

		String start = getPara("start", "");
		String end = getPara("end", "");

		Date now = new Date();
		if (StringUtils.isEmpty(start)) {
			Date lastSevenDate = DateUtils.addDays(now, -6);
			start = DateFormatUtils.ISO_DATE_FORMAT.format(lastSevenDate);
		}
		if (StringUtils.isEmpty(end)) {
			end = DateFormatUtils.ISO_DATE_FORMAT.format(now);
		}
		setAttr("start", start);
		setAttr("end", end);

		int arriveTimes = getParaToInt("arriveTimes", 0);
		int avagTime = getParaToInt("avagTime", 0);

		setAttr("arriveTimes", arriveTimes == 0 ? "" : arriveTimes);
		setAttr("avagTime", avagTime == 0 ? "" : avagTime);

		if (getRequest().getMethod().equals("POST")) {

			if (businessid == 0) {
				businessids = getSessionAttr("businessids");
			} else {
				businessids = String.valueOf(businessid);
			}

			List<Map<String, String>> userList = WebServiceUtil.getBwUserList(
					businessids, arriveTimes, avagTime, start, end, Constants.DATA_LIMIT);
			List<Map<String, String>> list = WebServiceUtil.getBwUserList(
					businessids, arriveTimes, avagTime, start, end, Constants.DATA_LIMIT);
			if (userList.size() > Constants.DATA_LIMIT) {
				Data rst = new Data();
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("数据量过大，请调整查询条件!");
				setFlashData(rst);
				setAttr("userList", new ArrayList<Map<String, String>>());
			} else {

				for (Map<String, String> map : userList) {
					int busId = Integer.parseInt(map.get("businessid"));

					List<Businessinfo> busList = Businessinfo.dao
							.selectBussinessinfoList(businessids);

					for (Businessinfo businessinfo : busList) {
						if (busId == businessinfo.getInt("id")) {
							map.put("busname", businessinfo.getStr("busname"));
						}
					}
					
				}
				if(list != null && list.size() > 0){
					for(int i = list.size() - 1; i >= 0; i--){
						Map<String, String> map = list.get(i);
						int busId = Integer.parseInt(map.get("businessid"));
						String mac = map.get("mac");
						Bwlist bwlist = Bwlist.dao
								.selectBwlistBybusinessidAndControlvalue(busId,mac);
						if (bwlist != null) {
							userList.remove(i);
						}
					}
				}

				setAttr("userList", userList);
			}
		} else {
			setAttr("userList", new ArrayList<Map<String, String>>());
		}

		flashRender("/admin/route_manage/import_bwlist.html");
	}

	/**
	 * 从客流导入mac黑白名单
	 */
	public void addBwListByImport() {

		String[] bwList = getParaValues("bwList");

		String[] comment = getParaValues("comment");

		int whtype = getParaToInt(0, Constants.WHTYPE_WHITE);

		List<Bwlist> modelList = new ArrayList<Bwlist>();
		String sql = "Insert into bwlist(businessid, whtype, controltype, controlvalue, comment) values (?,?,?,?,?)";

		Data rst = new Data();

		for (int i = 0; i < bwList.length; i++) {

			String[] bwArr = StringUtils.split(bwList[i], ";");

			String controlvalue = bwArr[0];
			String businessid = bwArr[1];

			Bwlist tempBw = new Bwlist();
			tempBw.set("businessid", businessid);
			tempBw.set("whtype", whtype);
			tempBw.set("controltype", Constants.CTLTYPE_MAC);
			tempBw.set("controlvalue", controlvalue);
			tempBw.set("comment", comment[i]);

			modelList.add(tempBw);
		}

		Db.batch(sql, "businessid, whtype, controltype, controlvalue, comment",
				modelList, modelList.size());

		rst.setStatus(Constants.OPERATION_SUCCEED);
		rst.setMsg("导入信息成功！");
		setFlashData(rst);
		
		renderJson(rst);
	}

	/**
	 * 添加域名白名单
	 */
	public void addDomainList() {

		if (getRequest().getMethod().equals("POST")) {

			String businessid = getPara("businessid");
			String controlvalue = getPara("controlvalue");

			String[] controlArr = StringUtils.split(controlvalue, "\n");

			HashSet<Bwlist> modelSet = new HashSet<Bwlist>();
			String sql = "Insert into bwlist(businessid, whtype, controltype, controlvalue, comment) values (?,?,?,?,?)";

			Data rst = new Data();
			if (StringUtils.isBlank(businessid)) {
				businessid = (String) getSessionAttr("businessids");

				String[] busArr = StringUtils.split(businessid, ",");

				for (String busId : busArr) {

					for (String domain : controlArr) {
						// 用正则表达式验证Mac是否有效
						Pattern pa = Pattern.compile(DOMAIN_REGEX);
						if (!pa.matcher(domain).find()) {
							rst.setStatus(Constants.OPERATION_FAILED);
							rst.setMsg("输入的域名不符合要求,如(www.baidu.com)");
							renderJson(rst);
							return;
						}

						Bwlist bwlist = Bwlist.dao.selectBwlistBybusinessidAndControlvalue(Integer.valueOf(busId), domain);
						if(bwlist != null){
							continue;
						}else{
							bwlist = new Bwlist();
							bwlist.set("businessid", busId);
							bwlist.set("whtype", Constants.WHTYPE_WHITE);
							bwlist.set("controltype", Constants.CTLTYPE_DOMAIN);
							bwlist.set("controlvalue", domain);
							bwlist.set("comment", "");

							modelSet.add(bwlist);
						}
						
					}
				}
			} else {

				for (String domain : controlArr) {
					// 用正则表达式验证Mac是否有效
					Pattern pa = Pattern.compile(DOMAIN_REGEX);
					if (!pa.matcher(domain).find()) {
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("输入的域名不符合要求,如(www.baidu.com)");
						renderJson(rst);
						return;
					}
					
					// 验证是否已经添加过
					long count = Bwlist.dao.countExistBwlist(Constants.WHTYPE_WHITE, Constants.CTLTYPE_DOMAIN,
							domain, Integer.parseInt(businessid), "");

					if (count > 0) {
						rst.setStatus(Constants.OPERATION_FAILED);
						rst.setMsg("添加的域名信息已经存在，请重新输入！");
					}

					if (Constants.OPERATION_FAILED.equals(rst.getStatus())) {
						renderJson(rst);
						return;
					}
					
					Bwlist bwlist = new Bwlist();
					bwlist.set("businessid", businessid);
					bwlist.set("whtype", Constants.WHTYPE_WHITE);
					bwlist.set("controltype", Constants.CTLTYPE_DOMAIN);
					bwlist.set("controlvalue", domain);
					bwlist.set("comment", "");

					modelSet.add(bwlist);
				}

			}
			List<Bwlist> modelList = new ArrayList<Bwlist>(modelSet);
			Db.batch(sql,
					"businessid, whtype, controltype, controlvalue, comment",
					modelList, modelList.size());

			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("添加域名信息成功！");
			setFlashData(rst);
			renderJson(rst);
		} else {
			getSelectinfo();
			render("/admin/route_manage/add_domainlist.html");
		}
	}

	/**
	 * 编辑黑白名单
	 */
	public void editDomainList() {

		if (getRequest().getMethod().equals("POST")) {
			Bwlist param = getModel(Bwlist.class);

			Bwlist bwlist = Bwlist.dao.findById(param.getInt("id"));

			bwlist.set("businessid", param.getInt("businessid"));
			bwlist.set("whtype", param.get("whtype"));

			String controlvalue = param.getStr("controlvalue");

			Data rst = new Data();
			// 用正则表达式验证域名是否有效
			Pattern pa = Pattern.compile(DOMAIN_REGEX);
			if (!pa.matcher(controlvalue).find()) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("输入的域名不符合要求,如(www.baidu.com)");
			}
			
			// 验证是否已经添加过
			long count = Bwlist.dao.countExistBwlist(Constants.WHTYPE_WHITE, Constants.CTLTYPE_DOMAIN,
					controlvalue, param.getInt("businessid"), String.valueOf(param.getInt("id")));

			if (count > 0) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("修改的用户信息已经存在，请重新输入！");
			}

			if (Constants.OPERATION_FAILED.equals(rst.getStatus())) {
				renderJson(rst);
				return;
			}

			bwlist.set("controltype", Constants.CTLTYPE_DOMAIN);
			bwlist.set("controlvalue", controlvalue);
			bwlist.set("comment", param.getStr("comment"));

			if (bwlist.update()) {
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("编辑信息成功！");
			} else {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("编辑信息失败！");
			}
			setFlashData(rst);
			renderJson(rst);
		} else {
			getSelectinfo();
			int id = getParaToInt(0);
			Bwlist bwlist = Bwlist.dao.findById(id);
			setAttr("bwlist", bwlist);
			setAttr("currentPage",getParaToInt(1, 1));
			render("/admin/route_manage/edit_domainlist.html");
		}
	}

	/**
	 * 删除黑白名单
	 */
	public void deleteBwList() {

		int id = getParaToInt(0, 0);
		if (id != 0) {

			Bwlist bwlist = Bwlist.dao.findById(id);
			Data rst = new Data();
			try {

				if (bwlist != null) {

					bwlist.delete();
					rst.setStatus(Constants.OPERATION_SUCCEED);
					rst.setMsg("删除信息成功！");
				}

			} catch (Exception e) {
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("删除信息失败！");
			}
			setFlashData(rst);
			
			int currentPage = getParaToInt(1, 1);
			if (Constants.CTLTYPE_DOMAIN == bwlist.getInt("controltype")) {
				redirect("/myRouter/domainList/"+currentPage);
			} else {
				redirect("/myRouter/bwList/"+currentPage+"?whtype=" + bwlist.getInt("whtype"), true);
			}
		}
	}

	/**
	 * 从客流导入白名单
	 */

	/**
	 * 批量删除黑白名单
	 */
	public void multiDeleteBwList() {

		final String[] bwIds = getParaValues("bwIds");

		boolean succeed = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				int count = 0;

				for (String string : bwIds) {
					if (Bwlist.dao.deleteById(Integer.parseInt(string))) {
						count++;
					}
				}
				return count == bwIds.length;
			}
		});
		Data rst = new Data();
		if (succeed) {
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("批量删除信息成功！");
			setFlashData(rst);
		} else {
			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("批量删除信息失败！");
		}
		
		renderJson(rst);
	}

	/**
	 * 域名白名单
	 */
	public void domainList() {


		String businessName = getPara("businessName");
		String businessids = getSessionAttr("businessids");
		// 设置黑白名单数量
		setCommonBwCount(businessids);
		if (StringUtils.isNotBlank(businessName)) {
			setAttr("businessName", businessName);
		}

		int pageNumber = getParaToInt(0, 1);//	
		setAttr("bwlistPage", Bwlist.dao.pageinfoList(pageNumber, 10,
				Constants.WHTYPE_WHITE, new int[] { Constants.CTLTYPE_DOMAIN },
				businessids, businessName));

		flashRender("/admin/route_manage/domain_list.html");
	}

	/**
	 * 黑白名单数量
	 */
	private void setCommonBwCount(String businessids) {

		Record record = Bwlist.dao.getWhiteListCount(businessids);

		setAttr("whiteListCount", record.get("whiteListCount"));
		setAttr("blackListCount", record.get("blackListCount"));
		setAttr("domainListCount", record.get("domainListCount"));
	}

	/**
	 * 获取店铺名称列表
	 */
	public void getSelectinfo() {
		String businessids = getSessionAttr("businessids");
		// 获取商家名称列表
		List<Businessinfo> businessinfoList = Businessinfo.dao
				.selectBussinessinfoList(businessids);
		setAttr("businessinfoList", businessinfoList);
	}

	/**
	 * 获取品牌类型列表
	 */
	public void getAcinfoList() {

		String acbrandId = getPara("businessid");
		List<Acinfo> acinfoList = Acinfo.dao.selectAcidList(acbrandId);
		renderJson(acinfoList);
	}
	
	public String expAcdetail(String businessId, String acId, String starttime, String endtime, String userid){
		String mdPath = ""; //导出文件
		String baseDir = JFinal.me().getServletContext().getRealPath("/"); // 得到虚拟目录的绝对路径
		try {
			//先删除之前的临时excel文件
			this.delExcel();
			
			//读取模板excel
			String mbPath = baseDir + "exp/mb.xls";
			System.out.println("mbPath=="+mbPath);
			InputStream is = new FileInputStream(new File(mbPath));
			HSSFWorkbook wb = new HSSFWorkbook(is);
			//处理第一张sheet
			if(businessId.length() == 0){
				String bussinessidStr = StringUtil.getIdsplit((ArrayList<String>)getBussinessid2(userid));
				businessId=bussinessidStr;
			}
			// 获取设备运营状态详情
			List<Map<String, String>> statusList = WebServiceUtil
					.getDeviceDetailData(businessId, acId, starttime, endtime);

			List<OnlineStatusDetail> detailList = new ArrayList<OnlineStatusDetail>();
			for (Map<String, String> status : statusList) {
				OnlineStatusDetail detail = new OnlineStatusDetail();
				// ACId
				String acid = status.get("deviceid");
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT");
				sb.append(" A.eqptssid, B.busname, A.acid, TIMESTAMPDIFF(MINUTE, C.lastpingtime, now()) < 15 as online ");
				sb.append("FROM");
				sb.append(" acinfo A ");
				sb.append("LEFT JOIN businessinfo B ON A.businessid = B.id ");
				sb.append("LEFT JOIN acstatetemp C ON A.acid = C.acid ");
				sb.append("WHERE ");
				sb.append("A.acid = ?");

				Record acRecord = Db.findFirst(sb.toString(), acid);
				if (acRecord != null) {
					detail.setSsid(acRecord.getStr("eqptssid"));
					detail.setBusName(acRecord.getStr("busname"));

					if (acRecord.get("online") != null) {
						detail.setOnline(acRecord.getLong("online") == 1 ? true
								: false);
					} else {
						detail.setOnline(false);
					}
				}
				detail.setAcid(acid);
				// 累计使用时间
				if (StringUtils.isNotEmpty(status.get("accumulatedusetime"))) {
					long userTime = Long.valueOf(status.get("accumulatedusetime")) / 3600;
					detail.setAccumulatedusetime(String.valueOf(userTime));
				}
				if (StringUtils.isNotEmpty(status.get("lastdate"))) {
					detail.setLastdate(status.get("lastdate"));
				}
				if (StringUtils.isNotEmpty(status.get("incoming"))) {
					detail.setIncoming(Long.valueOf(status.get("incoming")));
				}
				if (StringUtils.isNotEmpty(status.get("outcoming"))) {
					detail.setOutcoming(Long.valueOf(status.get("outcoming")));
				}
				if (StringUtils.isNotEmpty(status.get("utilizationrate"))) {

					detail.setUtilizationrate(new Formatter().format("%.2f",
							Float.parseFloat(status.get("utilizationrate")) * 100)
							.toString());
				}

				detailList.add(detail);
			}
			
			exportAcInfo(wb,detailList);
			// 设置目标文件路径
			String uuid = UUID.randomUUID().toString();
			mdPath = baseDir + "exp/temp/" + uuid + ".xls";
			FileOutputStream os = new FileOutputStream(mdPath);
			wb.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			mdPath = "-1";
		} catch (IOException e) {
			mdPath = "-1";
		} catch (Exception e) {
			mdPath = "-1";
			e.printStackTrace();
		}
		
		return mdPath;
	}
	
	public List<String> getBussinessid(){
		List<String> idList = new ArrayList<String>();
		List<Businessinfo> businessinfoList = Businessinfo.dao.getBussinessIdList();
		for(Businessinfo obj : businessinfoList){
			idList.add(String.valueOf(obj.getInt("id")));
		}
		return idList;
	}
	
	public List<String> getBussinessid2(String userid){
		List<String> idList = new ArrayList<String>();
		List<Userbiz> businessinfoList = Userbiz.dao.selectBusinessByUid(Integer.valueOf(userid));
		for(Userbiz obj : businessinfoList){
			idList.add(String.valueOf(obj.getInt("businessid")));
		}
		return idList;
	}
	
	private void exportAcInfo(HSSFWorkbook wb, List<OnlineStatusDetail> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		//row = sheet.getRow(1);
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("路由器名称");
		cell = row.createCell(1);
		cell.setCellValue("所属店铺");
		cell = row.createCell(2);
		cell.setCellValue("路由器编码");
		cell = row.createCell(3);
		cell.setCellValue("累计使用时间(小时) ");
		cell = row.createCell(4);
		cell.setCellValue("最近开机时间");
		cell = row.createCell(5);
		cell.setCellValue("在线状态");
		cell = row.createCell(6);
		cell.setCellValue("流量(MB)");
		cell = row.createCell(7);
		cell.setCellValue("流入(MB) ");
		cell = row.createCell(8);
		cell.setCellValue("流出(MB) ");
		cell = row.createCell(9);
		cell.setCellValue("设备利用率");
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(beanList.get(i).getSsid()==null?"未知":beanList.get(i).getSsid());
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).getBusName()==null?"未知":beanList.get(i).getBusName());
			cell = row.createCell(2);
			cell.setCellValue(beanList.get(i).getAcid()==null?"未知":beanList.get(i).getAcid());
			cell = row.createCell(3);
			cell.setCellValue(beanList.get(i).getAccumulatedusetime()==null?"未知":beanList.get(i).getAccumulatedusetime());
			cell = row.createCell(4);
			cell.setCellValue(beanList.get(i).getLastdate()==null?"未知":beanList.get(i).getLastdate());
			cell = row.createCell(5);
			cell.setCellValue(isOnline(beanList.get(i).isOnline()));
			cell = row.createCell(6);
			cell.setCellValue(flow(beanList.get(i).getIncoming()+beanList.get(i).getOutcoming()));
			cell = row.createCell(7);
			cell.setCellValue(flow(beanList.get(i).getIncoming()));
			cell = row.createCell(8);
			cell.setCellValue(flow(beanList.get(i).getOutcoming()));
			cell = row.createCell(9);
			cell.setCellValue(beanList.get(i).getUtilizationrate()+"%");
		}
	}
	
	public String isOnline(boolean flage){
		String result = "在线";
		if(false==flage){
			result = "离线";
		}
		return result;
	}
	
	public String flow(long num){
		String result = "未知";
		if(num > 1024*1024){
			result = formateLong((double)num/1024*1024)+"TB";
		}else if(num > 1024){
			result = formateLong((double)num/1024)+"MB";
		}else {
			result = num+"MB";
		}
		return result;
	}
	public String formateLong(double d){
		DecimalFormat df = new DecimalFormat("#.##");//格式化符点型数据
		return df.format(d);
	}
	
	
	/**
	 * 删除导出时产生的临时excel文件
	 */
	public String delExcel() {
		try {
			String baseDir = JFinal.me().getServletContext().getRealPath("/");
			String path = baseDir + "exp/temp";
			delAllFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//删除文件夹
	//param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); //删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
		 	myFilePath.delete(); //删除空文件夹
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
		     
	//删除指定文件夹下所有文件
	//param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = true;
		File file = new File(path);
		if (!file.exists()) {
			flag = false;
			return flag;
		}
		if (!file.isDirectory()) {
			flag = false;
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);//再删除空文件夹
			}
		}
		return flag;
  }
}
