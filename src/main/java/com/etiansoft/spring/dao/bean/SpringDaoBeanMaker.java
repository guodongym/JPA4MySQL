package com.etiansoft.spring.dao.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.etiansoft.util.MarkerUtil;


public class SpringDaoBeanMaker {

	public static void main(String[] args) throws Throwable {
		StringBuffer folder = new StringBuffer();
		folder.append("dist");
		folder.append(File.separator);
		folder.append("elf");
		folder.append(File.separator);
		folder.append("account");
		folder.append(File.separator);
		folder.append("server");
		folder.append(File.separator);
		folder.append("dao");
		File[] daoFiles = MarkerUtil.getFiles(folder.toString());
		System.out.println("beanDao\n");
		for (File daoFile : daoFiles) {
			if (!daoFile.isFile()) {
				continue;
			}
			printXmlFordaoFile(daoFile);
		}
		System.out.println("\n\n<!--------------------generate----------------------->\n");
		System.out.println("done!");
	}

	protected static void printXmlFordaoFile(File daoFile) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(daoFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("package")) {
				String packageName = line.split("([\\s]|;)+")[1];
				printXml(packageName, daoFile.getName());
			}
		}
		reader.close();
	}

	private static void printXml(String packageName, String fileName1) {
		String className = fileName1.substring(0, fileName1.indexOf("."));
		System.out.println("\t<bean id=\"" + String.valueOf(className.charAt(0)).toLowerCase() + className.substring(1) + "\" class=\"org.springframework.orm.ibatis3.MapperFactoryBean\">");
		System.out.println("\t\t<property name=\"sqlSessionTemplate\" ref=\"oracleSqlSessionTemplate\" />");
		System.out.println("\t\t<property name=\"mapperInterface\" value=\"" + packageName + "." + className + "\" />");
		System.out.println("\t</bean>");
	}
}
