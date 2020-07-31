package org.hum.wiretiger.core.external.conmonitor;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.wiretiger.core.handler.bean.Pipe;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接监听器
 * @author hudaming
 */
@Slf4j
public class PipeMonitor {

	private ConcurrentHashMap<Channel, Pipe> connections = new ConcurrentHashMap<>();
	private Timer timer = new Timer();
	
	private PipeMonitor() {
		timer.scheduleAtFixedRate(new SchedulePrinter(connections), new Date(), 5000L);
		log.info("ConnectMonitor started..");
	}

	private static class ConnectMonitorHodler {
		private static PipeMonitor instance = new PipeMonitor();
	}

	public static PipeMonitor get() {
		return ConnectMonitorHodler.instance;
	}
	
	public boolean isExists(Channel channel) {
		return connections.containsKey(channel);
	}
	
	public void add(Pipe pipe) {
		connections.put(pipe.getSourceCtx().channel(), pipe);
	}
	
	public void remove(Channel channel) {
		connections.remove(channel);
	}
	
	public Pipe get(Channel channel) {
		return connections.get(channel);
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
				log.info("request=" + entry.getValue().getRequest().getClass());
			}
		}
	}
}
