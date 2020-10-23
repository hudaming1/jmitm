package org.hum.wiretiger.proxy.pipe.core;

import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.server.WtDefaultServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 只做单纯的转发操作，对数据内容不做任何解析处理
 * @author hudaming
 */
@Slf4j
public class SimpleForwardPipeHandler extends ChannelInboundHandlerAdapter {
	
	private WtPipeContext wtContext;
	private static EventLoopGroup eventLoopGroup = null;
	
	static {
		eventLoopGroup = new NioEventLoopGroup(WtDefaultServer.config.getThreads() / 2);
	}
	
	public SimpleForwardPipeHandler(WtPipeContext wtContext) {
		this.wtContext = wtContext;
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(eventLoopGroup);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ServerForwrdHandler(wtContext));
			}
		});
		try {
			bootstrap.connect(wtContext.getTargetHost(), wtContext.getTargetPort()).addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					if (!f.isSuccess()) {
						wtContext.getClientChannel().close();
						wtContext.recordStatus(PipeStatus.Closed);
						wtContext.addEvent(PipeEventType.ServerClosed, "HTTPS服务端建立失败(" + f.cause().getMessage() + ")");
						return ;
					}
					wtContext.recordStatus(PipeStatus.Connected);
					wtContext.addEvent(PipeEventType.ServerConnected, "HTTPS服务端建立完成(未解析)");
					wtContext.registServer(f.channel());
				}
			}).sync();
		} catch (Exception e) {
			if (e instanceof ConnectTimeoutException) {
				log.warn("connect target" + wtContext.getTargetHost() + ":" + wtContext.getTargetPort() + " failed");
				return ;
			}
			log.error("connect target" + wtContext.getTargetHost() + ":" + wtContext.getTargetPort() + " failed", e);
		}
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	this.wtContext.recordStatus(PipeStatus.Error);
		this.wtContext.addEvent(PipeEventType.Error, "客户端发生错误,原因:" + cause.getMessage());
		if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
        if (wtContext.getServerChannel().isActive()) {
        	wtContext.getServerChannel().close();
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	this.wtContext.recordStatus(PipeStatus.Closed);
		this.wtContext.addEvent(PipeEventType.ClientClosed, "客户端断开连接");
		if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
        if (wtContext.getServerChannel().isActive()) {
        	wtContext.getServerChannel().close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	this.wtContext.recordStatus(PipeStatus.Read);
		this.wtContext.addEvent(PipeEventType.Read, "读取客户端请求");
		this.wtContext.getServerChannel().writeAndFlush(msg);
    }
}

class ServerForwrdHandler extends ChannelInboundHandlerAdapter {
	
	private WtPipeContext wtContext;
	
	public ServerForwrdHandler(WtPipeContext wtContext) {
		this.wtContext = wtContext;
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	this.wtContext.recordStatus(PipeStatus.Received);
    	this.wtContext.addEvent(PipeEventType.Received, "读取服务端响应");
    	this.wtContext.getClientChannel().writeAndFlush(msg).addListener(f -> {
    		if (f.isSuccess()) {
    	    	this.wtContext.recordStatus(PipeStatus.Flushed);
    	    	this.wtContext.addEvent(PipeEventType.Flushed, "将服务端响应输出到客户端");
    		} else {
    			this.wtContext.recordStatus(PipeStatus.Error);
    			this.wtContext.addEvent(PipeEventType.Error, "将服务端响应输出到客户端失败, 原因:" + f.cause().getMessage());
    		}
    	});
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	this.wtContext.recordStatus(PipeStatus.Error);
		this.wtContext.addEvent(PipeEventType.Error, "服务端发生错误,原因:" + cause.getMessage());
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
        if (wtContext.getClientChannel().isActive()) {
        	wtContext.getClientChannel().close();
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	this.wtContext.recordStatus(PipeStatus.Closed);
		this.wtContext.addEvent(PipeEventType.ServerClosed, "服务端断开连接");
		if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
        if (wtContext.getClientChannel().isActive()) {
        	wtContext.getClientChannel().close();
        }
    }
}
