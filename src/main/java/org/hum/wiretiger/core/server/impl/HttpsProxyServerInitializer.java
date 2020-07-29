/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.hum.wiretiger.core.server.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpsProxyServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";

	public HttpsProxyServerInitializer() {
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();

		// 如果需要https代理，则开启这个handler(如果不用https代理，则需要注释以下代码)
		p.addLast(new ChannelInboundHandlerAdapter() {
			
		});
	}
	
	private String[] parse2Domain(ByteBuf byteBuf) {
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		String[] requestLineArr = new String(bytes).split(" ");
		String method = requestLineArr[0];
		String hostAndPort = requestLineArr[1];
		byteBuf.resetReaderIndex();
		return new String[] { method, hostAndPort.split(":")[0] };
	}

	private static class Forward {
		private Bootstrap bootStrap = null;
		private String host;
		private int port;
		public Forward(ChannelHandlerContext ctx, String host, int port) {
			this.host = host;
			this.port = port;
			bootStrap = new Bootstrap();
			bootStrap.channel(NioSocketChannel.class);
			bootStrap.group(ctx.channel().eventLoop());
			NettyBootstrapUtil.initTcpServerOptions(bootStrap, NettyProxyContext.getConfig());
			bootStrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new ForwardHandler(ctx.channel()), new InactiveHandler(ctx.channel()));
				}
			});
		}
		
		public ChannelFuture start() {
			return bootStrap.connect(host, port);
		}
	}
}
