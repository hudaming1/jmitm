package org.hum.jmitm.console.common.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hum.jmitm.common.exception.WiredogException;
import org.hum.jmitm.common.util.SyncLinkedHashMap;
import org.hum.jmitm.common.util.HttpMessageUtil.InetAddress;
import org.hum.jmitm.console.websocket.service.WsPipeService;
import org.hum.jmitm.proxy.facade.PipeContext;
import org.hum.jmitm.proxy.facade.PipeInvokeChain;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeManagerInvokeChain extends PipeInvokeChain {

	private static Map<Channel, PipeContext> pipes4ClientChannel;
	private static Map<Long, PipeContext> pipes4Id;
	private WsPipeService wsPipeService = new WsPipeService();

	public PipeManagerInvokeChain(PipeInvokeChain next, int queueSize) {
		super(next);
		pipes4ClientChannel = new SyncLinkedHashMap<>(queueSize);
		pipes4Id = new SyncLinkedHashMap<>(queueSize);
	}

	@Override
	protected boolean clientConnect0(PipeContext ctx) {
		if (pipes4ClientChannel.containsKey(ctx.getClientChannel())) {
			log.error(ctx.getClientChannel() + "has exists, id=" + pipes4ClientChannel.get(ctx.getClientChannel()).getId());
			throw new WiredogException(ctx.getClientChannel() + " has exists");
		}
		pipes4Id.put(ctx.getId(), ctx);
		pipes4ClientChannel.put(ctx.getClientChannel(), ctx);
		wsPipeService.sendConnectMsg(ctx);
		return true;
	}

	public static PipeContext getById(Long id) {
		return pipes4Id.get(id);
	}
	
	public static List<PipeContext> getAll() {
		List<PipeContext> list = new ArrayList<>();
		list.addAll(pipes4Id.values());
		// 按照Id顺序展示
		Collections.sort(list, new Comparator<PipeContext>() {
			@Override
			public int compare(PipeContext o1, PipeContext o2) {
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
	protected boolean clientParsed0(PipeContext ctx) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientRead0(PipeContext ctx, FullHttpRequest request) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverConnect0(PipeContext ctx, InetAddress InetAddress) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverHandshakeSucc0(PipeContext ctx) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverFlush0(PipeContext ctx, FullHttpRequest request) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverRead0(PipeContext ctx, FullHttpResponse response) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientFlush0(PipeContext ctx, FullHttpResponse response) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientClose0(PipeContext ctx) {
		wsPipeService.sendDisConnectMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverClose0(PipeContext ctx) {
		wsPipeService.sendDisConnectMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientError0(PipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverError0(PipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientHandshakeSucc0(PipeContext ctx) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean clientHandshakeFail0(PipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverConnectFailed0(PipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}

	@Override
	protected boolean serverHandshakeFail0(PipeContext ctx, Throwable cause) {
		wsPipeService.sendStatusChangeMsg(ctx);
		return true;
	}
}
