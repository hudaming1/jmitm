package org.hum.wiretiger.core.pipe.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.Channel;

public class PipeHolder {

	private Pipe pipe = new Pipe();
	
	public PipeHolder(int id) {
		pipe.setId(id);
		pipe.addStatus(PipeStatus.Init);
	}
	
	public void registClient(Channel channel) {
		this.pipe.setSourceCtx(channel);
	}
	
	public void registServer(Channel channel) {
		this.pipe.setTargetCtx(channel);
	}
	 
	public void recordStatus(PipeStatus status) {
		this.pipe.addStatus(status);
	}
	
	public int getId() {
		return pipe.getId();
	}
	
	public Channel getClientChannel() {
		return pipe.getSourceCtx();
	}
	
	public Channel getServerChannel() {
		return pipe.getTargetCtx();
	}
	
	public String getUri() {
		return pipe.getRequest().uri();
	}
	
	public void setProtocol(Protocol protocol) {
		pipe.setProtocol(protocol);
	}

	public Protocol getProtocol() {
		return pipe.getProtocol();
	}

	public PipeStatus getCurrentStatus() {
		List<PipeStatus> status = new ArrayList<>(pipe.getStatusMap().keySet());
		Collections.sort(status, new Comparator<PipeStatus>() {
			@Override
			public int compare(PipeStatus o1, PipeStatus o2) {
				if (o1 == null) {
					return 1;
				} else if (o2 == null) {
					return -1;
				}
				return o1.getCode() < o2.getCode() ? 1 : -1;
			}
		});
		return status.get(0);
	}
	
}
