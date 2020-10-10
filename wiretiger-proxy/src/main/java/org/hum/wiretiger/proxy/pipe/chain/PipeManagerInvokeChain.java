package org.hum.wiretiger.proxy.pipe.chain;

import java.util.concurrent.ConcurrentHashMap;

import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeManagerInvokeChain extends DefaultPipeInvokeChain {

	private ConcurrentHashMap<Channel, WtPipeContext> pipes4ClientChannel = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, WtPipeContext> pipes4Id = new ConcurrentHashMap<>();

	public PipeManagerInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected void clientConnect0(WtPipeContext ctx) {
		if (pipes4ClientChannel.containsKey(ctx.getClientChannel())) {
			log.error(ctx.getClientChannel() + "has exists, id=" + pipes4ClientChannel.get(ctx.getClientChannel()).getId());
			throw new WiretigerException(ctx.getClientChannel() + " has exists");
		}
		pipes4Id.put(ctx.getId(), ctx);
		pipes4ClientChannel.put(ctx.getClientChannel(), ctx);
	}
}
