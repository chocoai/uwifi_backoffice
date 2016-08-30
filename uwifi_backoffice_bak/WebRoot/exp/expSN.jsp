<%@page language="java" contentType="application/x-msdownload" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder,java.io.*,java.util.*"%>
<%@page import="com.ccdc.ticai.webservice.controller.MarketingActivity,com.ccdc.ticai.webservice.util.StringUtil"%>
<%
	//关于文件下载时采用文件流输出的方式处理：   
	response.reset();//可以加也可以不加   
	response.setContentType("application/x-download");
	String filePath = "";
	String fileName = "";
	java.io.OutputStream outp = null;
	java.io.FileInputStream in = null;
	
	String excelName = StringUtil.null2String(java.net.URLDecoder.decode(request.getParameter("excelName"),"UTF-8"));
	String lotteryid = StringUtil.null2String(request.getParameter("lotteryid"));
	
	try {
		MarketingActivity ctl = new MarketingActivity();
		filePath = ctl.expSndata(lotteryid, "");

		fileName = excelName+".xls";
	
		String filedownload = filePath;
		String filedisplay = fileName;

		if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {  
	    	filedisplay = URLEncoder.encode(filedisplay, "UTF-8"); 
	    } else {  
	    	filedisplay = new String(filedisplay.getBytes("UTF-8"), "ISO8859-1");  
	    }
		
		response.setHeader("Content-Disposition","attachment;filename=" + filedisplay);

		outp = response.getOutputStream();
		in = new FileInputStream(filedownload);
		byte[] b = new byte[1024];
		int i = 0;

		while ((i = in.read(b)) > 0) {
			outp.write(b, 0, i);
		}
		outp.flush();
		out.clear();
		out = pageContext.pushBody();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if (in != null) {
			in.close();
			in = null;
		}
	}
%>