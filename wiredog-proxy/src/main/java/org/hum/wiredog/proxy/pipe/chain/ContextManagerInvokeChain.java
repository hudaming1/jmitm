package org.hum.wiredog.proxy.pipe.chain;

import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.WtPipeContext;
import org.hum.wiredog.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiredog.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiredog.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class ContextManagerInvokeChain extends PipeInvokeChain {

	public ContextManagerInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected boolean clientConnect0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Init);
		ctx.addEvent(PipeEventType.Init, "连接初始化");
		return true;
	}

	@Override
	protected boolean clientParsed0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Parsed);
		ctx.addEvent(PipeEventType.Parsed, "解析连接协(" + ctx.getProtocol() + ")");
		return true;
	}

	@Override
	protected boolean clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		ctx.recordStatus(PipeStatus.Read);
		ctx.addEvent(PipeEventType.Read, "读取客户端请求，uri=" + request.uri());
		return true;
	}

	@Override
	protected boolean serverConnect0(WtPipeContext ctx, InetAddress inetAddress) {
		ctx.recordStatus(PipeStatus.Connected);
		ctx.addEvent(PipeEventType.ServerConnected, "与服务端" + inetAddress + "建立连接完成");
		return true;
	}

	@Override
	protected boolean serverHandshakeSucc0(WtPipeContext ctx) {
		ctx.addEvent(PipeEventType.ServerTlsFinish, "与服务端握手完成");
		return true;
	}

	@Override
	protected boolean serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		ctx.recordStatus(PipeStatus.Received);
		ctx.addEvent(PipeEventType.Received, "读取服务端请求，字节数\"" + response.content().readableBytes() + "\"bytes");
		return true;
	}

	@Override
	protected boolean serverFlush0(WtPipeContext ctx, FullHttpRequest request) {
		ctx.recordStatus(PipeStatus.Forward);
		ctx.addEvent(PipeEventType.Forward, "已将客户端请求转发给服务端");	
		return true;	
	}

	@Override
	protected boolean clientFlush0(WtPipeContext ctx, FullHttpResponse response) {
		ctx.recordStatus(PipeStatus.Flushed);
		ctx.addEvent(PipeEventType.Flushed, "已将服务端响应转发给客户端");
		return true;
	}

	@Override
	protected boolean clientClose0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Closed);
		ctx.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
		return true;
	}

	@Override
	protected boolean serverClose0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Closed);
		ctx.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
		return true;
	}

	@Override
	protected boolean clientError0(WtPipeContext ctx, Throwable cause) {
		ctx.recordStatus(PipeStatus.Error);
		ctx.addEvent(PipeEventType.Error, "客户端异常：" + cause.getMessage());
		return true;
	}

	@Override
	protected boolean serverError0(WtPipeContext ctx, Throwable cause) {
		ctx.recordStatus(PipeStatus.Error);
		ctx.addEvent(PipeEventType.Error, "服务端异常：" + cause.getMessage());
		return true;
	}

	@Override
	protected boolean clientHandshakeSucc0(WtPipeContext ctx) {
		ctx.addEvent(PipeEventType.ClientTlsFinish, "客户端握手完成");
		return true;
	}

	@Override
	protected boolean clientHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		ctx.addEvent(PipeEventType.Error, "客户端握手异常");
		return true;
	}

	@Override
	protected boolean serverConnectFailed0(WtPipeContext ctx, Throwable cause) {
		ctx.addEvent(PipeEventType.Error, "与服务端建立连接失败");
		ctx.recordStatus(PipeStatus.Error);
		return true;
	}

	@Override
	protected boolean serverHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		ctx.addEvent(PipeEventType.Error, "与服务端握手失败");
		ctx.recordStatus(PipeStatus.Error);
		return true;
	}

}
