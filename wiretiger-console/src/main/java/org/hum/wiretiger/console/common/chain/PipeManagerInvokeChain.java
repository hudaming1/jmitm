package org.hum.wiretiger.console.common.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.console.websocket.service.WsPipeService;
import org.hum.wiretiger.proxy.facade.PipeInvokeChain;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeManagerInvokeChain extends PipeInvokeChain {

	private static ConcurrentHashMap<Channel, WtPipeContext> pipes4ClientChannel = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Long, WtPipeContext> pipes4Id = new ConcurrentHashMap<>();
	private WsPipeService wsPipeService = new WsPipeService();

	public PipeManagerInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected boolean clientConnect0(WtPipeContext ctx) {
		if (pipes4ClientChannel.containsKey(ctx.getClientChannel())) {
			log.error(ctx.getClientChannel() + "has exists, id=" + pipes4ClientChannel.get(ctx.getClientChannel()).getId());
			throw new WiretigerException(ctx.getClientChannel() + " has exists");
		}
		pipes4Id.put(ctx.getId(), ctx);
		pipes4ClientChannel.put(ctx.getClientChannel(), ctx);
		wsPipeService.sendConnectMsg(ctx);
		return true;
	}

	public static WtPipeContext getById(Long id) {
		return pipes4Id.get(id);
	}
	
	public static List<WtPipeContext> getAll() {
		List<WtPipeContext> list = new ArrayList<>();
		list.addAll(pipes4Id.values());
		// 按照Id顺序展示
		Collections.sort(list, new Comparator<WtPipeContext>() {
			@Override
			public int compare(WtPipeContext o1, WtPipeContext o2) {
				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				}
				return o1.getId() > o2.getId() ? 1 : -1;
			}
		});
		return list;
	}

	@Override
	protected boolean clientParsed0(WtPipeContext ctx) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverConnect0(WtPipeContext ctx, InetAddress InetAddress) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverHandshakeSucc0(WtPipeContext ctx) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverFlush0(WtPipeContext ctx, FullHttpRequest request) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientFlush0(WtPipeContext ctx, FullHttpResponse response) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientClose0(WtPipeContext ctx) {
		wsPipeService.sendDisConnectMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverClose0(WtPipeContext ctx) {
		wsPipeService.sendDisConnectMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientError0(WtPipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverError0(WtPipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientHandshakeSucc0(WtPipeContext ctx) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverConnectFailed0(WtPipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}
}
