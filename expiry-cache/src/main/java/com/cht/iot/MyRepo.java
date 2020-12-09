package com.cht.iot;

import javax.cache.annotation.CachePut;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyRepo {

	@Cacheable(cacheNames = "age")
	public Integer getMyAge(String name) {
		int age = (int) (Math.random() * 100);
		
		log.info("Make a fake age {} for {}", age, name);
		
		return age;
	}
	
	@CachePut(cacheName = "age")
	public Integer newMyAge(String name) {
		int age = (int) (Math.random() * 100);
		
		log.info("Re-new a fake age {} for {}", age, name);
		
		return age;
	}
	
	@CacheEvict(cacheNames = "age")
	public void cleanMyAge(String name) {		
		log.info("Clean my age for {}", name);
	}
	
}
