package org.hum.wiretiger.core.session;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.session.bean.WtSession;

public class SessionManager {

	private List<WtSession> RequestList = new CopyOnWriteArrayList<>();
	private Map<Long, WtSession> RequestIndex4Id = new ConcurrentHashMap<>();
	
	private static class ConnectionManagerHolder {
		public static SessionManager connectionManager = new SessionManager();
	}
	
	private SessionManager() {
	}
	
	public static SessionManager get() {
		return ConnectionManagerHolder.connectionManager;
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
