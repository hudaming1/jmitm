package org.hum.test.eventloop;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

public class MyAbstractEventExecutor extends MyAbstractExecutorService implements EventExecutor {

	@Override
	public boolean isShuttingDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Future<?> shutdownGracefully() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> terminationFuture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<EventExecutor> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<?> submit(Runnable task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventExecutor next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventExecutorGroup parent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean inEventLoop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inEventLoop(java.lang.Thread thread) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <V> Promise<V> newPromise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> ProgressivePromise<V> newProgressivePromise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> Future<V> newSucceededFuture(V result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> Future<V> newFailedFuture(Throwable cause) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }
}
