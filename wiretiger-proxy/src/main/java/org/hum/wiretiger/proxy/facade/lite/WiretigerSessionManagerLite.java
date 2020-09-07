package org.hum.wiretiger.proxy.facade.lite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.proxy.session.WtSessionManager;
import org.hum.wiretiger.proxy.session.bean.WtSession;

import io.netty.handler.codec.http.HttpHeaders;

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
		WtSession wtSession = WtSessionManager.get().getById(id);
		if (wtSession == null) {
			return null;
		}
		
		WiretigerFullSession fullSession = new WiretigerFullSession();
		fullSession.setPipeId(wtSession.getPipeId());
		fullSession.setMethod(wtSession.getRequest().method().name());
		fullSession.setProtocol(wtSession.getRequest().protocolVersion().protocolName());
		fullSession.setUri(wtSession.getRequest().uri());
		fullSession.setRequestHeaders(header2Map(wtSession.getRequest().headers()));
		// TODO RequestBody 暂未解析
		fullSession.setRequestBody(null);
		if (wtSession.getResponse() != null) {
			fullSession.setResponseHeaders(header2Map(wtSession.getResponse().headers()));
			fullSession.setResponseBody(wtSession.getResponseBytes());
		}
		return fullSession;
	}
	
	private Map<String, String> header2Map(HttpHeaders headers) {
		if (headers == null || headers.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> headerMap = new HashMap<>();
		headers.forEach(header -> {
			headerMap.put(header.getKey(), header.getValue());
		});
		return headerMap;
	}

	public Collection<WiretigerSimpleSession> getAll() {
		List<WiretigerSimpleSession> list = new ArrayList<>();
		for (WtSession wtSession : WtSessionManager.get().getAll()) {
			WiretigerSimpleSession simpleSession = new WiretigerSimpleSession();
			simpleSession.setPipeId(wtSession.getPipeId());
			simpleSession.setSessionId(wtSession.getId());
			simpleSession.setUri(wtSession.getRequest().uri());
			simpleSession.setResponseCode(wtSession.getResponse().status().code() + "");
			list.add(simpleSession);
		}
		return list;
	}
}
