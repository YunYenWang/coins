package com.cht.iot;

import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SpringBootCachingTest {	
	
	@Autowired
	MyRepo repo;
	
	@Test
	void test() {
		log.info("Get my age");		
		repo.getMyAge("Rick");
		
		log.info("Get my age");
		repo.getMyAge("Rick");
		
		log.info("Get my age");
		repo.getMyAge("Rick");

//		hack();
	}	
	
	@Test
	void eviction() {
		log.info("Get my age");		
		repo.getMyAge("Rick");
		
		log.info("Get my age");
		repo.getMyAge("Rick");
		
		log.info("New my age");		
		repo.newMyAge("Rick");
		
		log.info("Clean my age");		
		repo.cleanMyAge("Rick");
		
		log.info("Get my age");		
		repo.getMyAge("Rick");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Autowired
	CacheManager manager;
	
	@Test
	void hack() {
		String cacheName = "age";
		
		Cache cache = manager.getCache(cacheName);
		
		@SuppressWarnings("unchecked")
		ConcurrentMap<Object, Object> map = (ConcurrentMap<Object, Object>) cache.getNativeCache();
		map.keySet().forEach(k -> {
			log.info("cahce: {}, key: {}", cacheName, k);			
		});
	}
}
