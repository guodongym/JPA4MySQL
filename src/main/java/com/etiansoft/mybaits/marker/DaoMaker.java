package com.etiansoft.mybaits.marker;

import java.io.IOException;

import com.etiansoft.util.MarkerUtil;


public class DaoMaker {

	private ParserInfo parserInfo;

	public DaoMaker(ParserInfo parserInfo) {
		this.parserInfo = parserInfo;
	}

	public void makeInterface() throws Exception, IOException {
		MarkerUtil.writeContentToFile(MarkerUtil.createFile(parserInfo.getDaoPackage(), parserInfo.getDaoName(), ".java"), generateDaoInterfaceContent());
	}

	private String generateDaoInterfaceContent() throws IOException {
		String templateFileContent = MarkerUtil.getTemplateFileContent("DaoTemplate");
		String daoInterfaceContent = templateFileContent.replaceAll("#<modelPackage>", parserInfo.getModelPackage());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<tableComment>", parserInfo.getTableComment());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<tableName>", parserInfo.getTableName());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<daoPackage>", parserInfo.getDaoPackage());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<daoName>", parserInfo.getDaoName());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<modelName>", parserInfo.getModelName());
		return daoInterfaceContent.trim();
	}

}
