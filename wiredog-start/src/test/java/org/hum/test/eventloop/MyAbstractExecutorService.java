package org.hum.test.eventloop;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyAbstractExecutorService extends AbstractExecutorService {
	
	private final AtomicBoolean startFlag = new AtomicBoolean(false);
	private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
	private volatile Thread Thread;
	private volatile boolean isTerminate = false;

	@Override
	public void shutdown() {
		isTerminate = true;
	}

	@Override
	public List<Runnable> shutdownNow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isShutdown() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(Runnable command) {
		taskQueue.add(command);
		if (startFlag.get() == false && startFlag.compareAndSet(false, true)) {
			this.Thread = new Thread(new TaskQueueConsumer(taskQueue));
			this.Thread.setDaemon(true);
			this.Thread.start();
		}
	}

	private class TaskQueueConsumer implements Runnable {
		
		private BlockingQueue<Runnable> taskQueue;
		
		public TaskQueueConsumer(BlockingQueue<Runnable> taskQueue) {
			this.taskQueue = taskQueue;
		}

		@Override
		public void run() {
			while (isTerminate == false) {
				Runnable poll;
				try {
					poll = taskQueue.take();
					poll.run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Thread exited");
		}
		
	}
}
