package org.hum.wiretiger.proxy.facade.lite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.proxy.session.WtSessionManager;
import org.hum.wiretiger.proxy.session.bean.WtSession;

public class WiretigerSessionManagerLite {

	private static class WiretigerSessionManagerLiteHolder {
		private static WiretigerSessionManagerLite instance = new WiretigerSessionManagerLite();
	}

	public static WiretigerSessionManagerLite get() {
		return WiretigerSessionManagerLiteHolder.instance;
	}
	
	private WiretigerSessionManagerLite() {
	}
	

	public WiretigerFullSession getById(Long id) {
		return parse2WiretigerFullSession(WtSessionManager.get().getById(id));
	}

	public Collection<WiretigerFullSession> getAll() {
		List<WiretigerFullSession> list = new ArrayList<>();
		for (WtSession holder : WtSessionManager.get().getAll()) {
			list.add(parse2WiretigerFullSession(holder));
		}
		return list;
	}

	private WiretigerFullSession parse2WiretigerFullSession(WtSession wtSession) {
		if (wtSession == null) {
			return null;
		}
		WiretigerFullSession fullSession = new WiretigerFullSession();
		fullSession.setSessionId(wtSession.getId());
		fullSession.setUri(wtSession.getRequest().uri());
		fullSession.setResponseCode(wtSession.getResponse().status().toString());
		return fullSession;
	}
}
