package org.hum.wiretiger.proxy.pipe.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class WtPipeContext {

	private String sourceHost;
	private int sourcePort;
	private String targetHost;
	private int targetPort;
	private WtPipe pipe = new WtPipe();
	
	public WtPipeContext(int id, Channel clientChannel) {
		pipe.setId(id);
		pipe.addStatus(PipeStatus.Init);
		pipe.setSourceCtx(clientChannel);
		addEvent(PipeEventType.Init, "连接初始化");
	}
	
	public void setName(String name) {
		this.pipe.setName(name);
	}
	
	public String getName() {
		return this.pipe.getName();
	}
	
	public void registServer(Channel channel) {
		this.pipe.setTargetCtx(channel);
	}
	
	public void appendRequest(HttpRequest request) {
		this.pipe.addRequest(request);
	}
	
	public void appendResponse(FullHttpResponse response) {
		this.pipe.addResponse(response);
	}
	 
	public void recordStatus(PipeStatus status) {
		this.pipe.addStatus(status);
	}
	
	public Long getId() {
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
	
	public List<HttpRequest> getRequests() {
		return Collections.unmodifiableList(pipe.getRequestList());
	}
	
	public List<FullHttpResponse> getResponses() {
		return Collections.unmodifiableList(pipe.getResponseList());
	}
	
	public Map<Long, PipeStatus> getStatusTimeline() {
		return Collections.unmodifiableMap(pipe.getStatusMap());
	}
	
	public void addEvent(PipeEventType type, String desc) {
		this.pipe.addEvent(new WtPipeEvent(type, desc, System.currentTimeMillis()));
	}
	
	public List<WtPipeEvent> getEventList() {
		return Collections.unmodifiableList(this.pipe.getEvents());
	}

	public PipeStatus getCurrentStatus() {
		List<PipeStatus> status = new ArrayList<>(pipe.getStatusMap().values());
		Collections.sort(status, new Comparator<PipeStatus>() {
			@Override
			public int compare(PipeStatus o1, PipeStatus o2) {
				if (o1 == null && o2 == null) {
				    return 0;
				} else if (o1 == null) {
					return 1;
				} else if (o2 == null) {
					return -1;
				} else if (o1.getCode() == o2.getCode()) {
					return 0;
				}
				return o1.getCode() < o2.getCode() ? 1 : -1;
			}
		});
		return status.get(0);
	}
	
	public void setSource(String host, int port) {
		this.sourceHost = host;
		this.sourcePort = port;
	}
	
	public void setTarget(String host, int port) {
		this.targetHost = host;
		this.targetPort = port;
	}
	
	public String getSourceHost() {
		return sourceHost;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public boolean isHttps() {
		return pipe.getProtocol() == Protocol.HTTPS;
	}
}
