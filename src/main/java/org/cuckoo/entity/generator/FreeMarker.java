package org.cuckoo.entity.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarker {
	
	private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
	
	static {
		try {
			String templatePathDir = "src/main/resources/templates";
			cfg.setDirectoryForTemplateLoading(new File(templatePathDir));
			cfg.setDefaultEncoding("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void create(String templateFileName, Map<String, Object> model, String outputFilePath) {
		try {
			File file = new File(outputFilePath);
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			Writer writer = new BufferedWriter(osw);
			
			Template template = cfg.getTemplate(templateFileName);
			template.process(model, writer);
			
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
