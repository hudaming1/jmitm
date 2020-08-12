package org.hum.wiretiger.core.connection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.connection.bean.WiretigerConnection;

public class ConnectionManager {

	private List<WiretigerConnection> ConnectionList = new CopyOnWriteArrayList<>();
	private Map<Long, WiretigerConnection> ConnectionIndex4Id = new ConcurrentHashMap<>();
	
	private static class ConnectionManagerHolder {
		public static ConnectionManager connectionManager = new ConnectionManager();
	}
	
	private ConnectionManager() {
	}
	
	public static ConnectionManager get() {
		return ConnectionManagerHolder.connectionManager;
	}
	
	public void add(WiretigerConnection connection) {
		ConnectionList.add(connection);
		ConnectionIndex4Id.put(connection.getId(), connection);
	}
	
	public List<WiretigerConnection> getLis() {
		return Collections.unmodifiableList(this.ConnectionList);
	}
	
	public WiretigerConnection getConnection(long id) {
		return ConnectionIndex4Id.get(id);
	}
}
