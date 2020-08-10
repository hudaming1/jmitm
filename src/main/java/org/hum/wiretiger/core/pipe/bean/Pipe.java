package org.hum.wiretiger.core.pipe.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

@Getter
public class Pipe {
	
	// client->proxy
	private Channel sourceCtx;
	// proxy->server
	private Channel targetCtx;
	// request
	private List<DefaultHttpRequest> requestList = new ArrayList<>();
	// responseList
	private List<FullHttpResponse> responseList = new ArrayList<>();
	// pipe status
	private Map<Long, PipeStatus> statusMap = new HashMap<>();
	// create time
	private long birthday;
	// pipeId
	private int id;
	// protocol
	private Protocol protocol;
	
	public void addStatus(PipeStatus status) {
		this.statusMap.put(System.currentTimeMillis(), status);
	}

	void setSourceCtx(Channel sourceCtx) {
		this.sourceCtx = sourceCtx;
	}

	void setTargetCtx(Channel targetCtx) {
		this.targetCtx = targetCtx;
	}

	void addRequest(DefaultHttpRequest request) {
		this.requestList.add(request);
	}

	void addResponse(FullHttpResponse response) {
		this.responseList.add(response);
	}

	void setStatusMap(Map<Long, PipeStatus> statusMap) {
		this.statusMap = statusMap;
	}

	void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	void setId(int id) {
		this.id = id;
	}

	void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
}
