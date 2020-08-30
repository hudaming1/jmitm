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
	

	public WiretigerSimpleSession getById(Long id) {
		return null;
	}

	public Collection<WiretigerSimpleSession> getAll() {
		List<WiretigerSimpleSession> list = new ArrayList<>();
		for (WtSession wtSession : WtSessionManager.get().getAll()) {
			WiretigerSimpleSession simpleSession = new WiretigerSimpleSession();
			simpleSession.setSessionId(wtSession.getId());
			simpleSession.setUri(wtSession.getRequest().uri());
			simpleSession.setResponseCode(wtSession.getResponse().status().toString());
			list.add(simpleSession);
		}
		return list;
	}
}
