package com.etiansoft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;


public class MarkerUtil {

	public static String DIST = "dist";

	public static File[] getFiles(String folder) throws UnsupportedEncodingException {
		String projectFolder = getProjectFolder(MarkerUtil.class);
		File fieldsFolder = new File(projectFolder + File.separator + folder);
		File[] fieldsFiles = fieldsFolder.listFiles();
		return fieldsFiles;
	}

	public static String getProjectFolder(Class<?> clazz) throws UnsupportedEncodingException {
		String classFolder = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();
		classFolder = URLDecoder.decode(classFolder, "UTF-8");
		int index = classFolder.indexOf("target");
		return classFolder.substring(0, index);
	}

	public static void writeContentToFile(File file, String content) throws Exception {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content.getBytes("UTF-8"));
			fos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static File createFile(String packagee, String fileName, String postFix) {
		String currrntPath = new File("").getAbsolutePath();
		String pkgPath = packagee.replace(".", File.separator);
		String filePath = currrntPath + File.separator + MarkerUtil.DIST + File.separator + pkgPath + File.separator + fileName + postFix;
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		return file;
	}

	public static String getTemplateFileContent(String fileName) throws IOException {
		URL template = MarkerUtil.class.getClassLoader().getResource(fileName);
		InputStream input = template.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuffer buffer = new StringBuffer();
		char[] cs = new char[1024];
		int length = -1;
		while ((length = reader.read(cs)) != -1) {
			buffer.append(cs, 0, length);
		}
		input.close();
		return buffer.toString().trim();
	}
}
