package com.kdgz.uwifi.backoffice.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.model.Siteclassify;
import com.kdgz.uwifi.backoffice.model.Syscode;

public class StringUtil {

	/**
     * 将List转换为1，2的格式后返回，来方便数据库的操作
     *
     * @param ids
     * @return String
     */
    public static String getIdsplit(ArrayList ids) {
        if (ids == null || ids.size() == 0) return "";
        String result = "";
        for (int i = 0; i < ids.size(); i++) {
            if (i == ids.size() - 1) result = result + (String) ids.get(i);
            else
                result = result + (String) ids.get(i) + ",";
        }
        result = result + "";
        return result;
    }
    
    /**
     * 将字符串数组转换为['a','b']的格式后返回
     *
     * @param names
     * @return String
     */
    public static String getStrsplit(String[] names) {
        if (names == null || names.length == 0) return "[]";
        String result = "[";
        for (int i = 0; i < names.length; i++) {
            if (i == names.length - 1) result = result + "'" + names[i] + "'";
            else
                result = result + "'" + names[i] + "',";
        }
        result = result + "]";
        return result;
    }
    
    /**
     * 将字符串数组转换为[a,b]的格式后返回
     *
     * @param names
     * @return String
     */
    public static String getStrsplit2(String[] names) {
        if (names == null || names.length == 0) return "[]";
        String result = "[";
        for (int i = 0; i < names.length; i++) {
            if (i == names.length - 1) result = result + names[i] ;
            else
                result = result + names[i] + ",";
        }
        result = result + "]";
        return result;
    }
    
    /**
     * 获取时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getYMDHMS(){
    	long time = System.currentTimeMillis();
    	String ymdhms = DateFormatUtils.format(time, Constants.DATETIME_FORMAT);
    	return ymdhms;
    }
    
    /**
     * 将MAP转换成String
     * @param map
     * @return
     */
    public static String MapToString(Map<?, ?> map) { 
    	String SEP1 = "#";  
    	String SEP2 = ",";  

        StringBuffer sb = new StringBuffer();  
        // 遍历map  
        for (Object obj : map.keySet()) {  
            if (obj == null) {  
                continue;  
            }  
            Object key = obj;  
            Object value = map.get(key);  
            sb.append(key.toString() + SEP1 + value.toString());  
            sb.append(SEP2);  
        }  
        return sb.toString();  
    } 
    
    /**
     * 将对象转换了字符型
     *
     * @param s
     */
    public static String null2String(Object s) {
        return s == null || s.equals("null") || s.equals("NULL") ? "" : s.toString();
    }
    
    /**
     * 获取Hashtable
     */
    public static Map<String, String> getHTable(){
    	Map<String, String> htable = new Hashtable<String, String>();
    	List<Syscode> list = Syscode.dao.syscodeList();
    	if(list.size() > 0){
	    	for (Syscode obj : list) {
	    		htable.put(String.valueOf(obj.getInt("codevalue")), obj.getStr("codename"));
	    	}
    	}
    			
    	return htable;
    }
    
    public static Map<String, String> atBustimeHTable(){
    	Map<String, String> htable = new LinkedHashMap<String, String>();
    	htable.put("zero", "0-10分钟");
    	htable.put("ten", "10-30分钟");
    	htable.put("thirty", "30-60分钟");
	    htable.put("sixty", "60分钟以上");
	   
    	return htable;
    }
    
    /**
     * 获取广告id和name的键值对
     * @return
     */
    public static Map<String, String> getIdNameMap(String businessId){
    	Map<String, String> htable = new Hashtable<String, String>();
    	List<Siteclassify> list = Siteclassify.dao.getAdidList(businessId);
    	if(list.size() > 0){
	    	for (Siteclassify obj : list) {
	    		htable.put(String.valueOf(obj.getInt("id")), null2String(obj.getStr("name")));
	    	}
    	}
    			
    	return htable;
    }
    
    public static Map<String, String> getIdTimeMap(String businessId){
    	Map<String, String> htable = new Hashtable<String, String>();
    	List<Siteclassify> list = Siteclassify.dao.getAdidList(businessId);
    	if(list.size() > 0){
	    	for (Siteclassify obj : list) {
	    		htable.put(String.valueOf(obj.getInt("id")), obj.getTimestamp("addtime1")+"");
	    	}
    	}
    			
    	return htable;
    }
    
    public static Map<String, String> getIdDelTimeMap(String businessId){
    	Map<String, String> htable = new Hashtable<String, String>();
    	List<Siteclassify> list = Siteclassify.dao.getAdidList(businessId);
    	if(list.size() > 0){
	    	for (Siteclassify obj : list) {
	    		htable.put(String.valueOf(obj.getInt("id")), obj.getTimestamp("updatetime1")+"");
	    	}
    	}
    			
    	return htable;
    }
    
    public static Map<String, String> getIdflageMap(String businessId){
    	Map<String, String> htable = new Hashtable<String, String>();
    	List<Siteclassify> list = Siteclassify.dao.getAdidList(businessId);
    	if(list.size() > 0){
	    	for (Siteclassify obj : list) {
	    		htable.put(String.valueOf(obj.getInt("id")), obj.getInt("delflag")+"");
	    	}
    	}
    			
    	return htable;
    }
    
    public static String timestamp2String(Timestamp time){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
    	return df.format(time);
    }
    
    public static String dateDiff(String startTime, String endTime) {
    	String result = "";
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数
		long diff = 0;//获得两个时间的毫秒时间差异
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			long day = diff/nd;//计算差多少天
			long hour = diff%nd/nh;//计算差多少小时
			long min = diff%nd%nh/nm;//计算差多少分钟
			long sec = diff%nd%nh%nm/ns;//计算差多少秒
			
			result = day+"天"+hour+"小时"+min+"分钟"+sec+"秒";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
    
    public static void generateQRCode(String url, ServletOutputStream stream){
		int width = 200; 
		int height = 200; 
		//二维码的图片格式 
		String format = "gif"; 
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(); 
		//内容所使用编码 
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(url, 
					 BarcodeFormat.QR_CODE, width, height, hints);
			//生成二维码 
			MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
		} catch (WriterException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }

    public static String getDateStr8() {
    	String strDate="";
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
    	strDate = simpledateformat.format(calendar.getTime());
    	return strDate;
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
  				delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
  				delFolder(path + File.separator + tempList[i]);//再删除空文件夹
  			}
  		}
  		return flag;
  	}
  	
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
    
}
