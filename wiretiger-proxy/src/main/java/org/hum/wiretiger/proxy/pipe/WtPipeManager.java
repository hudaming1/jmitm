package org.hum.wiretiger.proxy.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hudaming
 */
@Slf4j
public class WtPipeManager {

	private static final AtomicInteger counter = new AtomicInteger(1);
	private ConcurrentHashMap<Channel, WtPipeHolder> pipes4ClientChannel = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, WtPipeHolder> pipes4Id = new ConcurrentHashMap<>();
	
	private WtPipeManager() {
		log.info("ConnectMonitor started..");
	}

	private static class WtPipeManagerHodler {
		private static WtPipeManager instance = new WtPipeManager();
	}

	public static WtPipeManager get() {
		return WtPipeManagerHodler.instance;
	}

	public WtPipeHolder create(Channel clientChannel) {
		WtPipeHolder holder = new WtPipeHolder(counter.getAndIncrement());
		holder.registClient(clientChannel);
		holder.setName(clientChannel.remoteAddress().toString());
		pipes4Id.put(holder.getId(), holder);
		pipes4ClientChannel.put(clientChannel, holder);
		return holder;
	}
	
	public WtPipeHolder getById(Long id) {
		return pipes4Id.get(id);
	}
	
	public List<WtPipeHolder> getAll() {
		List<WtPipeHolder> list = new ArrayList<>();
		list.addAll(pipes4Id.values());
		// 按照Id顺序展示
		Collections.sort(list, new Comparator<WtPipeHolder>() {
			@Override
			public int compare(WtPipeHolder o1, WtPipeHolder o2) {
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
