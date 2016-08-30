package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.jfinal.core.Controller;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.model.Property;
import com.kdgz.uwifi.backoffice.model.TempletpageLog;
import com.kdgz.uwifi.backoffice.util.CsvOperateUtil;
import com.kdgz.uwifi.backoffice.util.DateUtil;

/**
 * Controller父类
 * 
 * @author lanbo
 * 
 */
public class BaseController extends Controller {

	private Property property;

	public BaseController() {
		super();

		ResourceBundle bundle = ResourceBundle.getBundle("common");
		property = new Property();
		property.setCsvLogPath(bundle.getString("csv_log_path"));
		property.setFileProxyDomain(bundle.getString("file_proxy_domain"));
	}

	/**
	 * 重写render方法
	 */
	public void render(String view) {
		
		
		

		super.render(view);
	}

	/**
	 * 设置flash数据
	 * 
	 * @param flashData
	 */
	public void setFlashData(Data flashData) {

		setSessionAttr("flash", flashData);
	}

	/**
	 * 设置flash数据
	 * 
	 * @param flashData
	 */
	public void setFlashData(String status, String msg) {
		setSessionAttr("flash", new Data(status, msg));
	}

	/**
	 * 重写render方法
	 */
	public void flashRender(String view) {

		Data flashData = (Data) getSessionAttr("flash");

		if (flashData != null) {
			if (Constants.OPERATION_SUCCEED.equals(flashData.getStatus())) {
				setAttr("infoMsg", flashData.getMsg());
			} else {
				setAttr("errorMsg", flashData.getMsg());
			}
			setSessionAttr("flash", null);
		}

		this.render(view);
	}

	/**
	 * 写入portal广告日志文件
	 * 
	 * @param log
	 */
	protected void writePortalLog(Object log) {
		String logFileName = "module_ad_log."
				+ DateUtil.toYYYMMDDStr(new Date()) + ".log";
		File file = FileUtils.getFile(property.getCsvLogPath(), logFileName);

		CsvOperateUtil.writeBeanToCsv(file, log,
				TempletpageLog.getColumnArray(), getPortalLogProcessors());
	}

	protected CellProcessor[] getPortalLogProcessors() {

		return new CellProcessor[] { new ConvertNullTo(""),
				new ConvertNullTo(""), new ConvertNullTo(""),
				new ConvertNullTo(""), new ConvertNullTo(""),
				new ConvertNullTo(""), new ConvertNullTo(""),
				new ConvertNullTo("") };

	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

}
