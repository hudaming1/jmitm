package org.hum.wiretiger.proxy.pipe.bean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Getter;

@Getter
public class WtPipe {
	
	// pipeId
	private Long id;
	// name
	private String name;
	// client->proxy
	private Channel sourceCtx;
	// proxy->server
	private Channel targetCtx;
	// request
	private SoftReference<List<HttpRequest>> requestList = new SoftReference<>(new ArrayList<HttpRequest>());
	// responseList
	private SoftReference<List<FullHttpResponse>> responseList = new SoftReference<>(new ArrayList<FullHttpResponse>());
	// pipe status
	private Map<Long, PipeStatus> statusMap = new HashMap<>();
	// event
	private List<WtPipeEvent> events = new ArrayList<>();
	// protocol
	private Protocol protocol;
	
	public void addEvent(WtPipeEvent event) {
		this.events.add(event);
	}
	
	public void addStatus(PipeStatus status) {
		this.statusMap.put(System.currentTimeMillis(), status);
	}

	void setSourceCtx(Channel sourceCtx) {
		this.sourceCtx = sourceCtx;
	}

	void setTargetCtx(Channel targetCtx) {
		this.targetCtx = targetCtx;
	}

	void addRequest(HttpRequest request) {
		this.requestList.get().add(request);
	}

	void addResponse(FullHttpResponse response) {
		this.responseList.get().add(response);
	}

	void setStatusMap(Map<Long, PipeStatus> statusMap) {
		this.statusMap = statusMap;
	}

	void setId(long id) {
		this.id = id;
	}

	void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	void setName(String name) {
		this.name = name;
	}

	public List<HttpRequest> getRequestList() {
		return this.requestList.get();
	}

	public List<FullHttpResponse> getResponseList() {
		return this.responseList.get();
	}
}
