package com.cht.iot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OptionalTest {

	Map<String, String> tables = new HashMap<>();
	
	@BeforeEach
	void before() {
		tables.put("111", "AAA");
		tables.put("222", "BBB");
		tables.put("333", "CCC");
	}
	
	String getById(String id) {
		return tables.get(id);
	}
	
	@Test
	void legacyStyle() {
		String name = getById("444");
		if (name != null) {
			log.info("name is {}", name);
			
		} else {
			log.info("no such id");
		}
	}
	
	Optional<String> getOptionalById(String id) {
		String name = tables.get(id);
		
		return Optional.ofNullable(name);
	}
	
	@Test
	void optionalStyle() {
		Optional<String> name = getOptionalById("444");
		if (name.isPresent()) {
			log.info("name is {}", name.get());
			
		} else {
			log.info("no such id");
		}
	}
	
	@Test
	void optionalStyle2() {
		Optional<String> name = getOptionalById("111");
		name.ifPresent(n -> {
			log.info("name is {}", n);
		});
	}
	
	@Test
	void legacyStyleError() {
		int length = tables.get("444").length();
		
		log.info("length is {}", length);
	}
	
	@Test
	void optionalStyleError() {
		int length = getOptionalById("444").get().length();
		
		log.info("length is {}", length);
	}
	
	@Test
	void transform() {
		Optional<String> name = getOptionalById("111");		
		Optional<Integer> length = name.map(String::length);
		if (length.isPresent()) {
			log.info("length is {}", length.get());
		}
	}
	
	@Test
	void filter() {
		Optional<String> name = getOptionalById("111");		
		boolean enough = name
				.map(String::length)
				.filter(len -> len > 0)
				.filter(len -> len <= 3)
				.isPresent();
		
		log.info("length is enough - {}", enough);
	}
	
	@Test
	void filterEmpty() {
		Optional<String> name = getOptionalById("444");		
		boolean enough = name
				.map(String::length)
				.filter(len -> len > 0)
				.filter(len -> len <= 3)
				.isPresent();
		
		log.info("length is enough - {}", enough);
	}
	
}
