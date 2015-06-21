package com.weishang.repeater.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {

	public static String stream2String(final InputStream instream) throws IOException {
		final StringBuilder sb = new StringBuilder();
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally {
			closeStream(instream);
		}
		return sb.toString();
	}

	/**
	 * 保存将字符串保存到指定文件中 （文件名为当前的毫秒值，格式化）
	 */
	public static void saveFile(String content, File file) {
		try {
			if (file.exists()) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file, DateUtils.getFormatTime(System.currentTimeMillis()) + ".txt")));
				writer.write(content);
				// 关闭流对象
				IOUtils.closeStream(writer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存将字符串保存到指定文件中
	 * 
	 * @param content
	 *            文件内容
	 * @param file
	 *            文件的路径（不包含文件名）
	 */
	public static void saveFile(String content, File file, String filename) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(file, filename)));
			writer.write(content);
		} catch (Exception e) {
		} finally {
			IOUtils.closeStream(writer);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 *            文件路径
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 关闭IO流对象
	 * 
	 * @param stream
	 */
	public static void closeStream(Closeable stream) {
		if (null != stream) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 获取文件的内容，以字符串的形式返回， 注：暂没有考虑编码问题
	 * 
	 * @return 文件内容，如果没有文件，返回空
	 */
	public static String getFileContent(File file) {
		if (file.exists()) {
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader = null;
			try {
				FileReader reader = new FileReader(file);
				bufferedReader = new BufferedReader(reader);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					builder.append(lineTxt);
				}
				bufferedReader.close();
			} catch (Exception e) {
			} finally {
				if (bufferedReader != null) {
					bufferedReader = null;
				}
			}
			return builder.toString();
		}
		return null;
	}
}
