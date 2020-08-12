package org.hum.wiretiger.core.connection;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.connection.bean.WiretigerConnection;

public class ConnectionManager {

	private List<WiretigerConnection> ConnectionList = new CopyOnWriteArrayList<>();
	
	private static class ConnectionManagerHolder {
		public static ConnectionManager connectionManager = new ConnectionManager();
	}
	
	public static ConnectionManager get() {
		return ConnectionManagerHolder.connectionManager;
	}
	
	public void add(WiretigerConnection connection) {
		ConnectionList.add(connection);
	}
	
	public List<WiretigerConnection> getLis() {
		return Collections.unmodifiableList(this.ConnectionList);
	}
}
