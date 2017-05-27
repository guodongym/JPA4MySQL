package com.etiansoft.mybaits.marker.field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etiansoft.util.MarkerUtil;


public class DaoFieldMarker {

	public static void main(String[] args) throws Throwable {
		File[] fieldsFiles = MarkerUtil.getFiles("fields");
		for (File fieldsFile : fieldsFiles) {
			if (!fieldsFile.isFile()) {
				continue;
			}
			System.out.println(fieldsFile.getName());
			printXmlForFieldsFile(fieldsFile);
		}
		System.out.println("done!");
	}

	private static void printXmlForFieldsFile(File fieldsFile) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fieldsFile));
		String packageName = "";
		String className = "";
		List<String> fields = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("package")) {
				String result = line.substring(7);
				packageName = result.substring(0, result.indexOf(';')).trim();
			} else if (line.startsWith("private") && !line.matches(".*\\sstatic\\s.*")) {
				String fieldName = line.split("([\\s]|;)+")[2];
				fields.add(fieldName);
			} else if (line.contains("class ")) {
				int index = line.indexOf("class ");
				className = line.substring(index + 6, line.indexOf(' ', index + 7)).trim();
			}
		}

		String fullClassName = packageName.isEmpty() ? className : packageName + "." + className;
		System.out.println("\t<resultMap id=\"ViewResultMap\" type=\"" + fullClassName + "\">");
		for (String fieldName : fields) {
			 printXmlField(fieldName);
		}
		System.out.println("\t</resultMap>");
		reader.close();
		System.out.println("<!--------------------generate----------------------->\n\n\n");
	}

	private static void printXmlField(String fieldName) {
		String xmlFieldName = getXmlFieldName(fieldName);
		System.out.println("\t\t<result column=\"" + xmlFieldName + "\" property=\"" + fieldName + "\" />");
	}

	private static String getXmlFieldName(String fieldName) {
		char[] cs = fieldName.toCharArray();
		List<StringBuilder> segments = new ArrayList<StringBuilder>();
		StringBuilder segment = new StringBuilder(20);
		for (char c : cs) {
			if (Character.isUpperCase(c)) {
				segments.add(segment);
				segment = new StringBuilder(20);
			}
			segment.append(Character.toUpperCase(c));
		}
		segments.add(segment);
		StringBuilder result = new StringBuilder(100);
		result.append(segments.get(0));
		for (int i = 1; i < segments.size(); i++) {
			result.append('_').append(segments.get(i));
		}
		return result.toString();
	}

}
