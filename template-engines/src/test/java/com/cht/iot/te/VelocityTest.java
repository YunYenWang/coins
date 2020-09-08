package com.cht.iot.te;

import java.io.StringWriter;
import java.util.Arrays;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VelocityTest {

	@Test
	void test() {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");		
		engine.init();		
		
		VelocityContext context = new VelocityContext();
		context.put("list", Arrays.asList("AAA", "BBB", "", "CCC"));
		context.put("validator", new Validator());
		
		Template template = engine.getTemplate("/example.velocity");
		
		StringWriter writer = new StringWriter();
		
		template.merge(context, writer);
		
		String text = writer.toString();
		
		log.info("\n{}", text);		
	}
	
	public static class Validator {
		
		public boolean isNotBlank(String s) {
			return (s != null) && (s.trim().isEmpty() == false);
		}
	}
}
