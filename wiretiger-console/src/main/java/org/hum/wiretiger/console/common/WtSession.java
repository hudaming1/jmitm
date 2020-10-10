package org.hum.wiretiger.console.common;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

@Getter
public class WtSession {
	
	private static final AtomicInteger IdCreator = new AtomicInteger(0);
	
	private long id;
	private Long pipeId;
	private FullHttpRequest request;
	private long requestTime;
	private FullHttpResponse response;
	private byte[] responseBytes;
	private long responseTime;
	
	public WtSession(Long pipeId, FullHttpRequest request, long requestTime) {
		this.id = IdCreator.incrementAndGet();
		this.pipeId = pipeId;
		this.request = request;
		this.requestTime = requestTime;
	}
	
	public void setResponse(FullHttpResponse response, byte[] responseBytes, long time) {
		this.response = response;
		this.responseBytes = responseBytes;
		this.responseTime = time;
	}
}
