package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import org.apache.commons.io.FilenameUtils;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ChannelInterceptor;
import com.kdgz.uwifi.backoffice.model.Actypeinfo;
import com.kdgz.uwifi.backoffice.model.Firmware;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.MD5Filter;
import com.kdgz.uwifi.backoffice.util.StringUtil;

@Before(ChannelInterceptor.class)
public class FirmwareController extends BaseController {
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index(){
		
		setAttr("FirmwareList", Firmware.dao.getFirmwareList());
		flashRender("/admin/firmware/firmware_list.html");
	}
	
	public void addFirmware(){
		
		if (getRequest().getMethod().equals("POST")) {
			Firmware param = getModel(Firmware.class);
			
			String firmwareName = param.getStr("name");
			String firmwareUrl = param.getStr("url");
			
			String randomName = CommonUtil.getRandomString(8);
    		String fileExt = FilenameUtils.getExtension(firmwareName);
    		
    		String fileName = randomName+"."+fileExt;
    		String targetPath = getGjSavePath();

			String baseDir = JFinal.me().getServletContext().getRealPath("/");
			String gjPath = baseDir + targetPath;
			try {
				MD5Filter.downLoadFromUrl(bundle.getString("file_proxy_domain")+firmwareUrl,  
						fileName, gjPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			File targetFile = new File(gjPath+File.separator+fileName);
			
			String md5String = MD5Filter.getMd5ByFile(targetFile);
			
			Firmware firmware = new Firmware();
			Data retMsg = new Data();
			
			String encrypt = md5String;
			
			firmware.set("encrypt", encrypt.toUpperCase());
			firmware.set("type", param.getStr("type"));
			firmware.set("url", firmwareUrl);
			firmware.set("version", param.getStr("version"));
			firmware.set("name", firmwareName);
			firmware.set("status", 0);
		    
			if (firmware.save()) {
				StringUtil.delAllFile(gjPath);
				retMsg.setStatus(Constants.OPERATION_SUCCEED);
				
//				int firmwareid = firmware.getInt("id");
//				Actypeinfo actypeinfo = new Actypeinfo();
//				actypeinfo.set("acbrandid", "1000");
//				actypeinfo.set("typename", param.getStr("type"));
//				actypeinfo.set("firmwareid", firmwareid);
//				actypeinfo.set("delflag", 0);
//				actypeinfo.save();
//				
				
				retMsg.setMsg("添加成功！");
				setFlashData(retMsg);
			}
			renderJson(retMsg);
			
		}else {
			setAttr("uploadPath",bundle.getString("uploadPath"));
			render("/admin/firmware/add_firmware.html");
		}
	}
	
	
	public void editFirmware(){
		
		int id = getParaToInt(0);
		
		Firmware firmware = Firmware.dao.findById(id);
		
		if (getRequest().getMethod().equals("POST")) {
			Firmware param = getModel(Firmware.class);
			
			String firmwareName = param.getStr("name");
			String firmwareUrl = param.getStr("url");
			
			String gjPath = null;
			if(firmwareUrl != null){
				String randomName = CommonUtil.getRandomString(8);
	    		String fileExt = FilenameUtils.getExtension(firmwareName);
	    		
	    		String fileName = randomName+"."+fileExt;
	    		String targetPath = getGjSavePath();

				String baseDir = JFinal.me().getServletContext().getRealPath("/");
			    gjPath = baseDir + targetPath;
				try {
					MD5Filter.downLoadFromUrl(bundle.getString("file_proxy_domain")+firmwareUrl,  
							fileName, gjPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				File targetFile = new File(gjPath+File.separator+fileName);
				
				String md5String = MD5Filter.getMd5ByFile(targetFile);
				String encrypt = md5String;
				
				firmware.set("encrypt", encrypt.toUpperCase());
			}
			
		
			
			Data retMsg = new Data();
			
			
			
			
			firmware.set("type", param.getStr("type"));
			firmware.set("url", firmwareUrl);
			firmware.set("version", param.getStr("version"));
			firmware.set("name", firmwareName);
			firmware.set("status", 0);
		    
			if (firmware.update()) {
				if(firmwareUrl != null){
					StringUtil.delAllFile(gjPath);
				}
//				Actypeinfo actypeinfo = Actypeinfo.dao.selectActypeinfoByFirmwareId(id);
//				actypeinfo.set("typename", param.getStr("type"));
//				actypeinfo.update();
				retMsg.setStatus(Constants.OPERATION_SUCCEED);
				retMsg.setMsg("修改成功！");
				setFlashData(retMsg);
			}
			renderJson(retMsg);
			
		}else {
			setAttr("firmware", firmware);
			setAttr("uploadPath",bundle.getString("uploadPath"));
			render("/admin/firmware/edit_firmware.html");
		}
	}
	
	
	private String getGjSavePath() {

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.UPLOADED_DIRECTORY).append(File.separator)
				.append("firmware").append(File.separator);

		return sb.toString();
	}
	
	public void changeStatus(){
		String id = getPara(0);
		int result = Db.update("update firmware set status = 0 ");
		Data rst = new Data();
		if (-1 != result) {
			Db.update("update firmware set status = 1 where id = ?", id);
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("选择版本成功！");
			setFlashData(rst);
		} else {
			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("选择版本失败！");
		}
		
		renderJson(rst);
		redirect("/firmWare/");
	}
	
	public void checkTypename(){
		
		String typename = getPara("firmware.type");
		
		long count = Firmware.dao.getFirmwareByType(typename);
		if (count > 0) {
			renderJson("false");
			return;
		}
		renderJson("true");
	}
	public void editcheckTypename(){
		
		int id = getParaToInt(0);
		String typename = getPara("firmware.type");
		
		long count =  Firmware.dao.getFirmwareByType(typename, id);
		if (count > 0) {
			renderJson("false");
			return;
		}
		renderJson("true");
	}
	
}
