package org.hum.wiretiger.proxy.facade.lite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipeEvent;
import org.hum.wiretiger.proxy.pipe.WtPipeManager;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;

public class WiretigerPipeManagerLite {

	private static class WiretigerPipeManagerLiteHolder {
		private static WiretigerPipeManagerLite instance = new WiretigerPipeManagerLite();
	}

	public static WiretigerPipeManagerLite get() {
		return WiretigerPipeManagerLiteHolder.instance;
	}
	
	private WiretigerPipeManagerLite() {
	}

	public WiretigerFullPipe getById(Long id) {
		WtPipeContext wtcontext = WtPipeManager.get().getById(id);
		WiretigerFullPipe fullPipe = parse2WiretigerFullPipe(wtcontext);
		fullPipe.setEvents(new ArrayList<>());
		wtcontext.getEventList().forEach(event -> {
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
		for (WtPipeContext context : WtPipeManager.get().getAll()) {
			list.add(parse2WiretigerFullPipe(context));
		}
		return list;
	}

	private WiretigerFullPipe parse2WiretigerFullPipe(WtPipeContext wtContext) {
		if (wtContext == null) {
			return null;
		}
		WiretigerFullPipe fullPipe = new WiretigerFullPipe();
		fullPipe.setPipeId(wtContext.getId() + "");
		fullPipe.setPipeName(wtContext.getName());
		fullPipe.setProtocol(wtContext.getProtocol());
		fullPipe.setSourceHost(wtContext.getSourceHost());
		fullPipe.setSourcePort(wtContext.getSourcePort());
		fullPipe.setTargetHost(wtContext.getTargetHost());
		fullPipe.setTargetPort(wtContext.getTargetPort());
		fullPipe.setStatus(WiretigerPipeStatus.getEnum(wtContext.getCurrentStatus().getCode()));
		return fullPipe;
	}
}
