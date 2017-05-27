package com.etiansoft.mybaits.marker;

import java.io.IOException;
import java.util.List;

import com.etiansoft.util.MarkerUtil;

public class DaoModelMaker {

	private ParserInfo parserInfo;

	public DaoModelMaker(ParserInfo parserInfo) {
		this.parserInfo = parserInfo;
	}

	public void makeModel() throws IOException, Exception {
		String generateDaoModelContent = generateDaoModelContent();
		MarkerUtil.writeContentToFile(MarkerUtil.createFile(parserInfo.getModelPackage(), parserInfo.getModelName(), ".java"), generateDaoModelContent);
	}

	private String generateDaoModelContent() throws IOException {
		String templateFileContent = MarkerUtil.getTemplateFileContent("ModelTemplate");
		String daoModelContent = templateFileContent.replaceAll("#<modelPackage>", parserInfo.getModelPackage());
		daoModelContent = daoModelContent.replaceAll("#<tableComment>", parserInfo.getTableComment());
		daoModelContent = daoModelContent.replaceAll("#<tableName>", parserInfo.getTableName());
		daoModelContent = daoModelContent.replaceAll("#<modelName>", parserInfo.getModelName());
		daoModelContent = daoModelContent.replaceAll("#<serialVersion>", "1");
		daoModelContent = daoModelContent.replaceAll("#<fieldsContent>", getFieldsContent(parserInfo.getModelName()));
		daoModelContent = daoModelContent.replaceAll("#<getterSetterContent>", getGetterSetterContent());
		return daoModelContent;
	}

	private String getFieldsContent(String modelName) {
		List<Field> FIELDS = parserInfo.getFields();
		StringBuffer buffer = new StringBuffer();
		for (Field field : FIELDS) {
			String template = "\t@Column(name = \"#<fieldName>\")\n\tprivate #<javaType> #<propertyName>;\n";
			if (field.isKey()) {
				if ("Integer".equals(field.getJavaType())) {
					template = "\t@Column(name = \"#<fieldName>\")\n\tprivate #<javaType> id;\n";
					template = "\t@Id\n\t@GeneratedValue(generator = \"#<modelName>Generator\")\n\t@GenericGenerator(name = \"#<modelName>Generator\", strategy = \"native\")\n" + template;
				} else {
					template = "\t@Column(name = \"#<fieldName>\")\n\tprivate #<javaType> code;\n";
					template = "\t@Id\n" + template;
				}
			}
			template = "\t/** #<propertyDescription> */\n" + template;
			String property = template.replaceAll("#<javaType>", field.getJavaType());
			property = property.replaceAll("#<propertyName>", field.getPropertyName());
			String description = field.getDescription();
			if (description != null && !description.trim().equals("")) {
				property = property.replaceAll("#<propertyDescription>", description);
			} else {
				property = property.replaceAll("#<propertyDescription>", "");
			}
			property = property.replaceAll("#<fieldName>", field.getFieldName());
			property = property.replaceAll("#<modelName>", modelName);
			buffer.append(property);
		}
		return buffer.toString();
	}

	private String getGetterSetterContent() {
		StringBuffer buffer = new StringBuffer();
		List<Field> FIELDS = parserInfo.getFields();
		for (Field field : FIELDS) {
			buffer.append(getGetterContent(field));
			buffer.append(getSetterContent(field));
		}
		return buffer.toString();
	}

	private String getGetterContent(Field field) {
		String fieldType = field.getFieldType();
		if (field.isKey()) {
			String templateGetter = null;
			if ("Integer".equals(field.getJavaType())) {
				templateGetter = "\tpublic #<javaType> getId() {\n\t\treturn id;\n\t}\n\n";
			} else {
				templateGetter = "\tpublic #<javaType> getCode() {\n\t\treturn code;\n\t}\n\n";
			}
			return getMetholdContent(templateGetter, field);
		} else {
			String templateGetter = "\tpublic #<javaType> get#<metholdPostName>() {\n\t\treturn #<propertyName>;\n\t}\n\n";
			if ("BIT".equals(fieldType)) {
				templateGetter = "\tpublic #<javaType> is#<metholdPostName>() {\n\t\treturn #<propertyName>;\n\t}\n\n";
			}
			return getMetholdContent(templateGetter, field);
		}
	}

	private String getSetterContent(Field field) {
		if (field.isKey()) {
			String templateSetter = null;
			if ("Integer".equals(field.getJavaType())) {
				templateSetter = "\tpublic void setId(#<javaType> id) {\n\t\tthis.id = id;\n\t}\n\n";
			} else {
				templateSetter = "\tpublic void setCode(#<javaType> code) {\n\t\tthis.code = code;\n\t}\n\n";
			}
			return getMetholdContent(templateSetter, field);
		} else {
			String templateSetter = "\tpublic void set#<metholdPostName>(#<javaType> #<propertyName>) {\n\t\tthis.#<propertyName> = #<propertyName>;\n\t}\n\n";
			return getMetholdContent(templateSetter, field);
		}
	}

	private String getMetholdContent(String template, Field field) {
		StringBuffer buffer = new StringBuffer();
		String methold = template.replaceAll("#<javaType>", field.getJavaType());
		String propertyName = field.getPropertyName();
		methold = methold.replaceAll("#<metholdPostName>", propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
		methold = methold.replaceAll("#<propertyName>", field.getPropertyName());
		buffer.append(methold);
		return buffer.toString();
	}
}
