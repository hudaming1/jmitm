package org.hum.wiretiger.core.handler.bean;

import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import lombok.Data;

@Data
public class Pipe {
	
	public static final String PIPE_ATTR_NAME = "PIPE";
	private static final AtomicInteger counter = new AtomicInteger(1);

	// 
	private ChannelHandlerContext sourceCtx;
	// 
	private ChannelHandlerContext targetCtx;
	//
	private DefaultHttpRequest request;
	//
	private DefaultHttpResponse response;
	//
	private ConnectionStatus status;
	//
	private long birthday;
	//
	private int id;
	
	public Pipe() {
		this.birthday = System.currentTimeMillis();
		this.id = counter.getAndIncrement();
	}
}
