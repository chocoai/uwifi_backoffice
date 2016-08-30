package com.kdgz.uwifi.backoffice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.log.Logger;
import com.kdgz.uwifi.backoffice.bean.WSResult;
import com.kdgz.uwifi.backoffice.constant.Constants;

/**
 * 调用数据平台接口工具
 * 
 * @author lanbo
 * 
 */
public class WebServiceUtil {

	private static final String DATASERVICE_URI = "http://218.23.36.211:10000/CityCloudDataService/";
	
//	private static final String DATASERVICE_URI = "http://192.168.109.21:8080/CityCloudDataService/";

	// 首页展示接口
	private static final String DEVICEDATA_ONLINEUSERS = "getdevicedata_onlineusers";

	// 设备运行状态概况
	private static final String DEVICE_DATA_GENERAL = "getdevicedata_general";

	// 设备运行状态详情
	private static final String DEVICE_DATA_DETAIL = "getdevicedata_device_detail";

	// 今日在线客户概要分析
	private static final String USER_DATA_TODAYGENARAL = "getuserdata_todaygenaral";

	// 今日在线客户概要分析
	private static final String USER_DATA_TODAYPHONE = "getuserdata_todayphone";
	
	
	// 今日在线客户概要分析
	private static final String DEVICE_DATA_BLACKLIST = "getdevicedata_blacklist";
	
	private static final String USER_DATA_TODAYCOUNT = "getuserdata_todayonlinecount";

	private static final Logger logger = Logger.getLogger(WebServiceUtil.class);

	/**
	 * Http Post
	 * 
	 * @param url
	 * @param nvps
	 * @return
	 */
	public static String httpPost(String url, List<NameValuePair> nvps) {

		String responseBody = "";
		// 调用数据平台接口 获取统计数据
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(url);

			httppost.setEntity(new UrlEncodedFormEntity(nvps));

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			responseBody = httpclient.execute(httppost, responseHandler);

		} catch (Exception e) {
			logger.error("调用数据平台接口失败！", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseBody;
	}

	/**
	 * 获取数据平台数据
	 * 
	 * @param url
	 * @param nvps
	 * @return
	 */
	public static List<Map<String, String>> getDataServiceData(String url,
			List<NameValuePair> nvps) {
		
		long time = System.currentTimeMillis();
		nvps.add(new BasicNameValuePair("time", String.valueOf(time)));
		
		String accesstoken = EncryptUtil.sha(time + Constants.WS_ACCESSTOKEN);
		nvps.add(new BasicNameValuePair("accesstoken", accesstoken));

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String resultStr = httpPost(url, nvps);

		ObjectMapper mapper = new ObjectMapper();
		WSResult result = new WSResult();
		try {
			result = mapper.readValue(resultStr, WSResult.class);
		} catch (Exception e) {
			logger.error("获取数据平台数据失败！", e);
		}
		if (Constants.WS_SUCCESS.equals(result.getCode())) {
			list = (ArrayList<Map<String, String>>) result.getData();
		} else if (Constants.WS_NO_DATA.equals(result.getCode())) {
			logger.error("当前调用没有数据！:" + url);

		} else if (Constants.WS_ACCESSTOKEN_ILLEGAL.equals(result.getCode())) {
			logger.error("token不正确:" + url);
		} else if (Constants.WS_UNKNOWN_ERROR == result.getCode()) {
			logger.error("未知错误:" + url);
		}
		return list;
	}

	/**
	 * 获取设备运行状态概况
	 * 
	 * @param businessid
	 * @return
	 */
	public static Map<String, String> getDeviceGeneralData(String businessid) {

		String wsUrl = DATASERVICE_URI + DEVICE_DATA_GENERAL;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("businessid", businessid));

		List<Map<String, String>> data = getDataServiceData(wsUrl, nvps);

		if (data.size() > 0) {
			return data.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取设备详情数据
	 * 
	 * @param businessid
	 * @param acId
	 * @param startdate
	 * @param endDate
	 * @return
	 */
	public static List<Map<String, String>> getDeviceDetailData(
			String businessid, String acId, String startdate, String endDate) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String wsUrl = DATASERVICE_URI + DEVICE_DATA_DETAIL;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("startdate", startdate));
		nvps.add(new BasicNameValuePair("enddate", endDate));

		nvps.add(new BasicNameValuePair("businessid", businessid));

		if (StringUtils.isEmpty(acId)) {
			nvps.add(new BasicNameValuePair("type",
					Constants.WS_DEVICE_TYPE_ALL));
		} else {
			nvps.add(new BasicNameValuePair("type",
					Constants.WS_DEVICE_TYPE_ONE));
			nvps.add(new BasicNameValuePair("deviceid", acId));
		}
		list = getDataServiceData(wsUrl, nvps);

		return list;
	}

