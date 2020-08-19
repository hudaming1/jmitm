package org.hum.wiretiger.core.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.request.bean.WtRequest;

public class RequestManager {

	private List<WtRequest> ConnectionList = new CopyOnWriteArrayList<>();
	private Map<Long, WtRequest> ConnectionIndex4Id = new ConcurrentHashMap<>();
	
	private static class ConnectionManagerHolder {
		public static RequestManager connectionManager = new RequestManager();
	}
	
	private RequestManager() {
	}
	
	public static RequestManager get() {
		return ConnectionManagerHolder.connectionManager;
	}
	
	public void add(WtRequest connection) {
		ConnectionList.add(connection);
		ConnectionIndex4Id.put(connection.getId(), connection);
	}
	
	public List<WtRequest> getLis() {
		return Collections.unmodifiableList(this.ConnectionList);
	}
	
	public WtRequest getConnection(long id) {
		return ConnectionIndex4Id.get(id);
	}
}
