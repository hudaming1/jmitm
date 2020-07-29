package org.hum.wiretiger.test;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Client {

	private Map<Integer, Future<String>> cache = new ConcurrentHashMap<>(); // 1

	public static void main(String[] args) {
		Client client = new Client();
		int id = 1234;
		// 启动五个线程并发执行
		for (int i = 0; i < 5; i++) {
			new Thread(() -> System.out.println(client.getUserById(id))).start();
		}
	}

	// 使用FutureTask的特性，如果已有查询任务在执行，其他线程可以
	// 获取这个任务，并等待其返回结果
	private String getUserById(Integer id) {
		Future<String> future = cache.get(id);
		if (future == null) {
			FutureTask<String> task = new FutureTask<>(new Callable<String>() {
				@Override
				public String call() throws Exception {
					System.out.println("1111");
					return "user" + id;
				}
			});
			future = cache.putIfAbsent(id, task); // 2
			if (future == null) {
				future = task;
				task.run();
			}
		}

		try {
			return future.get();
		} catch (Exception e) {
			cache.remove(id, future); // 3
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}