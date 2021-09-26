package org.hum.jmitm.console.websocket.handler;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;
import java.util.Map;

import org.hum.jmitm.console.websocket.ConsoleManager;
import org.hum.jmitm.console.websocket.bean.WsClientMessage;
import org.hum.jmitm.console.websocket.bean.WsServerMessage;
import org.hum.jmitm.console.websocket.enumtype.MessageTypeEnum;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketDecoder extends ChannelInboundHandlerAdapter {

	public static WebSocketServerHandshaker webSocketHandshaker;
	private final String CONSOLE_PARAM_NAME = "console_id";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 处理业务消息
		if (msg instanceof FullHttpRequest) {
			handlerHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		} else {
			log.warn("[notice] unknown message type:" + msg.getClass());
		}
	}

	private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
		if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get(HttpHeaderNames.UPGRADE)))) {
			ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        } else if (req.method() != GET) {
            ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
		
		// parse console_id
		String consoleId = getConsoleId(req);
		if (StringUtil.isNullOrEmpty(consoleId)) {
			ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
		}
		
		// regist console_connection
		ConsoleManager.get().regist(consoleId, ctx.channel());
		log.info("console({}) regist", consoleId);
		
		// web_socket handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("", null, false);
		webSocketHandshaker = wsFactory.newHandshaker(req);
		if (webSocketHandshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			return ;
		} else {
			webSocketHandshaker.handshake(ctx.channel(), req); 
		}
	}

	private String getConsoleId(FullHttpRequest req) {
		try {
			Map<String, List<String>> parameters = new QueryStringDecoder(req.uri()).parameters();
			return parameters.get(CONSOLE_PARAM_NAME).get(0);
		} catch (Exception ce) {
			log.error("cann't parse console_id", ce);
			return null;
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			return;
		} else if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		} else if (frame instanceof TextWebSocketFrame) {
			handlerTextWebSocketFrame(ctx, (TextWebSocketFrame) frame);
			return;
		} else {
			// TODO 未知类型消息无法处理，直接关闭（不过这样真的好吗？）
			webSocketHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
			return;
		}
	}

	private void handlerTextWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame message) {
		try {
			WsClientMessage request = new WsClientMessage();
			JSONObject parseObject = JSON.parseObject(message.text());
			request.setType(MessageTypeEnum.getEnum(parseObject.getInteger("type")));
			request.setData(parseObject.getJSONObject("data"));
			ctx.fireChannelRead(request);
			return;
		} catch (Exception ce) {
			log.error("handle websocket frame error, can't parse request, text=" + message.text(), ce);
			ctx.channel().writeAndFlush(new WsServerMessage<Void>(MessageTypeEnum.MESSAGE_PARSE_ERROR));
			return;
		}
	}
}
