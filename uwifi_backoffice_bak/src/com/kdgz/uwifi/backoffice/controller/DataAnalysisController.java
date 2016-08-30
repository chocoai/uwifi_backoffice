package com.kdgz.uwifi.backoffice.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.kdgz.uwifi.backoffice.bean.WSResult;
import com.kdgz.uwifi.backoffice.interceptor.LayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Siteclassify;
import com.kdgz.uwifi.backoffice.util.StringUtil;
import com.kdgz.uwifi.backoffice.util.WebServiceUtil;

@Before(LayoutInterceptor.class)
public class DataAnalysisController extends Controller {

	public static SimpleDateFormat yyyyMMdd8 = new SimpleDateFormat("yyyyMMdd");
	
	private static final String DATASERVICE_URI = "http://218.23.36.211:10000/CityCloudDataService/";//218.23.36.211:10000 192.168.109.21:8080
	
//	private static final String DATASERVICE_URI = "http://192.168.109.21:8080/CityCloudDataService/";
	
	private static final String COUNT_USER = "getdevicedata_onlineusers";//客流统计数
	private static final String LINE_USER = "getuserdata_yesterday";//曲线与列表
	
	private static final String CLIENT_COUNT = "getuserdata_customeranalysis";//客户统计
	
	
	private static final String ADSHOWDATA_USER = "getaddata_detail_sum";//昨日展示数
	private static final String ADDATA_USER = "getaddata_threetimesuser";//昨日点击广告3次以上客户
	private static final String ADLINEDATA_USER = "getaddata_detail_date";//广告曲线数据
	private static final String ADTABLEDATA_USER = "getaddata_detail_business";//商家广告列表数据接口
	
	/**
	 * 客流统计
	 */
	public void index() {
		
		String flage = getPara("flage","");
		setAttr("flage",flage);
		String radioBtnQ = getPara("radioBtnQ","");
		setAttr("radioBtnQ",radioBtnQ);
		
		//String starttime = getday(new Date(),-1);
		//String endtime = getday(new Date(),0);
		
		/*********NEW**********/
		String bussinessidStr = getSessionAttr("businessids");//StringUtil.getIdsplit((ArrayList)getBussinessid());
		
		WSResult totalUser = WebServiceUtil.getDataByWS(DATASERVICE_URI+COUNT_USER,"","",bussinessidStr,"","");
		if(null != totalUser){
			if("SUCCESSED".equals(totalUser.getMessage())){
				ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>)totalUser.getData();
				if(list.size()>0){
					int yesterdayusers = 0;
					int yesterdaynewusers = 0;
					int sevenday = 0;
					int outsevenday = 0;
					for(int i=0; i<list.size(); i++){
						yesterdayusers += Integer.parseInt(list.get(i).get("yesterdayusers"));
						yesterdaynewusers += Integer.parseInt(list.get(i).get("yesterdaynewusers"));
						sevenday += Integer.parseInt(list.get(i).get("sevenday"));
						outsevenday += Integer.parseInt(list.get(i).get("outsevenday"));
					}
					//昨日到店总客户
					setAttr("yesterdayusers", yesterdayusers);
					//昨日首次到店客户
					setAttr("yesterdaynewusers", yesterdaynewusers);
					//7日内到店客户
					setAttr("sevenday", sevenday);
					//超过7日未到店客户
					setAttr("outsevenday", outsevenday);
					
				}
			}
		}
		
		String businessId = getPara("acinfo.businessid","");
		String acId = getPara("acinfo.acid","");
		setAttr("businessId",businessId);
		setAttr("acId",acId);
		String type="1";
		if(acId.length() > 0){
			type="0";
		}
		if(businessId.length() == 0){
			businessId=bussinessidStr;
		}
		
		String xCategories="[]";
		StringBuffer jsonStr = new StringBuffer("[");
		
		StringBuffer piestr = new StringBuffer("[");
		
