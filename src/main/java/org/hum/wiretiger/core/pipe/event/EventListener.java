package org.hum.wiretiger.core.pipe.event;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface EventListener {

	public void onConnect(PipeHolder pipe);

	public void onDisconnect(PipeHolder pipe);
	
	public void onPipeStatusChange(PipeHolder pipe);

	public void onError(PipeHolder pipe);
	
	public void onNewSession(PipeHolder pipe, DefaultHttpRequest sessionReq);
	
	public void onSessionUpdate(PipeHolder pipe, FullHttpResponse sessionResp);
}
