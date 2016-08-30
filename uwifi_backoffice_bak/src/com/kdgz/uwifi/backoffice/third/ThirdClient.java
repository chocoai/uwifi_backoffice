package com.kdgz.uwifi.backoffice.third;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import com.gz.wd.web.WebRPCClient;

public class ThirdClient {
	private static String rpcAddress ;
	private static int rpcPort;
	private static int rpcTimeout;
	private static String callThirdUrl;
	
	static{
		Properties prop = new Properties();
		try{
			//InputStream in = new BufferedInputStream(new FileInputStream("config.properties"));
			InputStream in = ThirdClient.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(in);
			rpcAddress = prop.getProperty("rpc.address");
			try{
				rpcPort = Integer.parseInt(prop.getProperty("rpc.port"));
			}catch (Exception e) {
				e.printStackTrace();
				rpcPort = 80;
			}
			try{
				rpcTimeout = Integer.parseInt(prop.getProperty("rpc.timeout"));
			}catch(Exception e){
				e.printStackTrace();
				rpcTimeout = 300;
			}
			callThirdUrl = prop.getProperty("callThird.url");
			
			in.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static WebRPCClient getWebClient(InetAddress host, int port, int timeout){
		WebRPCClient webclient = null;		
//		try{
//			webclient = new WebRPCClient(host, port, OncRpcProtocols.ONCRPC_TCP);		
//			webclient.getClient().setTimeout(timeout*1000);
//		}catch (OncRpcException e){
//			e.printStackTrace();
//		}catch (IOException e){
//			e.printStackTrace();
//		}
		return webclient;
	}
	public static WebRPCClient getWebClient(InetAddress host, int port){
		return getWebClient(host, port, rpcTimeout);
	}
	
	public static WebRPCClient getWebClient(){
		return getWebClient(getHost(rpcAddress), rpcPort);
	}
	
	public static WebRPCClient getWebClient(String address, int port){
		return getWebClient(getHost(address), port);
	}
	
	public static WebRPCClient getWebClient(String address, String sPort){
		int port = Integer.parseInt(sPort);
		return getWebClient(getHost(address), port);
	}
	
	private static InetAddress getHost(String address){
		InetAddress host = null;
		try{
			host = InetAddress.getByName(address);
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return host;
	}
	
	/**
	 * 
	 * @param uri
	 * 		请求的服务名及参数
	 * @return Map 包括
	 * 		http请求的返回码 resCode
	 * 		http请求的返回内容 resStr
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Map callThird(String uri) throws MalformedURLException,IOException{
		Map map = new HashMap();
		StringBuffer outStr = new StringBuffer("");
		String url = callThirdUrl + uri;
		URL r_url = new URL(url.replaceAll(" ", "%20"));
		HttpURLConnection r_connection = (HttpURLConnection)r_url.openConnection();
		int responseCode = r_connection.getResponseCode();
		map.put("resCode", responseCode);
		if(responseCode == HttpURLConnection.HTTP_OK){
			BufferedReader br = new BufferedReader(new InputStreamReader(r_connection.getInputStream(),"utf-8"));
			String strLine;
			while((strLine = br.readLine()) != null){
				outStr.append(strLine);
			}
			br.close();
		}
		map.put("resStr", outStr.toString());
		return map;
	}
	
	/**
	 * 传入response 直接将请求返回的内容写入response
	 * @param response
	 * 		传入response
	 * @param uri
	 * 		请求的服务名及参数
	 */
	public static void callThird(HttpServletResponse response, String uri) {
		StringBuffer outStr = new StringBuffer("");
		String url = callThirdUrl + uri;
		try{
			URL r_url = new URL(url.replaceAll(" ", "%20"));
			//System.out.println(new Date());
			//System.out.println(url);
			HttpURLConnection r_connection = (HttpURLConnection)r_url.openConnection();
			int responseCode = r_connection.getResponseCode();
			//System.out.println(new Date());
			//map.put("resCode", responseCode);
			if(responseCode == HttpURLConnection.HTTP_OK){
				BufferedReader br = new BufferedReader(new InputStreamReader(r_connection.getInputStream(),"utf-8"));
				String strLine;
				while((strLine = br.readLine()) != null){
					outStr.append(strLine);
				}
				br.close();
			}else{
				outStr.append(responseCode);
			}
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain; charset=UTF-8");  		  
	        response.setHeader("Content-Disposition", "inline");  
			PrintWriter pw = null;
			pw = response.getWriter();
			pw.write(outStr.toString());
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
