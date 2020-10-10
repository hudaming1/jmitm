package org.hum.wiretiger.console.http.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.console.common.chain.PipeManagerInvokeChain;
import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerPipeDetailVO;
import org.hum.wiretiger.console.http.vo.WiretigerPipeListQueryVO;
import org.hum.wiretiger.console.http.vo.WiretigerPipeListVO;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;

public class PipeService {
	
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WiretigerPipeListVO> list(WiretigerPipeListQueryVO queryVo) {
		Collection<WtPipeContext> all = PipeManagerInvokeChain.getAll();
		List<WiretigerPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			if (queryVo.isActive() && item.getCurrentStatus() == PipeStatus.Closed) {
				 return ;
			}
			requestList.add(ConsoleHelper.parse2WtPipeListVO(item));
		});
		return requestList;
	}

	public WiretigerPipeDetailVO getById(Long id) {
		WtPipeContext pipe = PipeManagerInvokeChain.getById(id);
		if (pipe == null) {
			return new WiretigerPipeDetailVO();
		}
		WiretigerPipeDetailVO detailVo = new WiretigerPipeDetailVO();
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
