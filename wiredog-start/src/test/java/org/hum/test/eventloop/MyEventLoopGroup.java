package org.hum.test.eventloop;

import java.util.concurrent.Executor;

import io.netty.channel.EventLoop;
import io.netty.channel.MultithreadEventLoopGroup;

public class MyEventLoopGroup extends MultithreadEventLoopGroup {
	
	protected MyEventLoopGroup(int nThreads) {
		super(nThreads, (Executor) null);
	}

	@Override
	protected EventLoop newChild(Executor executor, Object... args) throws Exception {
		return new MyEventLoop(this, executor, true);
	}
}
