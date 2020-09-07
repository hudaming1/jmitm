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

	public void fireConnectEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onConnect(convert(context));
		}
	}

	@Deprecated
	public void fireReadEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(context));
		}
	}

	@Deprecated
	public void fireReceiveEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(context));
		}
	}

	@Deprecated
	public void fireForwardEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(context));
		}
	}

	@Deprecated
	public void fireFlushEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(context));
		}
	}
	
	public void fireChangeEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onPipeStatusChange(convert(context));
		}
	}

	public void fireDisconnectEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onDisconnect(convert(context));
		}
	}

	public void fireErrorEvent(WtPipeContext context) {
		for (EventListener listener : listeners) {
			listener.onError(convert(context));
		}
	}

	public void fireNewSessionEvent(WtPipeContext context, WtSession session) {
		for (EventListener listener : listeners) {
			listener.onNewSession(convert(context), convert(session));
		}
	}
	
	private WiretigerPipe convert(WtPipeContext context) {
		WiretigerPipe pipeVo = new WiretigerPipe();
		InetSocketAddress source = (InetSocketAddress) context.getClientChannel().remoteAddress();
		pipeVo.setSourceHost(source.getHostName());
		pipeVo.setSourcePort(source.getPort());
		pipeVo.setProtocol(context.getProtocol());
		pipeVo.setPipeId(context.getId() + "");
		pipeVo.setStatus(WiretigerPipeStatus.getEnum(context.getCurrentStatus().getCode()));
		
		if (context.getServerChannel() != null) {
			InetSocketAddress target = (InetSocketAddress) context.getServerChannel().remoteAddress();
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
