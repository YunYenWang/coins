package com.cht.iot.lab3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyCache<K, V> {
	Map<K, V> cache = Collections.synchronizedMap(new HashMap<>());

	public void put(K key, V value) {
		cache.put(key, value);		
	}
	
	public V get(K key) {
		return cache.get(key);
	}
}
