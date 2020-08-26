package org.hum.wiretiger.proxy.session;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.proxy.session.bean.WtSession;

public class WtSessionManager {

	private List<WtSession> RequestList = new CopyOnWriteArrayList<>();
	private Map<Long, WtSession> RequestIndex4Id = new ConcurrentHashMap<>();
	
	private static class WtSessionManagerHolder {
		public static WtSessionManager connectionManager = new WtSessionManager();
	}
	
	private WtSessionManager() {
	}
	
	public static WtSessionManager get() {
		return WtSessionManagerHolder.connectionManager;
	}
	
	public void add(WtSession connection) {
		RequestList.add(connection);
		RequestIndex4Id.put(connection.getId(), connection);
	}
	
	public List<WtSession> getList() {
		return Collections.unmodifiableList(this.RequestList);
	}
	
	public WtSession getRequest(long id) {
		return RequestIndex4Id.get(id);
	}
}
