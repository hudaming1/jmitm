package org.hum.wiretiger.core.handler.bean;

import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import lombok.Data;

@Data
public class Pipe {
	
	public static final String PIPE_ATTR_NAME = "PIPE";

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
	
	public Pipe() {
		this.birthday = System.currentTimeMillis();
	}
}
