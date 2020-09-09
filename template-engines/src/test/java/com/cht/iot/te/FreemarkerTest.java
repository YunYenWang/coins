package com.cht.iot.te;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FreemarkerTest {

	@Test
	void test() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {		
		Configuration freemarker = new Configuration(Configuration.VERSION_2_3_30);
		freemarker.setClassForTemplateLoading(getClass(), "/");
		
		Map<String, Object> context = new HashMap<>();
		context.put("list", Arrays.asList("AAA", "BBB", "", "CCC"));
		context.put("validator", new Validator());
		
		Template template = freemarker.getTemplate("example.freemarker");
		
		StringWriter writer = new StringWriter();
		
		template.process(context, writer);
		
		String text = writer.toString();
		
		log.info("\n{}", text);		
	}
	
	public static class Validator {
		
		public boolean isNotBlank(String s) {
			return (s != null) && (s.trim().isEmpty() == false);
		}
	}
}
