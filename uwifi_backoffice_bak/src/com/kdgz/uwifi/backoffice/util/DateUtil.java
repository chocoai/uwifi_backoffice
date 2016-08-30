package com.kdgz.uwifi.backoffice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * @author lanbo
 *
 */
public class DateUtil {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

	public static String toStr(Date date, String format) {

		SimpleDateFormat sf = new SimpleDateFormat(format);

		return sf.format(date);
	}

	public static String toYYYMMStr(Date date) {

		return toStr(date, DATE_FORMAT);
	}
	public static String toYYYMMDDStr(Date date) {

		return toStr(date, DATE_FORMAT_YYYY_MM_DD);
	}
	
	/**
	 * 获取 yyyy_mm_dd格式日期
	 * @param date
	 * @param num
	 * @return
	 * @author jason
	 */
	public static String getday_yymmdd(Date date, int num) {  
        String strday = "";  
        SimpleDateFormat sdFormat = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);  
          
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        strday = sdFormat.format(calendar.getTime());  
        return strday;  
    } 
	
	/**
	 * 日期转换为int型
	 * @param timeString
	 * @return
	 * @author jason
	 */
	public static int DateToInt(String timeString) {  
       int time = 0;  
       try {  
           SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);  
           Date date = sdf.parse(timeString);  
           String strTime = date.getTime() + "";  
           strTime = strTime.substring(0, 10);  
           time = Integer.parseInt(strTime);
       } catch (ParseException e) {  
           e.printStackTrace();  
       }  
       return time;  
   }
	
	/**
	 * 计算日期间隔时间
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int getDaysBetween(Calendar d1, Calendar d2) {
		if (d1.after(d2)) {
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}
}