		if(StrKit.notBlank(radioBtnQ)){
			//昨日到店总客户
			if("1".equals(radioBtnQ)){
				
				xCategories = getUserDataInfo(DATASERVICE_URI+LINE_USER+"?method=visit",businessId, type, acId, xCategories,
						jsonStr, "昨日到店总客户",piestr);
		
			}
			//昨日新客户
			if("2".equals(radioBtnQ)){
				xCategories = getUserDataInfo(DATASERVICE_URI+LINE_USER+"?method=firstvisit",businessId, type, acId, xCategories,
						jsonStr, "昨日首次到店客户",piestr);
			}
			//7日内到店客户
			if("3".equals(radioBtnQ)){
				xCategories = getUserDataInfo(DATASERVICE_URI+LINE_USER+"?method=sevendays",businessId, type, acId, xCategories,
						jsonStr, "7日内到店客户",piestr);
			}
			//超过7日未到店客户
			if("4".equals(radioBtnQ)){
				xCategories = getUserDataInfo(DATASERVICE_URI+LINE_USER+"?method=outsevendays",businessId, type, acId, xCategories,
						jsonStr, "超过7日未到店客户",piestr);
			}
		}else if ("sytz".equals(flage)){
			xCategories = getUserDataInfo(DATASERVICE_URI+LINE_USER+"?method=outsevendays",businessId, type, acId, xCategories,
					jsonStr, "超过7日未到店客户",piestr);
		}else{
			xCategories = getUserDataInfo(DATASERVICE_URI+LINE_USER+"?method=visit",businessId, type, acId, xCategories,
					jsonStr, "昨日到店总客户",piestr);
		}
		jsonStr.append("]");
		setAttr("x_array", xCategories);
		
