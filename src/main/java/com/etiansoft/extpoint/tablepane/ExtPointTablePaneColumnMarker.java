package com.etiansoft.extpoint.tablepane;

import com.etiansoft.util.MarkerUtil;

import java.io.*;


public class ExtPointTablePaneColumnMarker {

	public static void main(String[] args) throws Throwable {
		File[] columnsFiles = MarkerUtil.getFiles("fields");
		for (File columnsFile : columnsFiles) {
			if (!columnsFile.isFile()) {
				continue;
			}
			System.out.println(columnsFile.getName());
			printXmlForColumnsFile(columnsFile);
		}
		System.out.println("done!");
	}

	protected static void printXmlForColumnsFile(File columnsFile) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(columnsFile));
		String line = null;
		boolean isValidMark = false;
		String mark = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!isValidMark && mark != null) {
				mark = null;
			}
			if (line.startsWith("/**") && line.endsWith("*/")) {
				mark = line.split("([\\s])+")[1];
				isValidMark = true;
				continue;
			}
			if (line.startsWith("private") && !line.matches(".*\\sstatic\\s.*")) {
				String fieldName = line.split("([\\s]|;)+")[2];
				if (mark == null) {
					mark = fieldName;
				}
				printXml(fieldName, mark);
			}
			isValidMark = false;
		}
		reader.close();
		System.out.println("<!--------------------generate----------------------->\n\n\n");
	}

	private static void printXml(String fieldName, String mark) {
		System.out.println("\t\t<column");
		System.out.println("\t\t\tid=\"" + fieldName + "\"");
		System.out.println("\t\t\tname=\"" + mark + "\"");
		System.out.println("\t\t\tmoveable=\"false\"");
		System.out.println("\t\t\tresizable=\"false\"");
		System.out.println("\t\t\twidth=\"100\">");
		System.out.println("\t\t</column>");
	}
}
