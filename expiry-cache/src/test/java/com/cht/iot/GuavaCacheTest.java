package com.cht.iot;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuavaCacheTest {

	@Test
	void test() throws ExecutionException, InterruptedException {
		LoadingCache<String, Integer> cache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(1, TimeUnit.SECONDS)				
				.build(new CacheLoader<String, Integer>() {
					
					@Override
					public Integer load(String key) {
						return key.hashCode();
					}
				});
		
		log.info("aaa = {}", cache.get("aaa"));
		
		cache.put("bbb", 222);				
		log.info("bbb = {}", cache.get("bbb"));
		
		log.info("ccc = {}", cache.get("ccc", () -> {
			return 333;
		}));
		
		Thread.sleep(2_000L);
		log.info("After 2 seconds");
		
		log.info("aaa = {}", cache.get("aaa"));		
		log.info("bbb = {}", cache.get("bbb"));		
		log.info("ccc = {}", cache.get("ccc"));
	}
	
	@Test
	void referenceBasedEviction() throws InterruptedException {
		Cache<String, Integer> cache = CacheBuilder.newBuilder()
				.weakValues()
				.build();
		
		String key = "aaa";
		Integer value = new Integer(111);
		
		cache.put(key, value);
		
		log.info("aaa = {}", cache.getIfPresent(key));
		
		value = null;
		
		Runtime.getRuntime().gc();
		
		log.info("aaa = {}", cache.getIfPresent(key));		
	}	
	
	@Test
	void benchmark() throws InterruptedException {
		AtomicInteger count = new AtomicInteger();
		
		LoadingCache<String, Integer> cache = CacheBuilder.newBuilder()
				.expireAfterWrite(1, TimeUnit.MILLISECONDS)
				.recordStats()
				.removalListener(new RemovalListener<String, Integer>() {
					
					@Override
					public void onRemoval(RemovalNotification<String, Integer> notification) {						
						count.incrementAndGet();
					}
				})				
				.build(new CacheLoader<String, Integer>() {
					
					@Override
					public Integer load(String key) {
						return key.hashCode();
					}
				});
		
		new Thread(() -> {			
			for (int i = 0;;i++) {
				String key = String.format("%09d", i);
				Integer value = i;
				
				cache.put(key, value);
			}
			
		}).start();
		
		Thread.sleep(1000L);

		long ctm = System.currentTimeMillis();
		
		cache.cleanUp();
		
		log.info("Scavenge {} entries in {} ms", count, System.currentTimeMillis() - ctm);
	}
}
