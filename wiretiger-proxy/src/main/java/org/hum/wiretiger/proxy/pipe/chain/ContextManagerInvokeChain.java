package org.hum.wiretiger.proxy.pipe.chain;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class ContextManagerInvokeChain extends AbstractPipeInvokeChain {

	public ContextManagerInvokeChain(AbstractPipeInvokeChain next) {
		super(next);
	}

	@Override
	protected void clientConnect0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Init);
		ctx.addEvent(PipeEventType.Init, "连接初始化");
	}

	@Override
	protected void clientParsed0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Parsed);
		ctx.addEvent(PipeEventType.Parsed, "解析连接协(" + ctx.getProtocol() + ")");
	}

	@Override
	protected void clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		ctx.recordStatus(PipeStatus.Read);
		ctx.addEvent(PipeEventType.Read, "读取客户端请求，uri=" + request.uri());
	}

	@Override
	protected void serverConnect0(WtPipeContext ctx, InetAddress inetAddress) {
		ctx.recordStatus(PipeStatus.Connected);
		ctx.addEvent(PipeEventType.ServerConnected, "与服务端" + inetAddress + "建立连接完成");
	}

	@Override
	protected void serverHandshakeSucc0(WtPipeContext ctx) {
		ctx.addEvent(PipeEventType.ServerTlsFinish, "与服务端握手完成");
	}

	@Override
	protected void serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		ctx.recordStatus(PipeStatus.Received);
		ctx.addEvent(PipeEventType.Received, "读取服务端请求，字节数\"" + response.content().readableBytes() + "\"bytes");
	}

	@Override
	protected void serverFlush0(WtPipeContext ctx, FullHttpRequest request) {
		ctx.recordStatus(PipeStatus.Forward);
		ctx.addEvent(PipeEventType.Forward, "已将客户端请求转发给服务端");		
	}

	@Override
	protected void clientFlush0(WtPipeContext ctx, FullHttpResponse response) {
		ctx.recordStatus(PipeStatus.Flushed);
		ctx.addEvent(PipeEventType.Flushed, "已将服务端响应转发给客户端");
	}

	@Override
	protected void clientClose0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Closed);
		ctx.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
	}

	@Override
	protected void serverClose0(WtPipeContext ctx) {
		ctx.recordStatus(PipeStatus.Closed);
		ctx.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
	}

	@Override
	protected void clientError0(WtPipeContext ctx, Throwable cause) {
		ctx.recordStatus(PipeStatus.Error);
		ctx.addEvent(PipeEventType.Error, "客户端异常：" + cause.getMessage());
	}

	@Override
	protected void serverError0(WtPipeContext ctx, Throwable cause) {
		ctx.recordStatus(PipeStatus.Error);
		ctx.addEvent(PipeEventType.Error, "服务端异常：" + cause.getMessage());
	}

	@Override
	protected void clientHandshakeSucc0(WtPipeContext ctx) {
		ctx.addEvent(PipeEventType.ClientTlsFinish, "客户端握手完成");
	}

	@Override
	protected void clientHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		ctx.addEvent(PipeEventType.Error, "客户端握手异常");
	}

	@Override
	protected void serverConnectFailed0(WtPipeContext ctx, Throwable cause) {
		ctx.addEvent(PipeEventType.Error, "与服务端建立连接失败");
		ctx.recordStatus(PipeStatus.Error);
	}

	@Override
	protected void serverHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		ctx.addEvent(PipeEventType.Error, "与服务端握手失败");
		ctx.recordStatus(PipeStatus.Error);
	}

}
