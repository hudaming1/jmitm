package org.hum.wiretiger.core.handler.bean;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import lombok.Setter;

@Setter
public class Pipe {

	// 
	private ChannelHandlerContext sourceCtx;
	// 
	private ChannelHandlerContext targetCtx;
	//
	private DefaultHttpRequest request;
	//
	private DefaultHttpResponse response;
}
