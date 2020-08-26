package org.hum.wiretiger.proxy.pipe.event;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.facade.proxy.EventListener;
import org.hum.wiretiger.facade.proxy.WiretigerPipe;
import org.hum.wiretiger.facade.proxy.WiretigerSession;
import org.hum.wiretiger.proxy.pipe.bean.PipeHolder;
import org.hum.wiretiger.proxy.session.bean.WtSession;

public class EventHandler {

	private List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();

	public void add(EventListener listener) {
		this.listeners.add(listener);
	}
	
	public void addAll(List<EventListener> listeners) {
		this.listeners.addAll(listeners);
	}

	public void fireConnectEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onConnect(convert(pipe));
		}
	}

	public void fireReadEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireReceiveEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireForwardEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireFlushEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireDisconnectEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onDisconnect(convert(pipe));
		}
	}

	public void fireErrorEvent(PipeHolder pipe) {
		for (EventListener listener : listeners) {
			listener.onError(convert(pipe));
		}
	}

	public void fireNewSessionEvent(PipeHolder pipe, WtSession session) {
		for (EventListener listener : listeners) {
			listener.onNewSession(convert(pipe), convert(session));
		}
	}
	
	private WiretigerPipe convert(PipeHolder holder) {
		WiretigerPipe pipeVo = new WiretigerPipe();
		InetSocketAddress source = (InetSocketAddress) holder.getClientChannel().remoteAddress();
		InetSocketAddress target = (InetSocketAddress) holder.getServerChannel().remoteAddress();
		pipeVo.setSourceHost(source.getHostName());
		pipeVo.setSourcePort(source.getPort());
		pipeVo.setTargetHost(target.getHostName());
		pipeVo.setTargetPort(target.getPort());
		pipeVo.setProtocol(holder.getProtocol());
		pipeVo.setPipeId(holder.getId() + "");
		
		return pipeVo;
	}
	
	private WiretigerSession convert(WtSession wtSession) {
		WiretigerSession session = new WiretigerSession();
		// TODO
		return session;
	}
}
