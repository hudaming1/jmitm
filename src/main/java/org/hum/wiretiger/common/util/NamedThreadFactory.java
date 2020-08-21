package org.hum.wiretiger.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	
	private final AtomicInteger SEQ = new AtomicInteger(0);
	private String name;
	
	public NamedThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, name + "-" + SEQ.incrementAndGet());
	}
}
