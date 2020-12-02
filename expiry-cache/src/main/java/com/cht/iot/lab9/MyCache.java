package com.cht.iot.lab9;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyCache<K, V> {
	final int maxEntries;
	
	Map<K, Entry<K, V>> cache = new ConcurrentHashMap<>();
	
	Map<K, Object> locks = new ConcurrentHashMap<>();
	
	public MyCache(int maxEntries) {
		this.maxEntries = maxEntries;
	}

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
	
	synchronized Object lock(K key) {
		Object lck = locks.get(key);
		if (lck == null) {
			lck = new Object();
			locks.put(key, lck);
		}
		
		return lck;
	}
	
	synchronized void unlock(K key) {
		locks.remove(key);
	}
	
	public V get(K key, ValueLoader<K, V> loader, long ttl, TimeUnit unit) {
		Object lck = lock(key);
		try {
			synchronized (lck) {
				V value = get(key);
				if (value == null) {
					value = loader.load(key);
					put(key, value, ttl, unit); // HINT - you can put null value
				}
				
				return value;
			}
			
		} finally {
			unlock(key);
		}
	}
	
	public int scavenge() {
		AtomicInteger count = new AtomicInteger();
		
		cache.values().parallelStream()
			.filter(e -> e.isExpired())
			.forEach(e -> {
				cache.remove(e.key);
				count.incrementAndGet();
			});
		
		// TODO - to support LRU, LFU, FIFO ...
		for (K key : cache.keySet()) {
			if (cache.size() <= maxEntries) {
				break;
			}
			
			cache.remove(key);			
			count.incrementAndGet();
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
	
	@FunctionalInterface
	public static interface ValueLoader<K, V> {
		V load(K key);
	}
}
