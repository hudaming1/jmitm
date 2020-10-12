package org.hum.wiretiger.common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	private final int capacity;
    private final Lock lock = new ReentrantLock();

	public SyncLinkedHashMap(int capacity) {
		this.capacity = capacity;
	}

	public V get(Object key) {
		lock.lock();
		try {
			return super.get(key);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public V put(K key, V value) {
		// 如果容量设置为0，就不要保存了
		if (capacity == 0) {
			return value;
		}
		lock.lock();
		try {
			return super.put(key, value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public V remove(Object key) {
		lock.lock();
		try {
			return super.remove(key);
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
	
	public static void main(String[] args) {
		SyncLinkedHashMap<String, Object> map = new SyncLinkedHashMap<String, Object>(0);
		for (int i = 1 ;i <= 100 ;i ++) {
			map.put("aaa" + i, i);
		}
		System.out.println(map);
		System.out.println(map.get("aaa91"));
		System.out.println(map.get("aaa90"));
	}
}
