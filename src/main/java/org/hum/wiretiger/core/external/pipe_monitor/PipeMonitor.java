package org.hum.wiretiger.core.external.pipe_monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接监听器
 * @author hudaming
 */
@Slf4j
@SuppressWarnings("unused")
public class PipeMonitor {

	private ConcurrentHashMap<Channel, Pipe> pipes4Channel = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Pipe> pipes4Id = new ConcurrentHashMap<>();
	private Timer timer = new Timer();
	
	private PipeMonitor() {
		// timer.scheduleAtFixedRate(new SchedulePrinter(pipes4Channel), new Date(), 5000L);
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
	
	private class SchedulePrinter extends TimerTask {
		
		private ConcurrentHashMap<Channel, Pipe> connections;
		
		public SchedulePrinter(ConcurrentHashMap<Channel, Pipe> connections) {
			this.connections = connections;
		}

		@Override
		public void run() {
			log.info("connection.size=" + connections.size());
			for (Entry<Channel, Pipe> entry : connections.entrySet()) {
				log.info("con_status=" + entry.getValue().getStatus() + ", alived times=" + (System.currentTimeMillis() - entry.getValue().getBirthday()) + "ms");
				if (entry.getValue().getRequest() != null) {
					log.info("request=" + entry.getValue().getRequest().getClass());
				}
			}
		}
	}
}