	/**
	 * 获取首页展示数据
	 * 
	 * @param businessid
	 * @param acId
	 * @param startdate
	 * @param endDate
	 * @return
	 */
	public static List<Map<String, String>> getDashboardData(String businessid) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String wsUrl = DATASERVICE_URI + DEVICEDATA_ONLINEUSERS;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair("businessid", businessid));

		list = getDataServiceData(wsUrl, nvps);

		return list;
	}

	/**
	 * 今日在线客户概要分析
	 */
	public static Map<String, String> getUserdataTodayGenaral(
			String businessid, String acId) {

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		String wsUrl = DATASERVICE_URI + USER_DATA_TODAYGENARAL;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair("businessid", businessid));

		if (StringUtils.isEmpty(acId)) {
			nvps.add(new BasicNameValuePair("type",
					Constants.WS_DEVICE_TYPE_ALL));
		} else {
			nvps.add(new BasicNameValuePair("type",
					Constants.WS_DEVICE_TYPE_ONE));
			nvps.add(new BasicNameValuePair("deviceid", acId));
		}
		data = getDataServiceData(wsUrl, nvps);
		
		if (data.size() > 0) {
			return data.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 今日手机客户详细分析
	 */
	public static List<Map<String, String>> getUserdataTodayphone(
			String businessid, String acId) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String wsUrl = DATASERVICE_URI + USER_DATA_TODAYPHONE;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair("businessid", businessid));

		if (StringUtils.isEmpty(acId)) {
			nvps.add(new BasicNameValuePair("type",
					Constants.WS_DEVICE_TYPE_ALL));
		} else {
			nvps.add(new BasicNameValuePair("type",
					Constants.WS_DEVICE_TYPE_ONE));
			nvps.add(new BasicNameValuePair("deviceid", acId));
		}

		list = getDataServiceData(wsUrl, nvps);
		return list;
	}
	
	/**
	 * 获取WS数据
	 * @param wsUrl
	 * @param businessid
	 * @return
	 */
	public static WSResult getDataByWS(String wsUrl, String startdate, String enddate, String businessid,String type, String adid) {
		WSResult result = new WSResult();
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("startdate", startdate));
		nvps.add(new BasicNameValuePair("enddate", enddate));
		nvps.add(new BasicNameValuePair("businessid", businessid));
		
		// Accesstoken加密
		long time = System.currentTimeMillis();
		nvps.add(new BasicNameValuePair("time", String.valueOf(time)));
		String accesstoken = EncryptUtil.sha(time + Constants.WS_ACCESSTOKEN);
		nvps.add(new BasicNameValuePair("accesstoken", accesstoken));
		nvps.add(new BasicNameValuePair("type", type));
		nvps.add(new BasicNameValuePair("deviceid", adid));
		
		String resultStr = httpPost(wsUrl, nvps);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			result = mapper.readValue(resultStr, WSResult.class);
		} catch (Exception e) {
			logger.error("获取数据失败！"+wsUrl, e);
			return null;
		}
		return result;
	}
	
	public static WSResult getDataByWS2(String wsUrl, String startdate, String enddate, String businessid,String type, String deviceid, String adid) {
		WSResult result = new WSResult();
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("startdate", startdate));
		nvps.add(new BasicNameValuePair("enddate", enddate));
		nvps.add(new BasicNameValuePair("businessid", businessid));
		
		// Accesstoken加密
		long time = System.currentTimeMillis();
		nvps.add(new BasicNameValuePair("time", String.valueOf(time)));
		String accesstoken = EncryptUtil.sha(time + Constants.WS_ACCESSTOKEN);
		nvps.add(new BasicNameValuePair("accesstoken", accesstoken));
		nvps.add(new BasicNameValuePair("type", type));
		nvps.add(new BasicNameValuePair("deviceid", deviceid));
		nvps.add(new BasicNameValuePair("adid", adid));
		
		String resultStr = httpPost(wsUrl, nvps);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			result = mapper.readValue(resultStr, WSResult.class);
		} catch (Exception e) {
			logger.error("获取数据失败！"+wsUrl, e);
			return null;
		}
		return result;
	}
	
	/**
	 * 黑白名单数据
	 */
	public static List<Map<String, String>> getBwUserList(
			String businessid, int arrivaltimes, int averageOnlineTime, String startdate, String endDate, int limit) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String wsUrl = DATASERVICE_URI + DEVICE_DATA_BLACKLIST;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair("businessid", businessid));
		nvps.add(new BasicNameValuePair("startdate", startdate));
		nvps.add(new BasicNameValuePair("enddate", endDate));
		// 到店次数
		nvps.add(new BasicNameValuePair("visitornum", String.valueOf(arrivaltimes)));
		// 平均上网时长
		nvps.add(new BasicNameValuePair("onlinetime", String.valueOf(averageOnlineTime)));
		// 数据限制
		nvps.add(new BasicNameValuePair("limit", String.valueOf(limit)));
		
		list = getDataServiceData(wsUrl, nvps);
		return list;
	}
	
	public static List<Map<String, String>> getOnlineUserCount(String businessid) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String wsUrl = DATASERVICE_URI + USER_DATA_TODAYCOUNT;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair("businessid", businessid));
		nvps.add(new BasicNameValuePair("range", "10"));

		list = getDataServiceData(wsUrl, nvps);

		return list;
	}

}
