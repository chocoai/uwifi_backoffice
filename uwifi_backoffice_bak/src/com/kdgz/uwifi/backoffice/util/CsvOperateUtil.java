package com.kdgz.uwifi.backoffice.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.mchange.v2.io.FileUtils;

public final class CsvOperateUtil {

	/**
	 * 读取csv文件(不带头部)
	 * 
	 * @param file
	 *            File
	 * @return csv文件组装成list
	 * @throws IOException
	 */
	public static List<String[]> getContentFromFile(File file)
			throws IOException {
		List<String[]> content = new ArrayList<String[]>();

		CsvListReader reader = null;
		try {
			reader = new CsvListReader(new FileReader(file),
					CsvPreference.EXCEL_PREFERENCE);
			// 去除头部字段声明
			reader.getHeader(true);
			List<String> line = new ArrayList<String>();
			while ((line = reader.read()) != null) {
				content.add(line.toArray(new String[] {}));
			}
		} catch (Exception e) {
			reader.close();
		}
		return content;
	}

	/**
	 * 读取csv文件(带头部)
	 * 
	 * @param file
	 *            File
	 * @return csv文件组装成list
	 * @throws IOException
	 */
	public static List<String[]> getDetailFromFile(File file)
			throws IOException {
		List<String[]> content = new ArrayList<String[]>();
		CsvListReader reader = null;
		try {
			reader = new CsvListReader(new FileReader(file),
					CsvPreference.EXCEL_PREFERENCE);
			String[] header = reader.getHeader(true);
			content.add(header);
			List<String> line = new ArrayList<String>();
			while ((line = reader.read()) != null) {
				content.add(line.toArray(new String[] {}));
			}
		} catch (Exception e) {
			reader.close();
		}
		return content;
	}

	/**
	 * 读取csv文件的头部
	 * 
	 * @param file
	 *            File
	 * @return csv文件的头部
	 * @throws IOException
	 */
	public static String[] getHeaderFromFile(File file) throws IOException {
		CsvListReader reader = null;
		try {
			reader = new CsvListReader(new FileReader(file),
					CsvPreference.EXCEL_PREFERENCE);
		} catch (Exception e) {
			reader.close();
		}

		return reader.getHeader(true);
	}

	/**
	 * 写入csv文件
	 * 
	 * @param file
	 *            File
	 * @param header
	 *            头部
	 * @param content
	 *            内容
	 * @throws IOException
	 */
	public static void writeToCsv(File file, String[] header,
			List<String[]> content) throws IOException {
		CsvListWriter writer = null;
		try {
			writer = new CsvListWriter(new FileWriter(file),
					CsvPreference.EXCEL_PREFERENCE);
			writer.writeHeader(header);
			for (String[] str : content) {
				writer.write(str);
			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			writer.close();
		}
	}

	/**
	 * 写入csv文件
	 * 
	 * @param file
	 *            File
	 * @param content
	 *            内容
	 * @throws IOException
	 */
	public static void writeContentToCsv(File file, List<String[]> content)
			throws IOException {
		CsvListWriter writer = null;
		try {
			writer = new CsvListWriter(new FileWriter(file),
					CsvPreference.EXCEL_PREFERENCE);
			for (String[] str : content) {
				writer.write(str);
			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			writer.close();
		}
	}

	/**
	 * 写入csv文件(头部)
	 * 
	 * @param file
	 *            File
	 * @param content
	 *            内容
	 * @throws IOException
	 */
	public static void writeHeaderToCsv(File file, String[] header)
			throws IOException {
		CsvListWriter writer = null;
		try {
			writer = new CsvListWriter(new FileWriter(file),
					CsvPreference.EXCEL_PREFERENCE);
			writer.writeHeader(header);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			writer.close();
		}
	}

	/**
	 * 将Bean List写入csv文件
	 * 
	 * @param file
	 * @param list
	 */
	public static void writeBeanToCsv(File file, Object sourse,
			String[] columns, CellProcessor[] processor) {

		CsvBeanWriter writer = null;
		try {
			if (!file.exists()) {
				FileUtils.touch(file);
			}
			writer = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			writer.write(sourse, columns, processor);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e1) {
				// nothing
			}
		}

	}
}
