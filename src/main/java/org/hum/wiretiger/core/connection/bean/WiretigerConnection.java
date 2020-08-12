package org.hum.wiretiger.core.connection.bean;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

@Getter
public class WiretigerConnection {

	private DefaultHttpRequest request;
	private long requestTime;
	private FullHttpResponse response;
	private long responseTime;
	
	public WiretigerConnection(DefaultHttpRequest request, long requestTime) {
		this.request = request;
		this.requestTime = requestTime;
	}
	
	public void setResponse(FullHttpResponse response, long time) {
		this.response = response;
		this.responseTime = time;
	}
}
