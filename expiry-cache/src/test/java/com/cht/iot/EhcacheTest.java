package com.cht.iot;

import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.DefaultStatisticsService;
import org.ehcache.core.statistics.TierStatistics;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EhcacheTest {

	@Test
	void test() throws Exception {
		StatisticsService stats = new DefaultStatisticsService();
		
		PersistentCacheManager manager = CacheManagerBuilder.newCacheManagerBuilder() 
				  .with(CacheManagerBuilder.persistence(new File("/tmp/cache")))
				  .using(stats)
				  .build();
		
		manager.init();
		
		Cache<String, byte[]> cache = manager.createCache("MyCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
				String.class, byte[].class,
				ResourcePoolsBuilder.newResourcePoolsBuilder()					
					.heap(10, MemoryUnit.KB)
					.offheap(5, MemoryUnit.MB)			// -XX:MaxDirectMemorySize=20M
					.disk(10, MemoryUnit.MB, true)
					.build()
				));

		for (int i = 0;i < 1000;i++) {
			String key = String.format("%06d", i);
			byte[] value = new byte[1024];
			
			cache.put(key, value);
		}
		
		cache.get("000000"); cache.get("000001"); cache.get("000002");
		
		CacheStatistics s = stats.getCacheStatistics("MyCache");
		
		s.getTierStatistics().entrySet().stream()
			.forEach(e -> {
				String tier = e.getKey();
				TierStatistics ts = e.getValue();				
				
				log.info("{} - entries: {}, size: {} bytes", tier, ts.getMappings(), ts.getAllocatedByteSize());
			});		
	}
	
	@Test
	void writeThrough() throws Exception {
		CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
		
		Cache<String, Integer> cache = manager.createCache("MyCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
				String.class, Integer.class,				
				ResourcePoolsBuilder.newResourcePoolsBuilder()					
					.heap(100, EntryUnit.ENTRIES)
					.build()					
				)
				.withLoaderWriter(new CacheLoaderWriter<String, Integer>() {

					Map<String, Integer> database = new ConcurrentHashMap<>();
					
					@Override
					public Integer load(String key) throws Exception {
						log.info("Load record from database for key: {}", key);
						
						return key.hashCode();
					}

					@Override
					public void write(String key, Integer value) throws Exception {
						log.info("Save record into database for key: {}, value: {}", key, value);
						
						database.put(key, value);						
					}

					@Override
					public void delete(String key) throws Exception {
						log.info("Delete record from database for key: {}", key);
						
						database.remove(key);						
					}					
				}));
		
		cache.get("aaa");
		cache.get("aaa"); // load from cache
		
		cache.put("bbb", 222);		
	}
	
	@Test
	void benchmark() throws Exception {
		StatisticsService stats = new DefaultStatisticsService();
		
		CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder()
				.using(stats)
				.build();
		
		manager.init();
		
		Cache<String, Integer> cache = manager.createCache("MyCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
				String.class, Integer.class,
				ResourcePoolsBuilder.newResourcePoolsBuilder()					
					.heap(2_000_000, EntryUnit.ENTRIES)
					.build()					
				)
				.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1))));
		
		new Thread(() -> {			
			for (int i = 0;;i++) {
				String key = String.format("%09d", i);
				Integer value = i;
				
				cache.put(key, value);
			}
			
		}).start();
		
		Thread.sleep(1000L);
		
		long ctm = System.currentTimeMillis();
		
		cache.forEach(e -> {}); // WTF
		
		log.info("Scavenge {} entries in {} ms",
				stats.getCacheStatistics("MyCache").getCacheExpirations(),
				System.currentTimeMillis() - ctm);		
	}
}