		piestr.append("]");
		setAttr("seriesData", jsonStr.toString());
		setAttr("pieSeries", piestr.toString());
		/**********END********/
		getSelectinfo();
		render("/admin/dataanalysis/dataAnalysis.html");
	}

	private String getUserDataInfo(String URL, String businessId, String type, String acId,
			String xCategories, StringBuffer jsonStr, String name, StringBuffer piestr) {
		String[] x_array;
		WSResult lineDataShow = WebServiceUtil.getDataByWS(URL,"","",businessId,type,acId);
		if(null != lineDataShow){
			if("SUCCESSED".equals(lineDataShow.getMessage())){
				LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> linedataMap = (LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>)lineDataShow.getData();
				//曲线图数据
				ArrayList<LinkedHashMap<String, String>> generalList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("general");
				if(generalList.size() > 0){
					if(generalList.get(0).size() > 0){
						String dataStrTemp = StringUtil.MapToString(generalList.get(0));
						String[] dataArrayTemp = dataStrTemp.split(",");
						String[] seriesTemp = new String[dataArrayTemp.length];
						x_array = new String[dataArrayTemp.length];
						
						for(int i=0; i<dataArrayTemp.length; i++){
							if(!("").equals(dataArrayTemp[i])){
								String[] timeAndData = dataArrayTemp[i].split("#");
								x_array[i] = timeAndData[0];
								seriesTemp[i] = timeAndData[1];
							}
						}
						jsonStr.append("{name:'").append(name).append("',");
						jsonStr.append("data:").append(StringUtil.getStrsplit2(seriesTemp)).append("}");
						
						if(x_array.length==1){
							if("".equals(x_array[0])||null==x_array[0]){
								xCategories="[]";
							}else{
								xCategories = StringUtil.getStrsplit(x_array);
							}
						}else{
							xCategories = StringUtil.getStrsplit(x_array);
						}
					}else{
						jsonStr.append("{name:'").append(name).append("',");
						jsonStr.append("data:").append("[0]").append("}");
					}
				}
				
				//饼图
				ArrayList<LinkedHashMap<String, String>> authtypeList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("authtype");
				if(authtypeList.size() > 0){
					Map<String, String> ht = StringUtil.getHTable();//认证方式
					ArrayList<String> pieDataList = new ArrayList<String>();
					Set<String> keys = ht.keySet();  
			        for(String key: keys){  
			            for (int i=0; i<authtypeList.size(); i++) {
			            	String authId = authtypeList.get(i).get("name");
			            	if(key.equals(authId)){
			            		pieDataList.add("['"+ht.get(key)+"',"+authtypeList.get(i).get("contimescount")+"]");
			            	}
						}
			        } 
					piestr.append(StringUtil.getIdsplit(pieDataList));
				}else{
					piestr.append("['无数据显示',0]");
				}
				
				//detail
				ArrayList<LinkedHashMap<String, String>> detailList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("detail");
				int len = detailList.size();
				if(len > 0 && len <= 5){
					setAttr("detailList", detailList);
				}else if (len>5){
					ArrayList<LinkedHashMap<String, String>> tempList = new ArrayList<LinkedHashMap<String, String>>();
					for (int i=0; i<5; i++){
						tempList.add(detailList.get(i));
					}
					setAttr("detailList", tempList);
				}
			}
		}
		return xCategories;
	}
	
	public String getday(Date timeStr, int num) {  
        String strday = "";  
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");  
          
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(timeStr);
        calendar.add(Calendar.DATE, num);
        strday = sdFormat.format(calendar.getTime());  
        return strday;  
    } 
	
	private String getData(java.net.URL url) throws IOException {
		StringBuffer str = new StringBuffer();
		
		HttpURLConnection r_connection = (HttpURLConnection)url.openConnection();
		int responseCode = r_connection.getResponseCode();
		
		if(responseCode == HttpURLConnection.HTTP_OK){
			BufferedReader br = new BufferedReader(new InputStreamReader(r_connection.getInputStream(),"utf-8"));
			String strLine;
			while((strLine = br.readLine()) != null){
				str.append(strLine);
			}
			br.close();
		}else{
			str.append(responseCode);
		}
		
		return str.toString();
	}
	
	/**
	 * 客户统计
	 */
	public void userStatistics(){
		
		String radioBtnQ = getPara("radioBtnQ","");
		setAttr("radioBtnQ",radioBtnQ);
		String businessId = getPara("acinfo.businessid","");
		String acId = getPara("acinfo.acid","");
		setAttr("businessId",businessId);
		setAttr("acId",acId);
		
		String type="1";
		if(acId.length() > 0){
			type="0";
		}
		if(businessId.length() == 0){
			businessId=getSessionAttr("businessids");//"85";
		}
		
		String xCategories="[]";
		StringBuffer jsonStr = new StringBuffer("[");
		
		StringBuffer piestr = new StringBuffer("[");
		if(StrKit.notBlank(radioBtnQ)){
			if("1".equals(radioBtnQ)){
				xCategories = getClientDataInfo(DATASERVICE_URI+CLIENT_COUNT+"?method=day",businessId, type, acId, xCategories,
						jsonStr, "昨日访问人数","昨日访问次数",piestr);
			}
			if("2".equals(radioBtnQ)){
				xCategories = getClientDataInfo(DATASERVICE_URI+CLIENT_COUNT+"?method=week",businessId, type, acId, xCategories,
						jsonStr, "访问人数","访问次数",piestr);
			}
			if("3".equals(radioBtnQ)){
				xCategories = getClientDataInfo(DATASERVICE_URI+CLIENT_COUNT+"?method=month",businessId, type, acId, xCategories,
						jsonStr, "访问人数","访问次数",piestr);
			}
		}else{
			xCategories = getClientDataInfo(DATASERVICE_URI+CLIENT_COUNT+"?method=day",businessId, type, acId, xCategories,
					jsonStr, "昨日访问人数","昨日访问次数",piestr);
		}
		jsonStr.append("]");
		setAttr("x_array", xCategories);
		
		piestr.append("]");
		setAttr("seriesData", jsonStr.toString());
		
		setAttr("pieSeries", piestr.toString());
		getSelectinfo();
		render("/admin/dataanalysis/dataAnalysis_user.html");
	}
	
	private String getClientDataInfo(String URL, String businessId, String type, String acId,
			String xCategories, StringBuffer jsonStr, String name, String name2, StringBuffer piestr) {
		String[] x_array;
		String[] x_array2;
		
		String xCategories2="[]";
		StringBuffer jsonStr2 = new StringBuffer("[");
		
		WSResult lineDataShow = WebServiceUtil.getDataByWS(URL,"","",businessId,type,acId);
		if(null != lineDataShow){
			if("SUCCESSED".equals(lineDataShow.getMessage())){
				LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> linedataMap = (LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>)lineDataShow.getData();
				//System.out.println("lineDataShow="+lineDataShow.getData());
				//新房客比例
				ArrayList<LinkedHashMap<String, String>> visitorratioList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("visitorratio");
				if(visitorratioList.size() > 0){
					double visitTotal = visitorratioList.get(0).get("usercount") == null ? 0 : Double.parseDouble(visitorratioList.get(0).get("usercount"));
//					double visitTotal = visitorratioList.get(0).get("usercount") == null ? 0 : Double.parseDouble(visitorratioList.get(0).get("usercount"));
//					double visitTotal = Double.parseDouble(visitorratioList.get(0).get("usercount"));
					double visitFirst = Double.parseDouble(visitorratioList.get(0).get("firstusercount"));
					if(0 == visitTotal){
						setAttr("visitrate",0);
					}else{
						setAttr("visitrate", new Formatter().format("%.0f", Float.parseFloat( visitFirst/visitTotal+"" ) * 100));
					}
				}
				
				//曲线图数据
				ArrayList<LinkedHashMap<String, String>> uvList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("uv");

				if(uvList.size() > 0){
					if(uvList.get(0).size()>0){
						String dataStrTemp = StringUtil.MapToString(uvList.get(0));
						String[] dataArrayTemp = dataStrTemp.split(",");
						String[] seriesTemp = new String[dataArrayTemp.length];
						x_array = new String[dataArrayTemp.length];
						
						for(int i=0; i<dataArrayTemp.length; i++){
							if(!("").equals(dataArrayTemp[i])){
								String[] timeAndData = dataArrayTemp[i].split("#");
								x_array[i] = timeAndData[0];
								seriesTemp[i] = timeAndData[1];
							}
						}
						jsonStr.append("{name:'").append(name).append("',");
						jsonStr.append("data:").append(StringUtil.getStrsplit2(seriesTemp)).append("}");
						
						if(x_array.length==1){
							if("".equals(x_array[0])){
								xCategories="[]";
							}else{
								xCategories = StringUtil.getStrsplit(x_array);
							}
						}else{
							xCategories = StringUtil.getStrsplit(x_array);
						}
					}else{
						jsonStr.append("{name:'").append(name).append("',");
						jsonStr.append("data:").append("[0]").append("}");
						
					}
				}
				//次数分布
				ArrayList<LinkedHashMap<String, String>> pvList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("pv");
				
				if(pvList.size() > 0){
					if( pvList.get(0).size() > 0){
						String dataStrTemp = StringUtil.MapToString(pvList.get(0));
						String[] dataArrayTemp = dataStrTemp.split(",");
						String[] seriesTemp = new String[dataArrayTemp.length];
						x_array2 = new String[dataArrayTemp.length];
						
						for(int i=0; i<dataArrayTemp.length; i++){
							if(!("").equals(dataArrayTemp[i])){
								String[] timeAndData = dataArrayTemp[i].split("#");
								x_array2[i] = timeAndData[0];
								seriesTemp[i] = timeAndData[1];
							}
						}
						jsonStr2.append("{name:'").append(name2).append("',");
						jsonStr2.append("data:").append(StringUtil.getStrsplit2(seriesTemp)).append("}");
						
						if(x_array2.length==1){
							if("".equals(x_array2[0])){
								xCategories2="[]";
							}else{
								xCategories2 = StringUtil.getStrsplit(x_array2);
							}
						}else{
							xCategories2 = StringUtil.getStrsplit(x_array2);
						}
					}else{
						jsonStr2.append("{name:'").append(name2).append("',");
						jsonStr2.append("data:").append("[0]").append("}");
						
					}
				}
				
				//饼图
				ArrayList<LinkedHashMap<String, String>> visittimeList = (ArrayList<LinkedHashMap<String, String>>)linedataMap.get("visittime");
				if(visittimeList.size() > 0){
					
					if(visittimeList.get(0).size()>0){
						Map<String, String> ht = StringUtil.atBustimeHTable();//在店时长
						ArrayList<String> pieDataList = new ArrayList<String>();
						
						Set<String> keys = ht.keySet();  
				        for(String key: keys){ 
				            for (int i=0; i<visittimeList.size(); i++) {
				            	pieDataList.add("['"+ht.get(key)+"',"+visittimeList.get(i).get(key)+"]");
							}
				        } 
				        //System.out.println(StringUtil.getIdsplit(pieDataList));
						piestr.append(StringUtil.getIdsplit(pieDataList));
					}else{
						piestr.append("['无数据显示',0]");
					}
					
				}
			}
		}
		
		jsonStr2.append("]");
		setAttr("x_array2", xCategories2);
		setAttr("seriesData2", jsonStr2.toString());
		return xCategories;
	}
	
	public String getAdid(String businessId){
		String result = "";
		ArrayList<String> tempList = new ArrayList<String>();
		List<Siteclassify> adidList = Siteclassify.dao.getAdidList(businessId);
		if(adidList.size() > 0){
			for(Siteclassify obj : adidList){
				tempList.add(obj.getInt("id")+"");
			}
			result = StringUtil.getIdsplit(tempList);
		}
		return result;
	}
	
	/**
	 * 广告统计
	 */
	public void advertisement(){
		//获取店铺id
		String bussinessidStr = getSessionAttr("businessids");//StringUtil.getIdsplit((ArrayList)getBussinessid());
				
		String businessId = getPara("acinfo.businessid","");
		String acId = getPara("acinfo.acid","");
		String startTime="";
		String endTime="";
		setAttr("businessId",businessId);
		setAttr("acId",acId);
		
		String adidStr = getAdid(businessId);
		Map<String, String> adNameMap = StringUtil.getIdNameMap(businessId);//用于列表
		Map<String, String> adTimeMap = StringUtil.getIdTimeMap(businessId);
		Map<String, String> adFlagMap = StringUtil.getIdflageMap(businessId);
		Map<String, String> adDelTimeMap = StringUtil.getIdDelTimeMap(businessId);
		
		String type="1";
		if(acId.length() > 0){
			type="0";
		}
		if(businessId.length() == 0){
			businessId = bussinessidStr;
		}
		
		String starttime = getday(new Date(),-1);
		String endtime = getday(new Date(),0);
		String optType = getPara("optType","");
		if(optType.length() == 0){
			startTime = getday(new Date(),-7);
			endTime = endtime;
			setAttr("startTime",startTime);
			setAttr("endTime",endtime);
		}else{
			startTime = getPara("start");
			endTime = getPara("end");
			setAttr("startTime",startTime);
			setAttr("endTime",endTime);
		}
		
		getSelectinfo();
		
		//昨日展示数
		WSResult adShowData = WebServiceUtil.getDataByWS(DATASERVICE_URI+ADSHOWDATA_USER,"",endtime,bussinessidStr,"","");
		if(null != adShowData){
			if("SUCCESSED".equals(adShowData.getMessage())){
				ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>)adShowData.getData();
				if(list.size()>0){
					//点击数
					setAttr("clickcount", list.get(0).get("clickcount"));
					//展示数
					setAttr("exposurecount", list.get(0).get("exposurecount"));
					double clickcount = Double.parseDouble(list.get(0).get("clickcount"));
					double exposurecount = Double.parseDouble(list.get(0).get("exposurecount"));
					//点击率
					setAttr("clickrate", new Formatter().format("%.2f", Float.parseFloat( clickcount/exposurecount+"" ) * 100));
				}
			}
		}
		
		//历史展示数
		WSResult adHistoryData = WebServiceUtil.getDataByWS(DATASERVICE_URI+ADSHOWDATA_USER,"",endtime,bussinessidStr,"","");
		if(null != adHistoryData){
			if("SUCCESSED".equals(adHistoryData.getMessage())){
				ArrayList<LinkedHashMap<String, String>> historyList = (ArrayList<LinkedHashMap<String, String>>)adHistoryData.getData();
				if(historyList.size()>0){
					//点击数
					setAttr("historyclickcount", historyList.get(0).get("clickcount"));
					//展示数
					setAttr("historyexposurecount", historyList.get(0).get("exposurecount"));
					double historyclickcount = Double.parseDouble(historyList.get(0).get("clickcount"));
					double historyexposurecount = Double.parseDouble(historyList.get(0).get("exposurecount"));
					//点击率
					setAttr("historyclickrate", new Formatter().format("%.2f", Float.parseFloat( historyclickcount/historyexposurecount+"" ) * 100));
				}
			}
		}
		
		//昨日点击广告3次以上客户
		WSResult result = WebServiceUtil.getDataByWS(DATASERVICE_URI+ADDATA_USER,"","",bussinessidStr,"","");
		if(null != result){
			if("SUCCESSED".equals(result.getMessage())){
				ArrayList<LinkedHashMap<String, String>> threedatedataList = (ArrayList<LinkedHashMap<String, String>>)result.getData();
				if(threedatedataList.size()>0){
					//点击数
					setAttr("count", threedatedataList.get(0).get("count"));
					//历史数
					setAttr("historycount", threedatedataList.get(0).get("historycount"));
				}
			}
		}
		
		//曲线图
		WSResult lineDataShow = WebServiceUtil.getDataByWS(DATASERVICE_URI+ADLINEDATA_USER,startTime,endTime,businessId,type,acId);
		String xCategories="[]";
		String[] x_array = {};
		StringBuffer jsonStr = new StringBuffer("[");
		if(null != lineDataShow){
			if("SUCCESSED".equals(lineDataShow.getMessage())){
				ArrayList<LinkedHashMap<String, String>> linedataList = (ArrayList<LinkedHashMap<String, String>>)lineDataShow.getData();
				if(linedataList.size() > 0){
					
					String[] series1Temp = new String[linedataList.size()];
					String[] series2Temp = new String[linedataList.size()];
					
					x_array = new String[linedataList.size()];
					
					for(int i=0; i<linedataList.size(); i++){
						x_array[i]=linedataList.get(i).get("date");
						series1Temp[i]=(linedataList.get(i).get("clickcount"));
						series2Temp[i]=(linedataList.get(i).get("exposurecount"));
					}
					jsonStr.append("{name:'点击数',");
					jsonStr.append("data:").append(StringUtil.getStrsplit2(series1Temp)).append("}");
					jsonStr.append(",{name:'展示数',");
					jsonStr.append("data:").append(StringUtil.getStrsplit2(series2Temp)).append("}");
					
					xCategories = StringUtil.getStrsplit(x_array);
				}
			}else{
				jsonStr.append("{name:'点击数',");
				jsonStr.append("data:").append("[0]").append("}");
				jsonStr.append(",{name:'展示数',");
				jsonStr.append("data:").append("[0]").append("}");
				
			}
		}
		jsonStr.append("]");
		setAttr("x_array", xCategories);
		setAttr("seriesData", jsonStr.toString());
		
		//列表
		WSResult tableDataShow = WebServiceUtil.getDataByWS2(DATASERVICE_URI+ADTABLEDATA_USER,startTime,endTime,businessId,type,acId,adidStr);
		String curTime = StringUtil.getYMDHMS();
		List<java.util.Hashtable<String,String>> adList = new ArrayList<java.util.Hashtable<String,String>>();
		if(null != tableDataShow){
			if("SUCCESSED".equals(tableDataShow.getMessage())){
				ArrayList<LinkedHashMap<String, String>> tabledataList = (ArrayList<LinkedHashMap<String, String>>)tableDataShow.getData();
				
				ArrayList<LinkedHashMap<String, String>> tempList = new ArrayList<LinkedHashMap<String, String>>();
				int len = tabledataList.size();
				if(len > 0){
					LinkedHashMap<String, String> ht = null;
					for(int i=0; i<len; i++){
						ht = new LinkedHashMap<String, String>();
						//--{clickcount=1, exposurecount=1, adid=229, type=0} -- 229
						String addtime = adTimeMap.get(tabledataList.get(i).get("adid"));
						ht.put("adname", adNameMap.get(tabledataList.get(i).get("adid")));
						ht.put("adtfkssj", addtime);
						ht.put("clickcount", tabledataList.get(i).get("clickcount"));
						ht.put("exposurecount", tabledataList.get(i).get("exposurecount"));
						
						String ljsj = "0天0小时0分钟0秒";
						if("1".equals(adFlagMap.get(tabledataList.get(i).get("adid")))){//广告删除
							String deltime = adDelTimeMap.get(tabledataList.get(i).get("adid"));
							ljsj = StringUtil.dateDiff(addtime.substring(0,19), deltime.substring(0,19));
							
						}else{
							ljsj = StringUtil.dateDiff(addtime.substring(0,19), curTime);
						}
						ht.put("ljsj", ljsj);
						
						tempList.add(ht);
					}
				}
				
				setAttr("adList", tempList);
			}
		}
		render("/admin/dataanalysis/dataAnalysis_advertisement.html");
	}
	
	/**
	 * 获取商家名称列表
	 */
	public void getSelectinfo(){
		String businessids = getSessionAttr("businessids");
		List<Businessinfo> businessinfoList = Businessinfo.dao.selectBussinessinfoList(businessids);
		setAttr("businessinfoList", businessinfoList);
		
	}
	
	public List<String> getBussinessid(){
		List<String> idList = new ArrayList<String>();
		List<Businessinfo> businessinfoList = Businessinfo.dao.getBussinessIdList();
		for(Businessinfo obj : businessinfoList){
			idList.add(String.valueOf(obj.getInt("id")));
		}
		return idList;
	}
	
	/**
	 * 获取设备列表
	 */
	public void getAcbrandinfoList() {

		String businessId = getPara("businessId");
		List<Acinfo> acinfoList = Acinfo.dao.selectAcidList(businessId);
		renderJson(acinfoList);

	}
	
	public String expUserSituation(String radioFlage, String businessId, String acId, String args1, String args2){
		String mdPath = ""; //导出文件
		String baseDir = JFinal.me().getServletContext().getRealPath("/"); // 得到虚拟目录的绝对路径
		List<LinkedHashMap<String, String>> listBean = null;
		try {
			//先删除之前的临时excel文件
			this.delExcel();
			
			//读取模板excel
			String mbPath = baseDir + "exp/mb.xls";
			InputStream is = new FileInputStream(new File(mbPath));
			HSSFWorkbook wb = new HSSFWorkbook(is);
			//处理第一张sheet
			listBean = new ArrayList<LinkedHashMap<String, String>>();
			
			if(businessId.length() == 0){
				String bussinessidStr = StringUtil.getIdsplit((ArrayList<String>)getBussinessid());
				businessId=bussinessidStr;
			}
			String type="1";
			if(acId.length() > 0){
				type="0";
			}
			
			if("1".equals(radioFlage)){//昨日到店总客户
				listBean = getDetaiDataForExp(DATASERVICE_URI+LINE_USER+"?method=visit", businessId, acId, type);
			}
			if("2".equals(radioFlage)){//昨日新增到店客户
				listBean = getDetaiDataForExp(DATASERVICE_URI+LINE_USER+"?method=firstvisit", businessId, acId, type);
			}
			if("3".equals(radioFlage)){//7日内到店客户
				listBean = getDetaiDataForExp(DATASERVICE_URI+LINE_USER+"?method=sevendays", businessId, acId, type);
			}
			if("4".equals(radioFlage)){//7日外到店客户
				listBean = getDetaiDataForExp(DATASERVICE_URI+LINE_USER+"?method=outsevendays", businessId, acId, type);
			}
			
			exportUserInfo(wb,listBean);
			// 设置目标文件路径
			String uuid = UUID.randomUUID().toString();
			mdPath = baseDir + "exp/temp/" + uuid + ".xls";
			FileOutputStream os = new FileOutputStream(mdPath);
			wb.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			mdPath = "-1";
//			e.printStackTrace();
		} catch (IOException e) {
			mdPath = "-1";
//			e.printStackTrace();
		} catch (Exception e) {
			mdPath = "-1";
			e.printStackTrace();
		}
		
		return mdPath;
	}

	private List<LinkedHashMap<String, String>> getDetaiDataForExp(
			String URL, String businessId, String acId, String type) {
		List<LinkedHashMap<String, String>> listBean = new ArrayList<LinkedHashMap<String, String>>();
		WSResult userData = WebServiceUtil.getDataByWS(URL, "", "", businessId,type,acId);
		if(null != userData){
			if("SUCCESSED".equals(userData.getMessage())){
				LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> userDataMap = (LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>)userData.getData();
				ArrayList<LinkedHashMap<String, String>> detailList = (ArrayList<LinkedHashMap<String, String>>)userDataMap.get("detail");
				listBean = detailList;
			}
		}
		
		return listBean;
	}
	
	private void exportUserInfo(HSSFWorkbook wb, List<LinkedHashMap<String,String>> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		//row = sheet.getRow(1);
		row = sheet.createRow(0);
		for (int i = 0; i < 4; i++) {
			cell = row.createCell(0);
			cell.setCellValue("手机号");
			cell = row.createCell(1);
			cell.setCellValue("累计登录次数");
			cell = row.createCell(2);
			cell.setCellValue("第一次登录时间");
			cell = row.createCell(3);
			cell.setCellValue("最后一次登录时间");
		}
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(beanList.get(i).get("phone"));
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).get("count"));
			cell = row.createCell(2);
			cell.setCellValue(beanList.get(i).get("firsttime")==""?"":beanList.get(i).get("firsttime").substring(0,19));
			cell = row.createCell(3);
			cell.setCellValue(beanList.get(i).get("lasttime")==""?"":beanList.get(i).get("lasttime").substring(0,19));
		}
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
