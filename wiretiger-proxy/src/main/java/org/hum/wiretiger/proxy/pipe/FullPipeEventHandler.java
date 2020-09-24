package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FullPipeEventHandler extends FullPipeHandler {
	
	private EventHandler eventHandler;

	public FullPipeEventHandler(FullPipeHandler next, EventHandler eventHandler) {
		super(next);
		this.eventHandler = eventHandler;
	}

	@Override
	protected void clientConnect0(WtPipeContext ctx) {
		log.info("FullPipeEventHandler connect log");
		eventHandler.fireConnectEvent(ctx);
	}

	@Override
	protected void clientDisconnect0(WtPipeContext ctx) {
		eventHandler.fireDisconnectEvent(ctx);
	}

	@Override
	protected void clientParsed0(WtPipeContext ctx) {
		eventHandler.fireChangeEvent(ctx);
	}

	@Override
	protected void clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		eventHandler.fireChangeEvent(ctx);
	}

	@Override
	protected void serverConnect0(WtPipeContext ctx) {
		eventHandler.fireConnectEvent(ctx);
	}

	@Override
	protected void serverHandshakeSucc0(WtPipeContext ctx) {
		eventHandler.fireChangeEvent(ctx);
	}

	@Override
	protected void serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		eventHandler.fireChangeEvent(ctx);
	}

	@Override
	protected void clientFlush0(WtPipeContext ctx, FullHttpResponse response) {
		eventHandler.fireChangeEvent(ctx);
	}

	@Override
	protected void clientClose0(WtPipeContext ctx) {
		eventHandler.fireDisconnectEvent(ctx);
	}

	@Override
	protected void serverClose0(WtPipeContext ctx) {
		eventHandler.fireDisconnectEvent(ctx);
	}

	@Override
	protected void clientError0(WtPipeContext ctx, Throwable cause) {
		eventHandler.fireErrorEvent(ctx);
	}

	@Override
	protected void serverError0(WtPipeContext ctx, Throwable cause) {
		eventHandler.fireErrorEvent(ctx);
	}

	@Override
	protected void clientHandshakeSucc0(WtPipeContext ctx) {
		eventHandler.fireChangeEvent(ctx);
	}

	@Override
	protected void clientHandshakeFail0(WtPipeContext ctx) {
		eventHandler.fireChangeEvent(ctx);		
	}

	@Override
	protected void serverConnectFailed0(WtPipeContext ctx) {
		eventHandler.fireChangeEvent(ctx);		
	}

	@Override
	protected void serverHandshakeFail0(WtPipeContext ctx) {
		eventHandler.fireChangeEvent(ctx);		
	}

}
