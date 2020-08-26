package org.hum.wiretiger.console.http.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WtPipeDetailVO;
import org.hum.wiretiger.console.http.vo.WtPipeListQueryVO;
import org.hum.wiretiger.console.http.vo.WtPipeListVO;
import org.hum.wiretiger.proxy.facade.enumtype.WiretigerPipeStatus;
import org.hum.wiretiger.proxy.facade.lite.WiretigerFullPipe;
import org.hum.wiretiger.proxy.facade.lite.WiretigerPipeManagerLite;

public class PipeService {
	
	private WiretigerPipeManagerLite pipeMgrLite = WiretigerPipeManagerLite.get();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WtPipeListVO> list(WtPipeListQueryVO queryVo) {
		Collection<WiretigerFullPipe> all = pipeMgrLite.getAll();
		List<WtPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			if (queryVo.isActive() && item.getStatus() == WiretigerPipeStatus.Closed) {
				 return ;
			}
			requestList.add(ConsoleHelper.parse2WtPipeListVO(item));
		});
		return requestList;
	}

	public WtPipeDetailVO getById(String id) {
//		WiretigerFullPipe pipe = pipeMgrLite.getById(id);
//		if (pipe == null) {
//			return new WtPipeDetailVO();
//		}
		WtPipeDetailVO detailVo = new WtPipeDetailVO();
//		List<Map<String, String>> pipeEventMapList = new ArrayList<>();
//		pipe.getEventList().forEach(item -> {
//			Map<String, String> map = new HashMap<>();
//			map.put("type", item.getType().toString());
//			map.put("time", DATE_TIME_FORMATTER.format(new Date(item.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
//			map.put("desc", item.getDesc());
//			pipeEventMapList.add(map);
//		});
//		detailVo.setPipeEvent(pipeEventMapList );
		return detailVo;
	}
	
}
