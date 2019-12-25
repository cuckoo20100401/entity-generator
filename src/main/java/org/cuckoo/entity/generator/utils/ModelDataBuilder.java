package org.cuckoo.entity.generator.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.cuckoo.entity.generator.Configuration;
import org.cuckoo.universal.utils.StringUtils;
import org.cuckoo.universal.utils.db.EntityTableTransformUtils;

public class ModelDataBuilder {
	
	public List<String> entityImportStatements = new ArrayList<>();
	public List<String> entityPropertyStatements = new ArrayList<>();
	public List<String> entityAssociatedEntityStatements = new ArrayList<>();
	public List<String> entityConstructorStatements = new ArrayList<>();
	public List<String> entityGetAndSetMethodStatements = new ArrayList<>();
	
	public static ModelDataBuilder build(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		ModelDataBuilder modelDataBuilder = new ModelDataBuilder();
		modelDataBuilder.entityImportStatements = modelDataBuilder.buildEntityImportStatements(databaseProductName, tableName, tableColumns);
		modelDataBuilder.entityPropertyStatements = modelDataBuilder.buildEntityPropertyStatements(databaseProductName, tableName, tableColumns);
		modelDataBuilder.entityAssociatedEntityStatements = modelDataBuilder.buildAssociatedEntityStatements(databaseProductName, tableName, tableColumns);
		modelDataBuilder.entityConstructorStatements = modelDataBuilder.buildEntityConstructorStatements(databaseProductName, tableName, tableColumns);
		modelDataBuilder.entityGetAndSetMethodStatements = modelDataBuilder.buildEntityGetAndSetMethodStatements();
		return modelDataBuilder;
	}

	private List<String> buildEntityPropertyStatements(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		
		List<String> entityPropertyStatements = new ArrayList<>();
		
		tableColumns.forEach(tableColumn -> {
			String propertyType = Configuration.getJavaTypeByJdbcType(databaseProductName, tableColumn.get("column_type")).split("\\.")[2];
			String propertyName = EntityTableTransformUtils.fromTableColumnNameToEntityPropertyName(tableColumn.get("column_name"));
			entityPropertyStatements.add("private "+propertyType+" "+propertyName);
		});
		return entityPropertyStatements;
	}
	
	private List<String> buildAssociatedEntityStatements(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		
		List<String> associatedEntityStatements = new ArrayList<>();
		
		String currEntityName = EntityTableTransformUtils.fromTableNameToEntityName(tableName);
		
		String manyToOneConfigValue = Configuration.get(currEntityName+".ManyToOne");
		if (!StringUtils.isNullOrEmpty(manyToOneConfigValue)) {
			String[] associatedEntityNames = manyToOneConfigValue.split(",");
			for (String associatedEntityName: associatedEntityNames) {
				associatedEntityName = associatedEntityName.trim();
				String associatedEntityStatement = "private "+associatedEntityName+" "+this.toManyToOnePropertyName(associatedEntityName);
				associatedEntityStatements.add(associatedEntityStatement);
			}
		}
		
		String oneToManyConfigValue = Configuration.get(currEntityName+".OneToMany");
		if (!StringUtils.isNullOrEmpty(oneToManyConfigValue)) {
			String[] associatedEntityNames = oneToManyConfigValue.split(",");
			for (String associatedEntityName: associatedEntityNames) {
				associatedEntityName = associatedEntityName.trim();
				String associatedEntityStatement = "private List<"+associatedEntityName+"> "+this.toOneToManyPropertyName(associatedEntityName);
				associatedEntityStatements.add(associatedEntityStatement);
			}
		}
		
		return associatedEntityStatements;
	}
	private List<String> buildEntityGetAndSetMethodStatements() {
		
		List<String> entityGetAndSetMethodStatements = new ArrayList<>();
		
		this.entityPropertyStatements.forEach(statement -> {
			
			String propertyType = statement.split(" ")[1];
			String propertyName = statement.split(" ")[2];
			
			String getMethodStatement = "public [propertyType] [methodName]() { return [propertyName]; }";
			getMethodStatement = getMethodStatement.replace("[propertyType]", propertyType);
			getMethodStatement = getMethodStatement.replace("[propertyName]", propertyName);
			getMethodStatement = getMethodStatement.replace("[methodName]", EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName(propertyName));
			entityGetAndSetMethodStatements.add(getMethodStatement);
			
			String setMethodStatement = "public void [methodName]([propertyType] [propertyName]) { this.[propertyName] = [propertyName]; }";
			setMethodStatement = setMethodStatement.replace("[propertyType]", propertyType);
			setMethodStatement = setMethodStatement.replace("[propertyName]", propertyName);
			setMethodStatement = setMethodStatement.replace("[methodName]", EntityTableTransformUtils.fromEntityPropertyNameToSetMethodName(propertyName));
			entityGetAndSetMethodStatements.add(setMethodStatement);
		});
		
		this.entityAssociatedEntityStatements.forEach(statement -> {
			
			String propertyType = statement.split(" ")[1];
			String propertyName = statement.split(" ")[2];
			
			String getMethodStatement = "public [propertyType] [methodName]() { return [propertyName]; }";
			getMethodStatement = getMethodStatement.replace("[propertyType]", propertyType);
			getMethodStatement = getMethodStatement.replace("[propertyName]", propertyName);
			getMethodStatement = getMethodStatement.replace("[methodName]", EntityTableTransformUtils.fromEntityPropertyNameToGetMethodName(propertyName));
			entityGetAndSetMethodStatements.add(getMethodStatement);
			
			String setMethodStatement = "public void [methodName]([propertyType] [propertyName]) { this.[propertyName] = [propertyName]; }";
			setMethodStatement = setMethodStatement.replace("[propertyType]", propertyType);
			setMethodStatement = setMethodStatement.replace("[propertyName]", propertyName);
			setMethodStatement = setMethodStatement.replace("[methodName]", EntityTableTransformUtils.fromEntityPropertyNameToSetMethodName(propertyName));
			entityGetAndSetMethodStatements.add(setMethodStatement);
		});
		
		return entityGetAndSetMethodStatements;
	}
	
