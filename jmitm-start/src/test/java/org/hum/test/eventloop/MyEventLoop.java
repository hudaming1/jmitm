package org.hum.test.eventloop;

import java.util.concurrent.Executor;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;

public class MyEventLoop extends SingleThreadEventLoop {

	protected MyEventLoop(EventLoopGroup parent, Executor executor, boolean addTaskWakesUp) {
		super(parent, executor, addTaskWakesUp);
	}

	@Override
	protected void run() {
		Runnable task = pollTask();
		super.execute(task);
	}
}
