package org.hum.wiretiger.proxy.facade.lite;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipeEvent;
import org.hum.wiretiger.proxy.pipe.WtPipeManager;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;

public class WiretigerPipeManagerLite {

	private static class WiretigerPipeManagerLiteHolder {
		private static WiretigerPipeManagerLite instance = new WiretigerPipeManagerLite();
	}

	public static WiretigerPipeManagerLite get() {
		return WiretigerPipeManagerLiteHolder.instance;
	}
	
	private WiretigerPipeManagerLite() {
	}

	public WiretigerFullPipe getById(Integer id) {
		WtPipeHolder holder = WtPipeManager.get().getById(id);
		WiretigerFullPipe fullPipe = parse2WiretigerFullPipe(holder);
		fullPipe.setEvents(new ArrayList<>());
		holder.getEventList().forEach(event -> {
			WiretigerPipeEvent e = new WiretigerPipeEvent();
			e.setDesc(event.getDesc());
			e.setTime(event.getTime());
			e.setType(event.getType().getDesc());
			fullPipe.getEvents().add(e);
		});
		return fullPipe;
	}

	public Collection<WiretigerFullPipe> getAll() {
		List<WiretigerFullPipe> list = new ArrayList<>();
		for (WtPipeHolder holder : WtPipeManager.get().getAll()) {
			list.add(parse2WiretigerFullPipe(holder));
		}
		return list;
	}

	private WiretigerFullPipe parse2WiretigerFullPipe(WtPipeHolder holder) {
		if (holder == null) {
			return null;
		}
		InetSocketAddress source = (InetSocketAddress) holder.getClientChannel().remoteAddress();
		WiretigerFullPipe fullPipe = new WiretigerFullPipe();
		fullPipe.setPipeId(holder.getId() + "");
		fullPipe.setProtocol(holder.getProtocol());
		fullPipe.setSourceHost(source.getHostName());
		fullPipe.setSourcePort(source.getPort());
		if (holder.getServerChannel() != null) {
			InetSocketAddress target = (InetSocketAddress) holder.getServerChannel().remoteAddress();
			fullPipe.setTargetHost(target.getHostName());
			fullPipe.setTargetPort(target.getPort());
		}
		fullPipe.setStatus(WiretigerPipeStatus.getEnum(holder.getCurrentStatus().getCode()));
		return fullPipe;
	}
}
