<%@page language="java" contentType="application/x-msdownload" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder,java.io.*,java.util.*"%>
<%@page import="com.ccdc.ticai.webservice.controller.DataAnalysisController,com.ccdc.ticai.webservice.util.StringUtil"%>
<%
	//关于文件下载时采用文件流输出的方式处理：   
	//加上response.reset()，并且所有的％>后面不要换行，包括最后一个；   
	response.reset();//可以加也可以不加   
	response.setContentType("application/x-download");
	String filePath = "";
	String fileName = "";
	java.io.OutputStream outp = null;
	java.io.FileInputStream in = null;
	
	//扩展用于添加条件
	//String args1 = request.getParameter("args1");
	//String args2 = request.getParameter("args2");
	String radioFlage = StringUtil.null2String(request.getParameter("radioFlage"));
	String businessid = StringUtil.null2String(request.getParameter("businessId"));
	String acid = StringUtil.null2String(request.getParameter("acId"));
	//System.out.println("radioFlage="+radioFlage+"businessid="+businessid+"acid="+acid);
	
	try {
		DataAnalysisController ctl = new DataAnalysisController();
		filePath = ctl.expUserSituation(radioFlage, businessid,acid,"", "");

		if("1".equals(radioFlage)){
			fileName = "昨日到店总客户-"+StringUtil.getDateStr8()+".xls";
		}else if ("2".equals(radioFlage)){
			fileName = "昨日首次到店客户-"+StringUtil.getDateStr8()+".xls";
		}else if ("3".equals(radioFlage)){
			fileName = "7日内到店客户-"+StringUtil.getDateStr8()+".xls";
		}else if ("4".equals(radioFlage)){
			fileName = "超过7日未到店客户-"+StringUtil.getDateStr8()+".xls";
		}
	
		String filedownload = filePath;
		String filedisplay = fileName;

		//filedisplay = URLEncoder.encode(filedisplay, "UTF-8");
		
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
		//     
		outp.flush();
		//要加以下两句话，否则会报错   
		//java.lang.IllegalStateException: getOutputStream() has already been called for //this response     
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