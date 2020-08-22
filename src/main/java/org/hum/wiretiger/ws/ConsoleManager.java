package org.hum.wiretiger.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.wiretiger.ws.wrapper.ConsoleChannelWrapper;

import io.netty.channel.Channel;

public class ConsoleManager {

	private static final Map<String, ConsoleChannelWrapper> ConsoleChannelMap = new ConcurrentHashMap<>();
	
	private static class ConsoleManagerHolder {
		private static final ConsoleManager instance = new ConsoleManager();
	}
	
	public static ConsoleManager get() {
		return ConsoleManagerHolder.instance;
	}
	
	private ConsoleManager() {
	}
	
	public void regist(String id, Channel channel) {
		ConsoleChannelMap.put(id, new ConsoleChannelWrapper(channel));
	}
	
	public void logout(String id) {
		ConsoleChannelMap.remove(id);
	}
	
	public ConsoleChannelWrapper get(String id) {
		return ConsoleChannelMap.get(id);
	}
}
