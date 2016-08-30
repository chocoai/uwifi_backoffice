package com.kdgz.uwifi.backoffice.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Filter {

	public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
	    try {
	    	in = new FileInputStream(file);
	        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
	        MessageDigest md5 = MessageDigest.getInstance("MD5");
	        md5.update(byteBuffer);
	        BigInteger bi = new BigInteger(1, md5.digest());
	        value = bi.toString(16);
	    } catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	if(null != in) {
	            try {
	            	in.close();
	            } catch (IOException e) {
	            	e.printStackTrace();
	            }
	        }
	    }
	    return value;
    }
	
	public static String getMD5(InputStream is) throws NoSuchAlgorithmException, IOException {
		StringBuffer md5 = new StringBuffer();
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] dataBytes = new byte[1024];
		
		int nread = 0; 
		while ((nread = is.read(dataBytes)) != -1) {
		  md.update(dataBytes, 0, nread);
		};
		byte[] mdbytes = md.digest();
		
		// convert the byte to hex format
		for (int i = 0; i < mdbytes.length; i++) {
		  md5.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return md5.toString();
	}
 
    public static void main(String[] args) {
//        String v = MD5Filter.getMd5ByFile(new File("F:/WAAS/天宇/tianyu_mt7620n_v1.0_sysupgrade.bin"));
//        System.out.println(v.toUpperCase());
        
        try{  
            downLoadFromUrl("http://61.191.204.180:8002/group1/M00/00/00/CgA7AlULdbmAXj7bAEAABCSnaSA.0_sysu",  
                    "aaabbb.bin","F:/WAAS/WAAS5.0/");  
        }catch (Exception e) {  
            e.printStackTrace();
        }  
    }
    
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
        //设置超时间为3秒  
        conn.setConnectTimeout(3*1000);  
        //防止屏蔽程序抓取而返回403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //得到输入流  
        InputStream inputStream = conn.getInputStream();    
        //获取自己数组  
        byte[] getData = readInputStream(inputStream);      
  
        //文件保存位置  
        File saveDir = new File(savePath);  
        if(!saveDir.exists()){  
            saveDir.mkdir();  
        }  
        File file = new File(saveDir+File.separator+fileName);      
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null){  
            fos.close();    
        }  
        if(inputStream!=null){  
            inputStream.close();  
        }  
        //System.out.println("info:"+url+" download success");   
  
    } 
    
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    } 
}
