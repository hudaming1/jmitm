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
import org.hum.wiredog.console.http.vo.wiredogPipeDetailVO;
import org.hum.wiredog.console.http.vo.wiredogPipeListQueryVO;
import org.hum.wiredog.console.http.vo.wiredogPipeListVO;
import org.hum.wiredog.proxy.facade.WtPipeContext;
import org.hum.wiredog.proxy.pipe.enumtype.PipeStatus;

public class PipeService {
	
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<wiredogPipeListVO> list(wiredogPipeListQueryVO queryVo) {
		Collection<WtPipeContext> all = PipeManagerInvokeChain.getAll();
		List<wiredogPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			if (queryVo.isActive() && item.getCurrentStatus() == PipeStatus.Closed) {
				 return ;
			}
			requestList.add(ConsoleHelper.parse2WtPipeListVO(item));
		});
		return requestList;
	}

	public wiredogPipeDetailVO getById(Long id) {
		WtPipeContext pipe = PipeManagerInvokeChain.getById(id);
		if (pipe == null) {
			return new wiredogPipeDetailVO();
		}
		wiredogPipeDetailVO detailVo = new wiredogPipeDetailVO();
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
