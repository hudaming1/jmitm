package org.hum.wiretiger.proxy.pipe;

import java.util.Stack;

import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.proxy.session.WtSessionManager;
import org.hum.wiretiger.proxy.session.bean.WtSession;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class FullPipe extends AbstractPipeHandler {
	
	protected MockHandler mockHandler;
	protected EventHandler eventHandler;
	/**
	 * 保存了当前HTTP连接，没有等待响应的请求
	 */
	private Stack<WtSession> reqStack4WattingResponse = new Stack<>();
	// 当前保持的服务端连接
	private BackPipe currentBack;
	private boolean isHttps;

	public FullPipe(FrontPipe front, EventHandler eventHandler, WtPipeContext wtContext, boolean isHttps, MockHandler mockHandler) {
		// init
		super(wtContext, front);
		this.mockHandler = mockHandler;
		this.eventHandler = eventHandler;
		this.wtContext = wtContext;
		this.isHttps = isHttps;
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		log.info("[" + wtContext.getId() + "]server connect");
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext clientCtx, Object msg) throws Exception {
		log.info("[" + wtContext.getId() + "] channel read");
		wtContext.recordStatus(PipeStatus.Read);
		
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			wtContext.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpRequest");
			
			if (request.decoderResult().isFailure()) {
				log.error("[" + wtContext.getId() + "] decode failure, cause=" + request.decoderResult().cause().getMessage());
				return ;
			}
			
			// mock
//			mockHandler.mock(request);
			if ("www.baidu.com".equals(request.headers().get("Host").split(":")[0]) && 
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri()))) {
				log.info("hit baidu_logo");
				request.headers().set("Host", "p.ssl.qhimg.com:443");
				request.setUri("/t012cdb572f41b93733.png");
			}

			InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, isHttps);
			wtContext.appendRequest(request);
			
			currentBack = super.select(InetAddress.getHost(), InetAddress.getPort());
			if (currentBack == null) {
				currentBack = initBackpipe(InetAddress, isHttps);
			}
			
			if (isHttps) {
				log.info("[" + wtContext.getId() + "] connect " + InetAddress);
				if (!currentBack.isActive()) {
					currentBack.connect().addListener(f->{
						if (!f.isSuccess()) {
							log.error("[" + wtContext.getId() + "] server connect error.", f.cause());
							wtContext.addEvent(PipeEventType.Error, "[X]与服务端" + InetAddress + "建立连接失败");
							wtContext.recordStatus(PipeStatus.Error);
							eventHandler.fireChangeEvent(wtContext);
							close();
							return ;
						}
						wtContext.registServer(currentBack.getChannel());
						wtContext.recordStatus(PipeStatus.Connected);
						eventHandler.fireChangeEvent(wtContext);
						wtContext.addEvent(PipeEventType.ServerConnected, "与服务端" + InetAddress + "建立连接完成");
						log.info("[" + wtContext.getId() + "] server connect ok(listener)");
					}).sync();
					log.info("[" + wtContext.getId() + "] server connect ok(sync)");
					currentBack.handshakeFuture().addListener(tls-> {
						if (!tls.isSuccess()) {
							log.error("[" + wtContext.getId() + "] server tls error", tls.cause());
							wtContext.addEvent(PipeEventType.Error, "[X]与服务端" + InetAddress + "建立连接失败");
							wtContext.recordStatus(PipeStatus.Error);
							eventHandler.fireChangeEvent(wtContext);
							close();
							return ;
						}
						log.info("[" + wtContext.getId() + "] server tls ok");
						wtContext.addEvent(PipeEventType.ServerTlsFinish, "与服务端" + InetAddress + "握手完成");
						currentBack.getChannel().pipeline().addLast(this);
					}).sync();
				}
			} else {
				if (!currentBack.isActive()) {
					log.info("[" + wtContext.getId() + "]server active, channel=" + currentBack.getChannel());
					currentBack.connect().addListener(f-> {
						if (!f.isSuccess()) {
							log.error("[" + wtContext.getId() + "] server connect error.", f.cause());
							wtContext.addEvent(PipeEventType.Error, "[X]与服务端" + InetAddress + "建立连接失败");
							wtContext.recordStatus(PipeStatus.Error);
							eventHandler.fireChangeEvent(wtContext);
							close();
							return ;
						}
						wtContext.registServer(currentBack.getChannel());
						wtContext.recordStatus(PipeStatus.Connected);
						eventHandler.fireChangeEvent(wtContext);
						wtContext.addEvent(PipeEventType.ServerConnected, "与服务端" + InetAddress + "建立连接完成");
						currentBack.getChannel().pipeline().addLast(this);
					}).sync();
				}
			}
		} else if (msg instanceof LastHttpContent) {
			wtContext.addEvent(PipeEventType.Read, "读取客户端请求，LastHttpContent");
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		log.info("[" + wtContext.getId() + "] flush to server.");
		currentBack.getChannel().writeAndFlush(msg);
		eventHandler.fireChangeEvent(wtContext);
	}

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.info("[" + wtContext.getId() + "] 6");
		if (msg instanceof FullHttpResponse) {
			if (reqStack4WattingResponse.isEmpty() || reqStack4WattingResponse.size() > 1) {
				log.warn(this + "---reqStack4WattingResponse.size error, size=" + reqStack4WattingResponse.size());
				super.front.getChannel().writeAndFlush(msg);
				return ;
			}
			WtSession session = reqStack4WattingResponse.pop();
			FullHttpResponse resp = (FullHttpResponse) msg;
			
			// do mock response..
//			mockHandler.mock(session.getRequest(), resp);

			wtContext.appendResponse(resp);
			wtContext.addEvent(PipeEventType.Received, "读取服务端请求，字节数\"" + resp.content().readableBytes() + "\"bytes");
			
			byte[] bytes = null;
			if (resp.content().readableBytes() > 0) {
				bytes = new byte[resp.content().readableBytes()];
				resp.content().duplicate().readBytes(bytes);
			}
			session.setResponse(resp, bytes, System.currentTimeMillis());
			// TODO 目前是当服务端返回结果，具备构建一个完整当Session后才触发NewSession事件，后续需要将动作置前
			WtSessionManager.get().add(session);
			eventHandler.fireNewSessionEvent(wtContext, session);
		} else if (msg instanceof LastHttpContent) { 
			log.warn("need support more types, find type=" + msg.getClass());
		} else {
			wtContext.addEvent(PipeEventType.Received, "读取服务端请求(" + msg.getClass() + ")");
			log.warn("need support more types, find type=" + msg.getClass());
		}
		
		wtContext.recordStatus(PipeStatus.Received);
		eventHandler.fireChangeEvent(wtContext);
		super.front.getChannel().writeAndFlush(msg);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		log.info("[" + wtContext.getId() + "] 7");
		wtContext.recordStatus(PipeStatus.Flushed);
		wtContext.addEvent(PipeEventType.Flushed, "已将服务端响应转发给客户端");
		eventHandler.fireChangeEvent(wtContext);
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof HttpRequest) {
			reqStack4WattingResponse.push(new WtSession(wtContext.getId(), (HttpRequest) msg, System.currentTimeMillis()));
		}
		// [HTTP] 5.ChannelHandler拦截写事件
		log.info("[" + wtContext.getId() + "] 5");
		wtContext.recordStatus(PipeStatus.Forward);
		wtContext.addEvent(PipeEventType.Forward, "已将客户端请求转发给服务端");
		eventHandler.fireChangeEvent(wtContext);
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (backMap != null && !backMap.isEmpty()) {
			for (BackPipe back : backMap.values()) {
				if (back.isActive()) {
					back.getChannel().close();
				}
			}
		}
		wtContext.recordStatus(PipeStatus.Closed);
		wtContext.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
		eventHandler.fireDisconnectEvent(wtContext);
		log.info("client disconnect");
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (front.getChannel() != null && front.getChannel().isActive()) {
			front.getChannel().close();
		}
		removeBackpipe(getBackChannel(ctx.channel()));
		wtContext.recordStatus(PipeStatus.Closed);
		wtContext.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
		eventHandler.fireDisconnectEvent(wtContext);
		log.info("server disconnect");
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("front exception, pipeId=" + wtContext.getId(), cause);
		wtContext.recordStatus(PipeStatus.Error);
		wtContext.addEvent(PipeEventType.Error, "客户端异常：" + cause.getMessage());
		close();
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("back exception, pipeId=" + wtContext.getId(), cause);
		wtContext.recordStatus(PipeStatus.Error);
		wtContext.addEvent(PipeEventType.Error, "服务端异常：" + cause.getMessage());
		eventHandler.fireErrorEvent(wtContext);
		close();
	}
	
	public void close() {
		if (front.getChannel() != null && front.getChannel().isActive()) {
			front.getChannel().close();
		}
		if (backMap != null && !backMap.isEmpty()) {
			for (BackPipe back : backMap.values()) {
				if (back.isActive()) {
					back.getChannel().close();
				}
			}
		}
		// 确保最终状态是closed
		wtContext.recordStatus(PipeStatus.Closed);
	}
}
