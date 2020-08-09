package org.hum.wiretiger.core.pipe.bean;

import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.ReferenceCountUtil;

public class PipeHolder {

	private static final AtomicInteger counter = new AtomicInteger(1);

	private Pipe pipe = new Pipe();

	public static PipeHolder create() {
		PipeHolder holder = new PipeHolder();
		holder.pipe.setId(counter.getAndIncrement());
		holder.pipe.addStatus(PipeStatus.Init);
		return holder;
	}
	
	public void registClient(Channel channel) {
		this.pipe.setSourceCtx(channel);
	}
	
	public void registServer(Channel channel) {
		this.pipe.setTargetCtx(channel);
	}

	public void onConnect4BackChannel(Channel channel) {
		pipe.setTargetCtx(channel);
	}

	public void onReadRequest(HttpObject req) {
		pipe.getTargetCtx().writeAndFlush(req);
	}

	public void onReadResponse(HttpObject res) {
		ReferenceCountUtil.retain(res);
		pipe.getSourceCtx().writeAndFlush(res);
	}

	public void onDisconnect4FrontChannel() {
		if (pipe.getTargetCtx().isActive()) {
			pipe.getTargetCtx().close();
		}
	}

	public void onDisconnect4BackChannel() {
		if (pipe.getSourceCtx().isActive()) {
			pipe.getSourceCtx().close();
		}
	}

	public void onError4FrontChannel() {
		if (pipe.getTargetCtx().isActive()) {
			pipe.getTargetCtx().close();
		}
	}

	public void onError4BackChannel() {
		if (pipe.getSourceCtx().isActive()) {
			pipe.getSourceCtx().close();
		}
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
		return pipe.getRequest().uri();
	}
}
