package org.hum.wiretiger.core.pipe.event;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

public interface EventListener {

	public void onConnect(PipeHolder pipe);

	public void onDisconnect(PipeHolder pipe);

	public void onError(PipeHolder pipe);
}
