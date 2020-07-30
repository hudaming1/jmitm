package org.hum.wiretiger.core.external.conmonitor;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接监听器
 * @author hudaming
 */
@Slf4j
public class ConnectMonitor {

	private ConcurrentHashMap<Channel, Long> connections = new ConcurrentHashMap<>();
	private Timer timer = new Timer();
	
	private ConnectMonitor() {
		timer.scheduleAtFixedRate(new SchedulePrinter(connections), new Date(), 5000L);
		log.info("ConnectMonitor started..");
	}

	private static class ConnectMonitorHodler {
		private static ConnectMonitor instance = new ConnectMonitor();
	}

	public static ConnectMonitor get() {
		return ConnectMonitorHodler.instance;
	}
	
	public boolean isExists(Channel channel) {
		return connections.containsKey(channel);
	}
	
	public void add(Channel channel) {
		connections.put(channel, System.currentTimeMillis());
	}
	
	public void remove(Channel channel) {
		connections.remove(channel);
	}
	
	public long get(Channel channel) {
		return connections.get(channel);
	}
	
	private class SchedulePrinter extends TimerTask {
		
		private ConcurrentHashMap<Channel, Long> connections;
		
		public SchedulePrinter(ConcurrentHashMap<Channel, Long> connections) {
			this.connections = connections;
		}

		@Override
		public void run() {
			log.info("connection.size=" + connections.size());
			for (Entry<Channel, Long> entry : connections.entrySet()) {
				log.info("con_status=" + entry.getKey().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).get());
			}
		}
	}
}
