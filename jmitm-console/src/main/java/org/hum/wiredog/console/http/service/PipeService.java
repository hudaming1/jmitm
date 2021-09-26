package org.hum.wiredog.console.http.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiredog.console.common.chain.PipeManagerInvokeChain;
import org.hum.wiredog.console.http.helper.ConsoleHelper;
import org.hum.wiredog.console.http.vo.WiredogPipeDetailVO;
import org.hum.wiredog.console.http.vo.WiredogPipeListQueryVO;
import org.hum.wiredog.console.http.vo.WiredogPipeListVO;
import org.hum.wiredog.proxy.facade.PipeContext;
import org.hum.wiredog.proxy.pipe.enumtype.PipeStatus;

public class PipeService {
	
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WiredogPipeListVO> list(WiredogPipeListQueryVO queryVo) {
		Collection<PipeContext> all = PipeManagerInvokeChain.getAll();
		List<WiredogPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			if (queryVo.isActive() && item.getCurrentStatus() == PipeStatus.Closed) {
				 return ;
			}
			requestList.add(ConsoleHelper.parse2WtPipeListVO(item));
		});
		return requestList;
	}

	public WiredogPipeDetailVO getById(Long id) {
		PipeContext pipe = PipeManagerInvokeChain.getById(id);
		if (pipe == null) {
			return new WiredogPipeDetailVO();
		}
		WiredogPipeDetailVO detailVo = new WiredogPipeDetailVO();
		List<Map<String, String>> pipeEventMapList = new ArrayList<>();
		pipe.getEventList().forEach(item -> {
			Map<String, String> map = new HashMap<>();
			map.put("type", item.getType().toString());
			map.put("time", DATE_TIME_FORMATTER.format(new Date(item.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
			map.put("desc", item.getDesc());
			pipeEventMapList.add(map);
		});
		detailVo.setPipeEvent(pipeEventMapList );
		return detailVo;
	}
	
}
