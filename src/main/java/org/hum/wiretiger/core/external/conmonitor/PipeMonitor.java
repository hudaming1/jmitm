package org.hum.wiretiger.core.external.conmonitor;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.wiretiger.core.handler.bean.Pipe;

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
	private Timer timer = new Timer();
	
	private PipeMonitor() {
		timer.scheduleAtFixedRate(new SchedulePrinter(pipes4Channel), new Date(), 5000L);
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
	
	public Collection<Pipe> getAll() {
		return Collections.unmodifiableCollection(pipes4Channel.values());
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
