package com.cht.iot.lab5;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyCache<K, V> {
	Map<K, Entry<K, V>> cache = Collections.synchronizedMap(new HashMap<>());

	public void put(K key, V value, long ttl, TimeUnit unit) {
		long timeout = unit.toMillis(ttl); 
		
		Entry<K, V> e = new Entry<>(key, value, timeout);
		cache.put(key, e);		
	}
	
	public V get(K key) {
		Entry<K, V> e = cache.get(key);
		if (e == null) {
			return null;
		}
		
		if (e.isExpired()) {
			cache.remove(key);
			
			return null;
		}
		
		return e.value;
	}
	
	public int scavenge() {
		AtomicInteger count = new AtomicInteger();
		
		synchronized (cache) {	
			for (Entry<K, V> e : cache.values()) {
				if (e.isExpired()) {
					cache.remove(e.key);
					
					count.incrementAndGet();
				}
			}
		}
		
		return count.intValue();
	}
	
	static class Entry<K, V> {
		final K key;
		final V value;
		
		final long birthday;
		final long timeout;
		
		public Entry(K key, V value, long timeout) {
			this.key = key;
			this.value = value;
			this.birthday = System.currentTimeMillis();
			this.timeout = timeout;
		}
		
		public boolean isExpired() {
			long now = System.currentTimeMillis();
			
			return ((now - birthday) > timeout);
		}
	}
}
