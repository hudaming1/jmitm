package org.hum.wiredog.console.common.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hum.wiredog.common.exception.wiredogException;
import org.hum.wiredog.common.util.SyncLinkedHashMap;
import org.hum.wiredog.console.websocket.service.WsPipeService;
import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.WtPipeContext;
import org.hum.wiredog.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeManagerInvokeChain extends PipeInvokeChain {

	private static Map<Channel, WtPipeContext> pipes4ClientChannel;
	private static Map<Long, WtPipeContext> pipes4Id;
	private WsPipeService wsPipeService = new WsPipeService();

	public PipeManagerInvokeChain(PipeInvokeChain next, int queueSize) {
		super(next);
		pipes4ClientChannel = new SyncLinkedHashMap<>(queueSize);
		pipes4Id = new SyncLinkedHashMap<>(queueSize);
	}

	@Override
	protected boolean clientConnect0(WtPipeContext ctx) {
		if (pipes4ClientChannel.containsKey(ctx.getClientChannel())) {
			log.error(ctx.getClientChannel() + "has exists, id=" + pipes4ClientChannel.get(ctx.getClientChannel()).getId());
			throw new wiredogException(ctx.getClientChannel() + " has exists");
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
