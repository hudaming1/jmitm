package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface FullPipeHandler {

	public void clientConnect(WtPipeContext ctx);

	public void clientDisconnect(WtPipeContext ctx);

	public void clientParsed(WtPipeContext ctx);
	
	public void clientRead(WtPipeContext ctx, FullHttpRequest request);
	
	public void serverConnect(WtPipeContext ctx);
	
	public void serverHandshakeSucc(WtPipeContext ctx);
	
	public void serverFlush(WtPipeContext ctx, FullHttpRequest request);
	
	public void serverRead(WtPipeContext ctx, FullHttpResponse response);
	
	public void clientFlush(WtPipeContext ctx, FullHttpResponse response);
	
	public void clientClose(WtPipeContext ctx);
	
	public void serverClose(WtPipeContext ctx);
	
	public void clientError(WtPipeContext ctx);
	
	public void serverError(WtPipeContext ctx);

	public void clientHandshakeSucc(WtPipeContext ctx);
	
	public void clientHandshakeFail(WtPipeContext ctx);
	
	public void serverConnectFailed(WtPipeContext ctx);
	
	public void serverHandshakeFail(WtPipeContext ctx);
	
}
