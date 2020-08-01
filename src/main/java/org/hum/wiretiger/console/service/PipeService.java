package org.hum.wiretiger.console.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.console.vo.RequestVO;
import org.hum.wiretiger.core.external.conmonitor.PipeMonitor;
import org.hum.wiretiger.core.handler.bean.Pipe;

public class PipeService {
	
	private PipeMonitor pipeMonitor = PipeMonitor.get();

	public List<RequestVO> list() {
		Collection<Pipe> all = pipeMonitor.getAll();
		List<RequestVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			RequestVO vo = new RequestVO();
			vo.setReqeustId(item.getId());
			vo.setUri(item.getRequest().uri());
			requestList.add(vo);
		});
		return requestList;
	}
}
