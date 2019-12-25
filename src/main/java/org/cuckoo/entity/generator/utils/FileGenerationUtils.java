package org.cuckoo.entity.generator.utils;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cuckoo.entity.generator.Configuration;
import org.cuckoo.entity.generator.FreeMarker;
import org.cuckoo.universal.utils.StringUtils;
import org.cuckoo.universal.utils.db.EntityTableTransformUtils;

public class FileGenerationUtils {

	public static void generateEntityFile(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		
		ModelDataBuilder modelDataBuilder = ModelDataBuilder.build(databaseProductName, tableName, tableColumns);
		
		Map<String, Object> model = new HashMap<>();
		model.put("tableName", tableName);
		model.put("entityName", EntityTableTransformUtils.fromTableNameToEntityName(tableName));
		model.put("entityPackage", Configuration.get("file.package"));
		model.put("entitySerialVersionUID", Configuration.get("enable.serializable").equals("true") ? StringUtils.uniqueNumString()+"L" : null);
		model.put("entityImportStatements", modelDataBuilder.entityImportStatements);
		model.put("entityPropertyStatements", modelDataBuilder.entityPropertyStatements);
		model.put("entityAssociatedEntityStatements", modelDataBuilder.entityAssociatedEntityStatements);
		model.put("entityConstructorStatements", modelDataBuilder.entityConstructorStatements);
		model.put("entityGetAndSetMethodStatements", modelDataBuilder.entityGetAndSetMethodStatements);
		model.put("entityFileCreateDate", LocalDate.now());
		
		String outputFilePath = Configuration.get("file.output.dir") + File.separator + model.get("entityName") + ".java";
		FreeMarker.create("entity.ftl", model, outputFilePath);
	}
	
	public static void generateMybatisMapperFile(String databaseProductName, String tableName, List<Map<String, String>> tableColumns) {
		
	}
}
