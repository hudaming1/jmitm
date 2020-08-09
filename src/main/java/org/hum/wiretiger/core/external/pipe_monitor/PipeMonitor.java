package org.hum.wiretiger.core.external.pipe_monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接监听器
 * @author hudaming
 */
@Slf4j
public class PipeMonitor {

	private ConcurrentHashMap<Channel, Pipe> pipes4Channel = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Pipe> pipes4Id = new ConcurrentHashMap<>();
	
	private PipeMonitor() {
		log.info("ConnectMonitor started..");
	}

	private static class ConnectMonitorHodler {
		private static PipeMonitor instance = new PipeMonitor();
	}

	public static PipeMonitor get() {
		return ConnectMonitorHodler.instance;
	}
	
	public boolean isExists(Channel channel) {
		return pipes4Channel.containsKey(channel);
	}
	
	public void add(Pipe pipe) {
		pipes4Channel.put(pipe.getSourceCtx().channel(), pipe);
		pipes4Id.put(pipe.getId(), pipe);
	}
	
	public void remove(Channel channel) {
		pipes4Channel.remove(channel);
	}
	
	/**
	 * PipeMonotir一切以clientChannel为标识
	 * @param channel
	 * @return
	 */
	public Pipe get(Channel channel) {
		return pipes4Channel.get(channel);
	}
	
	public Pipe getById(Integer id) {
		return pipes4Id.get(id);
	}
	
	public List<Pipe> getAll() {
		List<Pipe> list = new ArrayList<>();
		list.addAll(pipes4Channel.values());
		// 按照Id顺序展示
		Collections.sort(list, new Comparator<Pipe>() {
			@Override
			public int compare(Pipe o1, Pipe o2) {
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
