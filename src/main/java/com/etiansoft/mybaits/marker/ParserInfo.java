package com.etiansoft.mybaits.marker;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParserInfo {

	// #<tableName>
	private String tableName;
	// #<tableComment>
	private String tableComment;
	// #<sequenceName>
	private String sequenceName;
	// #<daoPackage>
	private String daoPackage;
	// #<daoName>
	private String daoName;
	// #<servicePackage>
	private String servicePackage;
	// #<serviceName>
	private String serviceName;
	// #<modelPackage>
	private String modelPackage;
	// #<modelName>
	private String modelName;

	private List<Field> fields;

	public ParserInfo(Connection connection, String parentPackageName, String tableName, String sequenceName) throws Exception {
		fields = new ArrayList<Field>();
		modelPackage = parentPackageName + ".po";
		daoPackage = parentPackageName + ".dao";
		servicePackage = parentPackageName + ".service";
		this.tableName = tableName;
		if (sequenceName == null) {
			sequenceName = tableName + "_S";
		}
		this.sequenceName = sequenceName;
		String javaName = getJavaName(tableName);
		modelName = javaName;
		daoName = javaName + "Dao";
		serviceName = javaName + "Service";

		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `" + tableName + "`");
		ResultSetMetaData metaData = rs.getMetaData();
		DatabaseMetaData dbMetaData = connection.getMetaData();
		int columnCount = metaData.getColumnCount();
		String primaryKey = null;
		ResultSet primaryKeys = dbMetaData.getPrimaryKeys(null, null, tableName);
		if (primaryKeys.next()) {
			primaryKey = primaryKeys.getString(4);
		}
		ResultSet tableInfos = statement.executeQuery("SHOW TABLE STATUS");
		while (tableInfos.next()) {
			if (tableName.equalsIgnoreCase(tableInfos.getString("NAME"))) {
				tableComment = tableInfos.getString("COMMENT");
				if (tableComment == null) {
					tableComment = "";
				}
				break;
			}
		}
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnName = metaData.getColumnName(columnIndex);
			String columnTypeName = metaData.getColumnTypeName(columnIndex);
			ResultSet column = dbMetaData.getColumns(null, null, tableName, columnName);
			column.next();
			String description = column.getString(12);
			String type = columnTypeName.toUpperCase();
			int index = type.indexOf('(');
			if (index != -1) {
				type = type.substring(0, index);
			}
			int scale = metaData.getScale(columnIndex);
			int precision = metaData.getPrecision(columnIndex);
			Field field = new Field(columnName, type, getPropertyKeyByColumnName(columnName), columnName.equals(primaryKey), precision, scale);
			field.setFieldName(columnName);
			if ("NO".equals(column.getString(18))) {
				field.setMandatory(true);
			}
			field.setDescription(description);
			fields.add(field);
		}
		rs.close();
		statement.close();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableComment() {
		return tableComment;
	}

	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public String getDaoPackage() {
		return daoPackage;
	}

	public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}

	public String getDaoName() {
		return daoName;
	}

	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}

	public String getModelPackage() {
		return modelPackage;
	}

	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	private String getJavaName(String tableName) {
		String[] segments = tableName.split("_");
		if (segments.length == 1) {
			return getSegment(segments[0]);
		}
		StringBuilder javaName = new StringBuilder();
		for (int i = 0; i < segments.length; i++) {
			String segment = segments[i];
			javaName.append(getSegment(segment));
		}
		return javaName.toString();
	}

	private String getSegment(String segment) {
		return Character.toUpperCase(segment.charAt(0)) + segment.substring(1).toLowerCase();
	}

	private String getPropertyKeyByColumnName(String columnName) {
		String[] split = columnName.split("_");
		StringBuffer buffer = new StringBuffer();
		buffer.append(split[0].toLowerCase());
		for (int i = 1; i < split.length; i++) {
			String lowerCaseSplit = split[i].toLowerCase();
			String firstUpperChar = (lowerCaseSplit.charAt(0) + "").toUpperCase();
			buffer.append(firstUpperChar + lowerCaseSplit.substring(1));
		}
		return buffer.toString();
	}
}
