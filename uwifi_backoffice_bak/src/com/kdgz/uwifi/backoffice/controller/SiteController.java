package com.kdgz.uwifi.backoffice.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.model.Roleinfo;
import com.kdgz.uwifi.backoffice.util.CommonUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;
import com.kdgz.uwifi.backoffice.util.EncryptUtil;

/**
 * 站点控制器
 * 
 * @author lanbo
 * 
 */
public class SiteController extends Controller {

	public void index() {
		renderFreeMarker("/admin/site/login_new.html");
	}

	/**
	 * 登录
	 */
	public void login() {

		String username = getPara("username");
		String password = getPara("password");
		// String rememberMe = getPara("rememberMe");

		if (StrKit.isBlank(username) || StrKit.isBlank(password)) {
			
			if(StringUtils.isNotEmpty(username)) {
				setAttr("username", username);
			}
			if(StringUtils.isNotEmpty(password)) {
				setAttr("password", password);
			}
			
			setAttr("attentionMsg", "请输入用户名及密码 ");
			render("/admin/site/login_new.html");
		} else {
			Record	user = Db
						.findFirst(
								"select * from userinfo where delflag = 0 and loginname = ? and checkstatus = 1",
								new Object[] { username });
			 
			if (user == null) {
				setAttr("username", username);
				setAttr("password", password);
				setAttr("errorMsg", "用户账户不存在，请联系管理员！");
				render("/admin/site/login_new.html");
			}else if(user.getInt("status") == 0){
				setAttr("username", username);
				setAttr("password", password);
				setAttr("errorMsg", "用户账户已被禁用，请联系管理员！");
				render("/admin/site/login_new.html");
				
			}else {
				
				String encryptPass = EncryptUtil.md5(EncryptUtil.md5(password)
						.substring(8) + user.getStr("salt"));
				
				if (user.getStr("password").equals(encryptPass)) {

					setSessionAttr("loginName", user.getStr("loginname"));
					setSessionAttr("roleCode", user.getBoolean("rolecode"));
					setSessionAttr("isLogin", true);
					setSessionAttr("userid", user.getInt("id"));
					setSessionAttr("province", user.getStr("province"));
					setSessionAttr("city", user.getStr("city"));
					setSessionAttr("channelName", user.getStr("channelname"));
					Roleinfo roleinfo = Roleinfo.dao.selectRoleinfoByUserId(user.getInt("id"));
					String roleid = roleinfo.getInt("id").toString();
					if((Constants.ROLE_ADMIN.equals(roleid)) || Constants.ROLE_GOLDCHANNEL.equals(roleid) || Constants.ROLE_SILVERCHANNEL.equals(roleid)){
						setSessionAttr("roleid", roleid);
						redirect("/dashboardchannel");
						return;
					}
					redirect("/dashboard");
				} else {
					setAttr("username", username);
					setAttr("password", password);
					setAttr("errorMsg", "用户密码错误，请重新输入！");
					render("/admin/site/login_new.html");
				}
			}
		}
	}

	/**
	 * 登出
	 */
	public void logout() {

		removeSessionAttr("username");
		removeSessionAttr("name");
		removeSessionAttr("roleCode");
		removeSessionAttr("userid");
		removeSessionAttr("businessids");
		removeSessionAttr("isLogin");
		removeSessionAttr("pageJump");

		renderFreeMarker("/admin/site/login_new.html");
	}

	/**
	 * 上传图片
	 * 
	 * 100 模块图片 200 模块列表图片 300 模块列表详细图片
	 */
	public void uploadPicture() {
		
		// 图片类型
		String type = getPara("type");

		// 图片扩展名
		String randomName = CommonUtil.getRandomString(8);
		String fileExt = FilenameUtils.getExtension(getPara("qqfile"));

		String fileName = randomName + "." + fileExt;

		Date now = new Date();
		// 获取上传图片路径
		String targetPath = getPictureSavePath(type, fileName, now);

		String savePath = FilenameUtils.separatorsToUnix(targetPath);

		// 目标保存文件
		File targetFile = new File(getSession().getServletContext()
				.getRealPath("/") + targetPath);
		
		try {
			FileUtils.copyInputStreamToFile(getRequest().getInputStream(),
					targetFile);
		} catch (IOException e) {
			renderJson("error", "图片上传失败，请稍后重试");
		}

		renderJson("path", savePath);
	}
	public void loadFileImage() {
		
		try {
			
		String fileSize = getPara("fileSize");
		String qqFile = getPara("qqfile");
		
		InputStream is = getRequest().getInputStream();
		
		URL url = new URL("http://218.23.36.213:8080/asyncUpload?qqfile="+qqFile+"&fileSize=" +fileSize);  
		
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        // 设置通用的请求属性
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.setRequestProperty("connection", "Keep-Alive");
        urlConnection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        
        OutputStream os = urlConnection.getOutputStream();
        
        IOUtils.copyLarge(is, os);
        
        urlConnection.connect();
        
        BufferedReader in = null;
        String result = "";
        in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        System.out.println(result);
        renderJson("fileId", result);
        
		} catch (Exception e) {
			System.out.println(e);
		}
	}


	/**
	 * 删除图片
	 * 
	 * @param type
	 * @return
	 */
	public void deletePicture() {

		// 图片路径
		String path = getPara("path");

		// 目标保存文件
		File targetFile = new File(getSession().getServletContext()
				.getRealPath("/") + path);

		FileUtils.deleteQuietly(targetFile);

		renderText("0");

	}

	/**
	 * 生成图片存储相对路径
	 * 
	 * @param type
	 * @param fileName
	 * @param now
	 * @return
	 */
	private String getPictureSavePath(String type, String fileName, Date now) {

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.UPLOADED_DIRECTORY).append(File.separator)
				.append(Constants.IMAGE_DIRECTORY.get(type))
				.append(File.separator).append(DateUtil.toYYYMMStr(now))
				.append(File.separator)
				.append(DateUtil.toStr(now, Constants.DATETIME_FORMAT_SHORT))
				.append(File.separator).append(fileName);

		return sb.toString();
	}
	

}
