package com.cht.iot.te;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThymeleafTest {

	@Test
	void test() {		
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		
		TemplateEngine engine = new TemplateEngine();
		engine.setTemplateResolver(resolver);
		
		Map<String, Object> vars = new HashMap<>();
		vars.put("list", Arrays.asList("AAA", "BBB", "", "CCC"));
		vars.put("validator", new Validator());
		
		Context context = new Context(Locale.getDefault(), vars);
		
		StringWriter writer = new StringWriter();
		
		engine.process("example", context, writer);
		
		String text = writer.toString();
		
		log.info("\n{}", text);		
	}
	
	public static class Validator {
		
		public boolean isNotBlank(String s) {
			return (s != null) && (s.trim().isEmpty() == false);
		}
	}
}
