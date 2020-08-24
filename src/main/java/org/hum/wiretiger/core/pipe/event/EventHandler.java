package org.hum.wiretiger.core.pipe.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class EventHandler {
	
	private List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();
	
	public void add(EventListener listener) {
		this.listeners.add(listener);
	}

	public void fireConnectEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onConnect(pipe);
		}
	}
	
	
	public void fireReadEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(pipe);
		}
	}
	
	
	public void fireReceiveEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(pipe);
		}
	}
	
	
	public void fireForwardEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(pipe);
		}
	}
	
	
	public void fireFlushEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(pipe);
		}
	}
	
	
	public void fireDisconnectEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onDisconnect(pipe);
		}
	}

	public void fireErrorEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onError(pipe);
		}
	}
	
	public void fireNewSessionEvent(PipeHolder pipeHolder, DefaultHttpRequest sessionReq) {
		for (EventListener listener : listeners) {
			listener.onNewSession(pipeHolder, sessionReq);
		}
	}
	
	public void fireSessionChangeEvent(PipeHolder pipeHolder, FullHttpResponse sessionResp) {
		for (EventListener listener : listeners) {
			listener.onSessionUpdate(pipeHolder, sessionResp);
		}
	}
}
