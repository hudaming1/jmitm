package org.hum.wiretiger.api.proxy;

public interface EventListener {

	public void onConnect(WireTigerPipe pipe);

	public void onDisconnect(WireTigerPipe pipe);
	
	public void onPipeStatusChange(WireTigerPipe pipe);

	public void onError(WireTigerPipe pipe);
	
	public void onNewSession(WireTigerPipe pipe, WiretigerSession sessionReq);
	
	public void onSessionUpdate(WireTigerPipe pipe, WiretigerSession sessionResp);
}
