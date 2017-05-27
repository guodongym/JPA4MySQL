package com.etiansoft.mybaits.marker.field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etiansoft.util.MarkerUtil;

public class FieldAssignMarker {

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
		List<String> fields = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("private") && !line.matches(".*\\sstatic\\s.*")) {
				String fieldName = line.split("([\\s]|;)+")[2];
				fields.add(fieldName);
			}
		}

		for (String fieldName : fields) {
			printFromField(fieldName);
		}
		System.out.println("\n\n");
		for (String fieldName : fields) {
			printToField(fieldName);
		}
		reader.close();
		System.out.println("<!--------------------generate----------------------->\n\n\n");
	}

	private static void printFromField(String fieldName) {
		String postfix = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		System.out.println("this.set" + postfix + "(po.get" + postfix + "());");
	}
	
	private static void printToField(String fieldName) {
		String postfix = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		System.out.println("po.set" + postfix + "(this.get" + postfix + "());");
	}
}
