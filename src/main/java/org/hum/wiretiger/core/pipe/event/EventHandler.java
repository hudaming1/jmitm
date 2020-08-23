package org.hum.wiretiger.core.pipe.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

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
		
	}
	
	
	public void fireReceiveEvent(PipeHolder pipe) {
		
	}
	
	
	public void fireForwardEvent(PipeHolder pipe) {
		
	}
	
	
	public void fireFlushEvent(PipeHolder pipe) {
		
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
}
