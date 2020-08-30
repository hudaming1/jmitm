package org.hum.wiretiger.console.http.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerPipeDetailVO;
import org.hum.wiretiger.console.http.vo.WiretigerPipeListQueryVO;
import org.hum.wiretiger.console.http.vo.WiretigerPipeListVO;
import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.proxy.facade.lite.WiretigerFullPipe;
import org.hum.wiretiger.proxy.facade.lite.WiretigerPipeManagerLite;

public class PipeService {
	
	private WiretigerPipeManagerLite pipeMgrLite = WiretigerPipeManagerLite.get();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WiretigerPipeListVO> list(WiretigerPipeListQueryVO queryVo) {
		Collection<WiretigerFullPipe> all = pipeMgrLite.getAll();
		List<WiretigerPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			if (queryVo.isActive() && item.getStatus() == WiretigerPipeStatus.Closed) {
				 return ;
			}
			requestList.add(ConsoleHelper.parse2WtPipeListVO(item));
		});
		return requestList;
	}

	public WiretigerPipeDetailVO getById(Long id) {
		WiretigerFullPipe pipe = pipeMgrLite.getById(id);
		if (pipe == null) {
			return new WiretigerPipeDetailVO();
		}
		WiretigerPipeDetailVO detailVo = new WiretigerPipeDetailVO();
		List<Map<String, String>> pipeEventMapList = new ArrayList<>();
		pipe.getEvents().forEach(item -> {
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
