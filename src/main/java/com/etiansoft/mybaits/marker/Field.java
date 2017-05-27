package com.etiansoft.mybaits.marker;

public class Field {

	private String fieldName;

	private String fieldType;

	private String propertyName;

	private String description;

	private boolean isKey;

	private final int precision;

	private int scale;

	private boolean mandatory;

	public Field(String fieldName, String fieldType, String propertyName, boolean isKey, int precision, int scale) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.propertyName = propertyName;
		this.isKey = isKey;
		this.precision = precision;
		this.scale = scale;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isKey() {
		return isKey;
	}

	public String getJavaType() {
		if (fieldType.toUpperCase().startsWith("VARCHAR")) {
			return "String";
		} else if (fieldType.toUpperCase().startsWith("CHAR")) {
			return "String";
		} else if (fieldType.toUpperCase().startsWith("TEXT")) {
			return "String";
		} else if (fieldType.toUpperCase().startsWith("DATE")) {
			return "Date";
		} else if (fieldType.toUpperCase().startsWith("DECIMAL")) {
			if (scale > 0) {
				return "BigDecimal";
			}
			if (precision == 1) {
				return "boolean";
			}
			if (precision > 9) {
				return "long";
			}
			return "int";
		} else if (fieldType.toUpperCase().startsWith("DATETIME")) {
			return "Date";
		} else if (fieldType.toUpperCase().startsWith("LONG")) {
			return "String";
		} else if (fieldType.toUpperCase().startsWith("INT")) {
			return "Integer";
		} else if (fieldType.toUpperCase().startsWith("BIT")) {
			return "boolean";
		} else {
			throw new RuntimeException("please implenents jdbc type: " + fieldType + "'s java type mapping!");
		}
	}

	@Override
	public String toString() {
		return fieldName + "(Key:" + isKey + ")" + "_" + fieldType + "_" + propertyName;
	}

	public String getDescription() {
		if (mandatory) {
			return description + " (Not Null)";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
}
