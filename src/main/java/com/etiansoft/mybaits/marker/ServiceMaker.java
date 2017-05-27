package com.etiansoft.mybaits.marker;

import java.io.IOException;

import com.etiansoft.util.MarkerUtil;


public class ServiceMaker {

	private ParserInfo parserInfo;

	public ServiceMaker(ParserInfo parserInfo) {
		this.parserInfo = parserInfo;
	}

	public void makeInterface() throws Exception, IOException {
		MarkerUtil.writeContentToFile(MarkerUtil.createFile(parserInfo.getServicePackage(), parserInfo.getServiceName(), ".java"), generateDaoInterfaceContent());
	}

	private String generateDaoInterfaceContent() throws IOException {
		String templateFileContent = MarkerUtil.getTemplateFileContent("ServiceTemplate");
		String daoInterfaceContent = templateFileContent.replaceAll("#<modelPackage>", parserInfo.getModelPackage());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<tableComment>", parserInfo.getTableComment());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<tableName>", parserInfo.getTableName());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<servicePackage>", parserInfo.getServicePackage());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<serviceName>", parserInfo.getServiceName());
		daoInterfaceContent = daoInterfaceContent.replaceAll("#<modelName>", parserInfo.getModelName());
		return daoInterfaceContent.trim();
	}

}
