package com.cht.iot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCacheTest {

	long ttl = 1;
	TimeUnit unit = TimeUnit.MILLISECONDS;
	
	@Test
	void scavengeButFailed() throws Exception {
		com.cht.iot.lab5.MyCache<String, Integer> cache = new com.cht.iot.lab5.MyCache<>();
		
		new Thread(() -> {			
			for (int i = 0;;i++) {
				String key = String.format("%09d", i);
				Integer value = i;
				
				cache.put(key, value, ttl, unit);
			}
			
		}).start();
		
		Thread.sleep(1000L);

		long ctm = System.currentTimeMillis();
		
		int count = cache.scavenge();
		
		log.info("Scavenge {} entries in {} ms", count, System.currentTimeMillis() - ctm);
	}
	
	@Test
	void scavengeWithConcurrentMap() throws Exception {
		com.cht.iot.lab6.MyCache<String, Integer> cache = new com.cht.iot.lab6.MyCache<>();
		
		new Thread(() -> {			
			for (int i = 0;;i++) {
				String key = String.format("%09d", i);
				Integer value = i;
				
				cache.put(key, value, ttl, unit);
			}
			
		}).start();
		
		Thread.sleep(1000L);

		long ctm = System.currentTimeMillis();
		
		int count = cache.scavenge();
		
		log.info("Scavenge {} entries in {} ms", count, System.currentTimeMillis() - ctm);
	}	
	
	@Test
	void scavengeParallelism() throws Exception {
		com.cht.iot.lab7.MyCache<String, Integer> cache = new com.cht.iot.lab7.MyCache<>();
		
		new Thread(() -> {			
			for (int i = 0;;i++) {
				String key = String.format("%09d", i);
				Integer value = i;
				
				cache.put(key, value, ttl, unit);
			}
			
		}).start();
		
		Thread.sleep(1000L);

		long ctm = System.currentTimeMillis();
		
		int count = cache.scavenge();
		
		log.info("Scavenge {} entries in {} ms", count, System.currentTimeMillis() - ctm);
	}
	
	@Test
	void scavengeBySize() throws Exception {
		int maxEntries = 1_000_000;
		
		com.cht.iot.lab8.MyCache<String, Integer> cache = new com.cht.iot.lab8.MyCache<>(maxEntries);
		
		ttl = Long.MAX_VALUE;
		
		new Thread(() -> {			
			for (int i = 0;;i++) {
				String key = String.format("%09d", i);
				Integer value = i;
				
				cache.put(key, value, ttl, unit);
			}
			
		}).start();
		
		Thread.sleep(1000L);

		long ctm = System.currentTimeMillis();
		
		int count = cache.scavenge();
		
		log.info("Scavenge {} entries in {} ms", count, System.currentTimeMillis() - ctm);
	}
	
	Integer getOrLoad(com.cht.iot.lab8.MyCache<String, Integer> cache, String key) {
		Integer value = cache.get(key);
		if (value == null) {
			value = expensiveLoad(); // expensive
			cache.put(key, value, ttl, unit);
		}
		
		return value;
	}
	
	Integer expensiveLoad() {
		return (int) (Math.random() * 1000);
	}
	
	@Test
	void loadIfNeed() throws Exception {
		int maxEntries = 1_000_000;
		
		com.cht.iot.lab9.MyCache<String, String> cache = new com.cht.iot.lab9.MyCache<>(maxEntries);
//		com.cht.iot.laba.MyCache<String, String> cache = new com.cht.iot.laba.MyCache<>(maxEntries);
		
		ttl = Long.MAX_VALUE;
		
		int threads = 1000;
		CountDownLatch latch = new CountDownLatch(threads);
		
		for (int i = 0;i < threads;i++) {
			new Thread(() -> {				
				String name = Thread.currentThread().getName();
				
				String key = "cht";
				String value = cache.get(key, k -> name, ttl, unit);
				
				log.info("{} got {}", name, value);
				
				latch.countDown();
				
			}).start();
		}
	}
}
