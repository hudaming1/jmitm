package org.hum.wiretiger.proxy.facade.event;

public interface EventListener {

	public void onConnect(WiretigerPipe pipe);

	public void onDisconnect(WiretigerPipe pipe);
	
	public void onPipeStatusChange(WiretigerPipe pipe);

	public void onError(WiretigerPipe pipe);
	
	public void onNewSession(WiretigerPipe pipe, WiretigerSession sessionReq);
	
	public void onSessionUpdate(WiretigerPipe pipe, WiretigerSession sessionResp);
}