	private List<String> buildEntityImportStatements(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		
		List<String> statements = new ArrayList<>();
		
		tableColumns.forEach(tableColumn -> {
			String propertyType = Configuration.getJavaTypeByJdbcType(databaseProductName, tableColumn.get("column_type"));
			if (!propertyType.startsWith("java.lang.")) {
				String statement = "import "+propertyType;
				if (!statements.contains(statement)) {
					statements.add(statement);
				}
			}
		});
		
		String currEntityName = EntityTableTransformUtils.fromTableNameToEntityName(tableName);
		String oneToManyConfigValue = Configuration.get(currEntityName+".OneToMany");
		if (!StringUtils.isNullOrEmpty(oneToManyConfigValue)) {
			statements.add("import java.util.List");
		}
		
		if (Configuration.get("enable.serializable").equals("true")) {
			statements.add("import java.io.Serializable");
		}
		
		return statements;
	}
	
	private List<String> buildEntityConstructorStatements(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		
		List<String> statements = new ArrayList<>();
		
		String currEntityName = EntityTableTransformUtils.fromTableNameToEntityName(tableName);
		String constructorConfigValue = Configuration.get(currEntityName+".Constructor");
		if (!StringUtils.isNullOrEmpty(constructorConfigValue)) {
			for (String constructorConfigPattern: this.parseConstructorConfigValue(constructorConfigValue)) {
				String constructorStatement = "public [methodName]([methodArgs]) {[methodBody]}";
				String constructorArgs = "";
				String constructorBody = "";
				switch (constructorConfigPattern) {
					case "":
						break;
					case "...":
						for (String statement: this.entityPropertyStatements) {
							String propertyType = statement.split(" ")[1];
							String propertyName = statement.split(" ")[2];
							if (constructorArgs != "") {
								constructorArgs += ", ";
							}
							constructorArgs += propertyType+" "+propertyName;
							constructorBody += "\n\t\tthis."+propertyName+" = "+propertyName+";";
						}
						for (String statement: this.entityAssociatedEntityStatements) {
							String propertyType = statement.split(" ")[1];
							String propertyName = statement.split(" ")[2];
							if (constructorArgs != "") {
								constructorArgs += ", ";
							}
							constructorArgs += propertyType+" "+propertyName;
							constructorBody += "\n\t\tthis."+propertyName+" = "+propertyName+";";
						}
						constructorBody += "\r\t";
						break;
					default:
						for (String constructorConfigPropertyName: constructorConfigPattern.split(",")) {
							for (String statement: this.entityPropertyStatements) {
								String propertyType = statement.split(" ")[1];
								String propertyName = statement.split(" ")[2];
								if (propertyName.equals(constructorConfigPropertyName)) {
									if (constructorArgs != "") {
										constructorArgs += ", ";
									}
									constructorArgs += propertyType+" "+propertyName;
									constructorBody += "\n\t\tthis."+propertyName+" = "+propertyName+";";
								}
							}
							for (String statement: this.entityAssociatedEntityStatements) {
								String propertyType = statement.split(" ")[1];
								String propertyName = statement.split(" ")[2];
								if (propertyName.equals(constructorConfigPropertyName)) {
									if (constructorArgs != "") {
										constructorArgs += ", ";
									}
									constructorArgs += propertyType+" "+propertyName;
									constructorBody += "\n\t\tthis."+propertyName+" = "+propertyName+";";
								}
							}
						}
						constructorBody += "\r\t";
						break;
				}
				constructorStatement = constructorStatement.replace("[methodName]", currEntityName);
				constructorStatement = constructorStatement.replace("[methodArgs]", constructorArgs);
				constructorStatement = constructorStatement.replace("[methodBody]", constructorBody);
				statements.add(constructorStatement);
			}
		}
		
		return statements;
	}
	
	private String toManyToOnePropertyName(String associatedEntityName) {
		return StringUtils.makeFirstCharToLowerCase(associatedEntityName);
	}
	private String toOneToManyPropertyName(String associatedEntityName) {
		
		String result = null;
		
		if (Pattern.matches("((o)|(sh)|(ch))$", associatedEntityName)) {
			result = associatedEntityName + "es";
		} else if (associatedEntityName.endsWith("y")) {
			result = associatedEntityName.substring(0, associatedEntityName.length()-1) + "ies";
		} else {
			result = associatedEntityName + "s";
		}
		
		return StringUtils.makeFirstCharToLowerCase(result);
	}
	private List<String> parseConstructorConfigValue(String constructorConfigValue) {
    	List<String> patterns = new LinkedList<>();
    	for (String str: constructorConfigValue.split("\\],\\s{0,}\\[")) {
    		patterns.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
    	}
    	return patterns;
    }
}
