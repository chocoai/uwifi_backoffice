package com.kdgz.uwifi.backoffice.util;

import java.io.File;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

/**
 * 共通工具类
 * 
 * @author lanbo
 * 
 */
public class CommonUtil {

	/**
	 * 生成加密密码串
	 * 
	 * @param pass
	 * @param salt
	 * @return
	 */
	public static String generateSaltPassword(String pass, String salt) {

		String returnStr = EncryptUtil.md5(EncryptUtil.md5(pass).substring(8)
				+ salt);

		return returnStr;
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 删除上传的图片文件
	 * @param imagePath
	 */
	public static void deleteUplaodedImages(String imagePath) {
		// 目标保存文件
		File targetFile = new File(imagePath);

		FileUtils.deleteQuietly(targetFile);
	}
	
	/**
	 * 实时在线WiFi用户
	 * 
	 * @param businessid
	 * @return
	 */
	@Deprecated
	public static long countPresentWiFiUser(String businessid) {
		if(StrKit.isBlank(businessid)){
			return 0;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) from clientauthinfo where stage = 'counters' and TIMESTAMPDIFF(MINUTE, authtime, now()) < 5");
		if (StrKit.notBlank(businessid)) {
			sql.append(" and acid in (select acid from acinfo where acinfo.businessid in (" + businessid + "))");
		}

		return Db.queryLong(sql.toString());
	}
}
