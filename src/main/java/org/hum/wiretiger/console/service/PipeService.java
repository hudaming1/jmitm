package org.hum.wiretiger.console.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.console.helper.HttpMessageUtil;
import org.hum.wiretiger.console.vo.WireTigerConnectionDetailVO;
import org.hum.wiretiger.console.vo.WireTigerConnectionListVO;
import org.hum.wiretiger.core.external.conmonitor.PipeMonitor;
import org.hum.wiretiger.core.handler.bean.Pipe;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeService {
	
	private PipeMonitor pipeMonitor = PipeMonitor.get();

	public List<WireTigerConnectionListVO> list() {
		Collection<Pipe> all = pipeMonitor.getAll();
		List<WireTigerConnectionListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			WireTigerConnectionListVO vo = new WireTigerConnectionListVO();
			vo.setRequestId(item.getId());
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

	public WireTigerConnectionDetailVO getById(int id) {
		Pipe pipe = pipeMonitor.getById(id);
		if (pipe == null) {
			return new WireTigerConnectionDetailVO();
		}
		WireTigerConnectionDetailVO detailVo = new WireTigerConnectionDetailVO();
		detailVo.setRequestString(HttpMessageUtil.appendRequest(new StringBuilder(), pipe.getRequest()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		detailVo.setResponseString(HttpMessageUtil.appendResponse(new StringBuilder(), pipe.getResponseList()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		return detailVo;
	}
}
