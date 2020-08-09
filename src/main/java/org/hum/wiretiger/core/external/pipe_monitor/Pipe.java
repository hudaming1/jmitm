package org.hum.wiretiger.core.external.pipe_monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

@Getter
public class Pipe {
	
	private static final AtomicInteger counter = new AtomicInteger(1);

	// client->proxy
	private ChannelHandlerContext sourceCtx;
	// proxy->server
	private ChannelHandlerContext targetCtx;
	// request
	private DefaultHttpRequest request;
	// responseList
	private List<FullHttpResponse> responseList = new ArrayList<FullHttpResponse>();
	// pipe status
	private PipeStatus status;
	// create time
	private long birthday;
	// pipeId
	private int id;
	/**
	 * 1-http; 2-https
	 * {@link Protocol}
	 */
	private int protocol;
	// 状态时间轴
	private Map<Long, PipeStatus> statusTimeline = new ConcurrentHashMap<>();
	
	public Pipe(ChannelHandlerContext ctx) {
		this.sourceCtx = ctx;
		this.birthday = System.currentTimeMillis();
		this.id = counter.getAndIncrement();
		recordStatus(PipeStatus.Init);
	}
	
	public void addResponse(FullHttpResponse resp) {
		this.responseList.add(resp);
	}
	
	public void recordStatus(PipeStatus status) {
		this.status = status;
		statusTimeline.put(System.currentTimeMillis(), status);
	}
	
	public void inactive(ChannelHandlerContext ctx) {
		recordStatus(PipeStatus.Closed);
	}

	public void error(ChannelHandlerContext ctx) {
		recordStatus(PipeStatus.Error);
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public void setRequest(DefaultHttpRequest request) {
		this.request = request;
	}
}
