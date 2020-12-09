package com.cht.iot;

import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JCacheTest {

	@Test
	void test() throws Exception {
		CachingProvider provider = Caching.getCachingProvider();
		
		log.info("CachingProvider: {}", provider);
		
		CacheManager cm = provider.getCacheManager();
		
		MutableConfiguration<String, Integer> conf = new MutableConfiguration<>();
		conf.setTypes(String.class, Integer.class);
		conf.setStoreByValue(false);
		conf.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 1L)));		
		
		Cache<String, Integer> cache = cm.createCache("MyCache", conf);
		
		cache.put("aaa", 111);
		
		log.info("aaa = {}", cache.get("aaa"));
		
		Thread.sleep(2_000L);
		
		log.info("aaa = {}", cache.get("aaa"));
	}
}
