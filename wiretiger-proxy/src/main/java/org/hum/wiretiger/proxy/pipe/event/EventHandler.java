package org.hum.wiretiger.proxy.pipe.event;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.proxy.facade.event.EventListener;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;
import org.hum.wiretiger.proxy.facade.event.WiretigerSession;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.session.bean.WtSession;

public class EventHandler {

	private List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();

	public void add(EventListener listener) {
		this.listeners.add(listener);
	}
	
	public void addAll(List<EventListener> listeners) {
		this.listeners.addAll(listeners);
	}

	public void fireConnectEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onConnect(convert(pipe));
		}
	}

	public void fireReadEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireReceiveEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireForwardEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireFlushEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(pipe));
		}
	}

	public void fireDisconnectEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onDisconnect(convert(pipe));
		}
	}

	public void fireErrorEvent(WtPipeContext pipe) {
		for (EventListener listener : listeners) {
			listener.onError(convert(pipe));
		}
	}

	public void fireNewSessionEvent(WtPipeContext pipe, WtSession session) {
		for (EventListener listener : listeners) {
			listener.onNewSession(convert(pipe), convert(session));
		}
	}
	
	private WiretigerPipe convert(WtPipeContext holder) {
		WiretigerPipe pipeVo = new WiretigerPipe();
		InetSocketAddress source = (InetSocketAddress) holder.getClientChannel().remoteAddress();
		pipeVo.setSourceHost(source.getHostName());
		pipeVo.setSourcePort(source.getPort());
		pipeVo.setProtocol(holder.getProtocol());
		pipeVo.setPipeId(holder.getId() + "");
		pipeVo.setStatus(WiretigerPipeStatus.getEnum(holder.getCurrentStatus().getCode()));
		
		if (holder.getServerChannel() != null) {
			InetSocketAddress target = (InetSocketAddress) holder.getServerChannel().remoteAddress();
			pipeVo.setTargetHost(target.getHostName());
			pipeVo.setTargetPort(target.getPort());
		}
		return pipeVo;
	}
	
	private WiretigerSession convert(WtSession wtSession) {
		WiretigerSession session = new WiretigerSession();
		session.setSessionId(wtSession.getId() + "");
		session.setResponseCode(wtSession.getResponse().status().code() + "");
		session.setUri(wtSession.getRequest().uri());
		return session;
	}
}
