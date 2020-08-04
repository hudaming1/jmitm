package org.hum.wiretiger.core.external.pipe_monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Data;

@Data
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
	
	public Pipe() {
		this.birthday = System.currentTimeMillis();
		this.id = counter.getAndIncrement();
	}
	
	public void addResponse(FullHttpResponse resp) {
		this.responseList.add(resp);
	}
}
