package org.hum.wiretiger.core.external.conmonitor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.extern.slf4j.Slf4j;

/**
 * 连接监听器
 * @author hudaming
 */
@Slf4j
public class ConnectMonitor {

	private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();
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
	
	public void add(String host, int port) {
		connections.add(new Connection(host, port));
	}
	
	public void remove(String host, int port) {
		connections.remove(new Connection(host, port));
	}
	
	private class SchedulePrinter extends TimerTask {
		
		private CopyOnWriteArrayList<Connection> connections;
		
		public SchedulePrinter(CopyOnWriteArrayList<Connection> connections) {
			this.connections = connections;
		}

		@Override
		public void run() {
			log.info("connection.size=" + connections.size());
		}
	}
}
