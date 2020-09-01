package org.hum.wiretiger.proxy.pipe.bean;

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
	private List<HttpRequest> requestList = new ArrayList<>();
	// responseList
	private List<FullHttpResponse> responseList = new ArrayList<>();
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
		this.requestList.add(request);
	}

	void addResponse(FullHttpResponse response) {
		this.responseList.add(response);
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
}
