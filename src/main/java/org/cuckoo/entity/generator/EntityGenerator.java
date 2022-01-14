package org.cuckoo.entity.generator;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cuckoo.entity.generator.utils.FileGenerationUtils;

/**
 * EntityGenerator
 *
 * 打包插件：maven-assembly-plugin，运行命令：mvn assembly:assembly
 */
public class EntityGenerator {
	
	public static List<String> messages = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.out.println("Usage errors, You must run the command like this:");
			System.out.println("java -jar /target/entity-generator-x.x.x.jar /storage/workspace/idea/project/src/main/resources/entity.properties");
			System.exit(0);
		}
		
		Configuration.init(args[0]);
		
		File fileOutputDir = new File(Configuration.get("file.output.dir"));
		if (!fileOutputDir.exists()) {
			fileOutputDir.mkdirs();
		}
		
		JDBC jdbc = new JDBC(Configuration.get("jdbc.driverClassName"), Configuration.get("jdbc.url"), Configuration.get("jdbc.username"), Configuration.get("jdbc.password"));
		String databaseProductName = jdbc.getDatabaseProductName();
		List<String> tableNames = jdbc.getAllTables();
		tableNames.forEach(tableName -> {
			try {
				List<Map<String, String>> tableColumns = jdbc.getAllColumns(tableName);
				FileGenerationUtils.generateEntityFile(databaseProductName, tableName, tableColumns);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		
		EntityGenerator.messages.forEach(message -> System.out.println(message));
		System.out.println("completed!");
	}

}
