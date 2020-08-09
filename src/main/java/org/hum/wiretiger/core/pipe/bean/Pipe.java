package org.hum.wiretiger.core.pipe.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Data;

@Data
public class Pipe {
	
	// client->proxy
	private Channel sourceCtx;
	// proxy->server
	private Channel targetCtx;
	// request
	private DefaultHttpRequest request;
	// responseList
	private List<FullHttpResponse> responseList = new ArrayList<FullHttpResponse>();
	// pipe status
	private Map<PipeStatus, Long> statusMap = new HashMap<>();
	// create time
	private long birthday;
	// pipeId
	private int id;
	/**
	 * 1-http; 2-https
	 * {@link Protocol}
	 */
	private int protocol;
	
	public void addStatus(PipeStatus status) {
		this.statusMap.put(status,System.currentTimeMillis());
	}
}
