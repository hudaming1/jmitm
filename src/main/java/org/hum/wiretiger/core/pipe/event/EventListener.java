package org.hum.wiretiger.core.pipe.event;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.session.bean.WtSession;

public interface EventListener {

	public void onConnect(PipeHolder pipe);

	public void onDisconnect(PipeHolder pipe);
	
	public void onPipeStatusChange(PipeHolder pipe);

	public void onError(PipeHolder pipe);
	
	public void onNewSession(PipeHolder pipe, WtSession sessionReq);
	
	public void onSessionUpdate(PipeHolder pipe, WtSession sessionResp);
}
