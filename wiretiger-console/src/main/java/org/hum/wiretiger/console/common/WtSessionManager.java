package org.hum.wiretiger.console.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
	
	public List<WtSession> getAll() {
		return Collections.unmodifiableList(this.RequestList);
	}
	
	public WtSession getById(long id) {
		return RequestIndex4Id.get(id);
	}
}
