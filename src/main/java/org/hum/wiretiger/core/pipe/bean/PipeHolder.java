package org.hum.wiretiger.core.pipe.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class PipeHolder {

	private Pipe pipe = new Pipe();
	
	public PipeHolder(int id) {
		pipe.setId(id);
		pipe.addStatus(PipeStatus.Init);
	}
	
	public void registClient(Channel channel) {
		this.pipe.setSourceCtx(channel);
		this.addEvent(PipeEventType.ClientConnect, "客户端上线");
	}
	
	public void registServer(Channel channel) {
		this.pipe.setTargetCtx(channel);
	}
	
	public void appendRequest(DefaultHttpRequest request) {
		this.pipe.addRequest(request);
	}
	
	public void appendResponse(FullHttpResponse response) {
		this.pipe.addResponse(response);
	}
	 
	public void recordStatus(PipeStatus status) {
		this.pipe.addStatus(status);
	}
	
	public int getId() {
		return pipe.getId();
	}
	
	public Channel getClientChannel() {
		return pipe.getSourceCtx();
	}
	
	public Channel getServerChannel() {
		return pipe.getTargetCtx();
	}
	
	public String getUri() {
		if (pipe.getRequestList() == null || pipe.getRequestList().isEmpty()) {
			return null;
		}
		return pipe.getRequestList().get(0).uri();
	}
	
	public void setProtocol(Protocol protocol) {
		pipe.setProtocol(protocol);
	}

	public Protocol getProtocol() {
		return pipe.getProtocol();
	}
	
	public List<DefaultHttpRequest> getRequests() {
		return Collections.unmodifiableList(pipe.getRequestList());
	}
	
	public List<FullHttpResponse> getResponses() {
		return Collections.unmodifiableList(pipe.getResponseList());
	}
	
	public Map<Long, PipeStatus> getStatusTimeline() {
		return Collections.unmodifiableMap(pipe.getStatusMap());
	}
	
	public void addEvent(PipeEventType type, String desc) {
		this.pipe.addEvent(new PipeEvent(type, desc, System.currentTimeMillis()));
	}
	
	public List<PipeEvent> getEventList() {
		return Collections.unmodifiableList(this.pipe.getEvents());
	}

	public PipeStatus getCurrentStatus() {
		List<PipeStatus> status = new ArrayList<>(pipe.getStatusMap().values());
		Collections.sort(status, new Comparator<PipeStatus>() {
			@Override
			public int compare(PipeStatus o1, PipeStatus o2) {
				if (o1 == null) {
					return 1;
				} else if (o2 == null) {
					return -1;
				}
				return o1.getCode() < o2.getCode() ? 1 : -1;
			}
		});
		return status.get(0);
	}
	
}
