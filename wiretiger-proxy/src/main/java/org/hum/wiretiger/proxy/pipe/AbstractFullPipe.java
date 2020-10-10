package org.hum.wiretiger.proxy.pipe;

import java.util.Stack;

import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.chain.FullPipeHandler;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

/**
 * PipeContext的实现类，抽象了Pipe整个通信流程，不关心Http和Https协议
 * @author hudaming
 */
@Slf4j
@Sharable
public abstract class AbstractFullPipe extends AbstractPipeHandler {
	
	protected MockHandler mockHandler;
	protected FullPipeHandler fullPipeHandler;
	// 当前保持的服务端连接
	protected BackPipe currentBack;
	protected Stack<FullHttpRequest> mockRequestStack = new Stack<>();

	public AbstractFullPipe(FrontPipe front, FullPipeHandler fullPipeHandler, WtPipeContext wtContext, MockHandler mockHandler) {
		// init
		super(wtContext);
		this.mockHandler = mockHandler;
		this.fullPipeHandler = fullPipeHandler;
		this.wtContext = wtContext;
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext clientCtx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			
			if (request.decoderResult().isFailure()) {
				log.error("[" + wtContext.getId() + "] decode failure, cause=" + request.decoderResult().cause().getMessage());
				return ;
			}
			
			// mock
			if (mockHandler != null) {
				mockHandler.mock(request);
				mockRequestStack.push(request);
			}
			
			fullPipeHandler.clientRead(wtContext, request);

			connect(request);
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		currentBack.getChannel().writeAndFlush(msg);
	}
	
	protected abstract void connect(FullHttpRequest request) throws InterruptedException;

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpResponse) {
			FullHttpResponse resp = (FullHttpResponse) msg;
			
			// do mock response..
			if (mockHandler != null) {
				mockHandler.mock(mockRequestStack.pop(), resp);
			}
			
			wtContext.appendResponse(resp);
			
			fullPipeHandler.serverRead(wtContext, resp);
		} else if (msg instanceof LastHttpContent) { 
			log.warn("need support more types, find type=" + msg.getClass());
		} else {
			wtContext.addEvent(PipeEventType.Received, "读取服务端请求(" + msg.getClass() + ")");
			log.warn("need support more types, find type=" + msg.getClass());
		}
		
		wtContext.getClientChannel().writeAndFlush(msg);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof FullHttpResponse) {
			fullPipeHandler.clientFlush(wtContext, (FullHttpResponse) msg);
		}
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		// [HTTP] 5.ChannelHandler拦截写事件
		if (msg instanceof FullHttpRequest) {
			fullPipeHandler.serverFlush(wtContext, (FullHttpRequest) msg);
		}
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
		fullPipeHandler.clientClose(wtContext);
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (wtContext.getClientChannel() != null && wtContext.getClientChannel().isActive()) {
			wtContext.getClientChannel().close();
		}
		removeBackpipe(getBackChannel(ctx.channel()));
		fullPipeHandler.serverClose(wtContext);
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("front exception, pipeId=" + wtContext.getId() + ", cause=" + cause.getMessage());
		fullPipeHandler.clientError(wtContext, cause);
		close();
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("back exception, pipeId=" + wtContext.getId() + ", cause=" + cause.getMessage());
		fullPipeHandler.serverError(wtContext, cause);
		close();
	}
	
	public void close() {
		if (wtContext.getClientChannel() != null && wtContext.getClientChannel().isActive()) {
			wtContext.getClientChannel().close();
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
