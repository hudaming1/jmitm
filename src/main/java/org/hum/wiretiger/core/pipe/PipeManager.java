package org.hum.wiretiger.core.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hudaming
 */
@Slf4j
public class PipeManager {

	private static final AtomicInteger counter = new AtomicInteger(1);
	private ConcurrentHashMap<Channel, PipeHolder> pipes4ClientChannel = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, PipeHolder> pipes4Id = new ConcurrentHashMap<>();
	
	private PipeManager() {
		log.info("ConnectMonitor started..");
	}

	private static class ConnectMonitorHodler {
		private static PipeManager instance = new PipeManager();
	}

	public static PipeManager get() {
		return ConnectMonitorHodler.instance;
	}

	public PipeHolder create(Channel clientChannel) {
		PipeHolder holder = new PipeHolder(counter.getAndIncrement());
		holder.registClient(clientChannel);
		holder.setName(clientChannel.remoteAddress().toString());
		pipes4Id.put(holder.getId(), holder);
		pipes4ClientChannel.put(clientChannel, holder);
		return holder;
	}
	
	public PipeHolder getById(Integer id) {
		return pipes4Id.get(id);
	}
	
	public List<PipeHolder> getAll() {
		List<PipeHolder> list = new ArrayList<>();
		list.addAll(pipes4Id.values());
		// 按照Id顺序展示
		Collections.sort(list, new Comparator<PipeHolder>() {
			@Override
			public int compare(PipeHolder o1, PipeHolder o2) {
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
