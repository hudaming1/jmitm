package org.hum.wiretiger.proxy.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hudaming
 */
@Slf4j
public class WtPipeManager {

	private static final AtomicInteger counter = new AtomicInteger(1);
	private ConcurrentHashMap<Channel, WtPipeContext> pipes4ClientChannel = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, WtPipeContext> pipes4Id = new ConcurrentHashMap<>();
	
	private WtPipeManager() {
		log.info("ConnectMonitor started..");
	}

	private static class WtPipeManagerHodler {
		private static WtPipeManager instance = new WtPipeManager();
	}

	public static WtPipeManager get() {
		return WtPipeManagerHodler.instance;
	}

	public WtPipeContext create(Channel clientChannel) {
		if (pipes4ClientChannel.containsKey(clientChannel)) {
			log.error(clientChannel + "has exists, id=" + pipes4ClientChannel.get(clientChannel).getId());
			throw new WiretigerException(clientChannel + " has exists");
		}
		WtPipeContext context = new WtPipeContext(counter.getAndIncrement(), clientChannel);
		context.setName(clientChannel.remoteAddress().toString());
		pipes4Id.put(context.getId(), context);
		pipes4ClientChannel.put(clientChannel, context);
		return context;
	}
	
	public WtPipeContext getById(Long id) {
		return pipes4Id.get(id);
	}
	
	public List<WtPipeContext> getAll() {
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
}
