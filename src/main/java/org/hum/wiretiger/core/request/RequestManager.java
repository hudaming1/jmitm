package org.hum.wiretiger.core.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hum.wiretiger.core.request.bean.WtRequest;

public class RequestManager {

	private List<WtRequest> RequestList = new CopyOnWriteArrayList<>();
	private Map<Long, WtRequest> RequestIndex4Id = new ConcurrentHashMap<>();
	
	private static class ConnectionManagerHolder {
		public static RequestManager connectionManager = new RequestManager();
	}
	
	private RequestManager() {
	}
	
	public static RequestManager get() {
		return ConnectionManagerHolder.connectionManager;
	}
	
	public void add(WtRequest connection) {
		RequestList.add(connection);
		RequestIndex4Id.put(connection.getId(), connection);
	}
	
	public List<WtRequest> getList() {
		return Collections.unmodifiableList(this.RequestList);
	}
	
	public WtRequest getRequest(long id) {
		return RequestIndex4Id.get(id);
	}
}
