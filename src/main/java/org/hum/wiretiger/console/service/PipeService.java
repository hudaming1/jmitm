package org.hum.wiretiger.console.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.console.vo.WireTigerConnectionVO;
import org.hum.wiretiger.core.external.conmonitor.PipeMonitor;
import org.hum.wiretiger.core.handler.bean.Pipe;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeService {
	
	private PipeMonitor pipeMonitor = PipeMonitor.get();

	public List<WireTigerConnectionVO> list() {
		Collection<Pipe> all = pipeMonitor.getAll();
		List<WireTigerConnectionVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			WireTigerConnectionVO vo = new WireTigerConnectionVO();
			vo.setReqeustId(item.getId());
			vo.setUri(item.getRequest() == null ? "waitting.." : getPath(item.getRequest().uri()));
			vo.setResponseCode((item.getResponseList() == null || item.getResponseList().isEmpty()) ? "pending.." : item.getResponseList().get(0).status().code() + "");
			requestList.add(vo);
		});
		return requestList;
	}
	
	private String getPath(String uri) {
		try {
			return new URI(uri).getPath();
		} catch (URISyntaxException e) {
			log.warn("cann't parse uri=" + uri);
			return "parse error:" + uri;
		}
	}
}
