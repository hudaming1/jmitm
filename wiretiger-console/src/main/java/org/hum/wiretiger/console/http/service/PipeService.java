package org.hum.wiretiger.console.http.service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hum.wiretiger.console.common.util.HttpMessageUtil;
import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WtPipeDetailVO;
import org.hum.wiretiger.console.http.vo.WtPipeListQueryVO;
import org.hum.wiretiger.console.http.vo.WtPipeListVO;
import org.hum.wiretiger.core.pipe.PipeManager;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.util.internal.StringUtil;

public class PipeService {
	
	private PipeManager pipeMonitor = PipeManager.get();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WtPipeListVO> list(WtPipeListQueryVO queryVo) {
		Collection<PipeHolder> all = pipeMonitor.getAll();
		List<WtPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			if (queryVo.isActive() && item.getCurrentStatus() == PipeStatus.Closed) {
				 return ;
			}
			requestList.add(ConsoleHelper.parse2WtPipeListVO(item));
		});
		return requestList;
	}

	public WtPipeDetailVO getById(int id) {
		PipeHolder pipe = pipeMonitor.getById(id);
		if (pipe == null) {
			return new WtPipeDetailVO();
		}
		WtPipeDetailVO detailVo = new WtPipeDetailVO();
		if (pipe.getRequests() != null && !pipe.getRequests().isEmpty()) {
			detailVo.setRequestString(HttpMessageUtil.appendRequest(new StringBuilder(), pipe.getRequests()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		}
		if (pipe.getResponses() != null && !pipe.getResponses().isEmpty()) {
			detailVo.setResponseString(HttpMessageUtil.appendResponse(new StringBuilder(), pipe.getResponses()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		}
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
