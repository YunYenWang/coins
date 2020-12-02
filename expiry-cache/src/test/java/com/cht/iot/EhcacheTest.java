package com.cht.iot;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EhcacheTest {

	@Test
	void test() throws Exception {
		CachingProvider provider = Caching.getCachingProvider();
		CacheManager cm = provider.getCacheManager();
		MutableConfiguration<Long, String> conf = new MutableConfiguration<>();
		conf.setTypes(Long.class, String.class);
		conf.setStoreByValue(false);
		conf.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 1L)));
		
		ExpiredListener listener = new ExpiredListener();		
		
		CacheEntryListenerConfiguration<Long, String> econf = new MutableCacheEntryListenerConfiguration<>(
				FactoryBuilder.factoryOf(listener),
				null,
				true,	// isOldValueRequired
				true); // isSynchronous
		
		conf.addCacheEntryListenerConfiguration(econf);
		
		Cache<Long, String> cache = cm.createCache("default", conf);
		
		cache.put(1L, "one");
		
		log.info("{}", cache.get(1L));
		
		Thread.sleep(2_000L);
		
		
		
		
		log.info("before getting value");
		
		log.info("{}", cache.get(1L));
	}
	
	class ExpiredListener implements CacheEntryExpiredListener<Long, String>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void onExpired(Iterable<CacheEntryEvent<? extends Long, ? extends String>> events) throws CacheEntryListenerException {
			for (CacheEntryEvent<? extends Long, ? extends String> e : events) {
				log.info("Expiry key - {}", e.getKey());;
			}
		}
	}
}
