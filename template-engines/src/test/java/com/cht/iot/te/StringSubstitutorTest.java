package com.cht.iot.te;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringSubstitutorTest {

	@Test
	void test() {
		Map<String, String> vars = new HashMap<>();
		vars.put("name", "IoT");
		vars.put("version", "1.0.0");
		
		String template = "Name is '${name}'. Version is '${version}'. Birthday is '${birthday:-2020/09/10}'.";
		
		String text = StringSubstitutor.replace(template, vars);
		
		log.info(text);
	}
}
