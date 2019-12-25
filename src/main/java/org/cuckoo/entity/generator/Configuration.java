package org.cuckoo.entity.generator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.cuckoo.universal.utils.StringUtils;

public class Configuration {
	
	private static final Properties prop = new Properties();
	
	public static void init(String propertiesFilePath) throws FileNotFoundException, IOException {
		prop.load(new FileInputStream(propertiesFilePath));
	}
	
	public static String get(String key) {
		return prop.getProperty(key);
	}
	
	public static String getJavaTypeByJdbcType(String databaseProductName, String jdbcType) {
		
		String javaType = null;
		
		if (databaseProductName.equals("PostgreSQL")) {
			javaType = prop.getProperty("db.column.type.pgsql."+jdbcType);
		} else if (databaseProductName.equals("MySQL")) {
			javaType = prop.getProperty("db.column.type.mysql."+jdbcType);
		}
		
		if (StringUtils.isNullOrEmpty(javaType)) {
			String message = "The data type["+jdbcType+"] is not configured yet, Use the string by default.";
			if (!EntityGenerator.messages.contains(message)) {
				EntityGenerator.messages.add(message);
			}
			javaType = "java.lang.String";
		}
		return javaType;
	}
}
