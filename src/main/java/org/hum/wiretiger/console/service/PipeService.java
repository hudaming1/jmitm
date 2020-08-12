package org.hum.wiretiger.console.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.console.helper.HttpMessageUtil;
import org.hum.wiretiger.console.vo.WiretigerPipeDetailVO;
import org.hum.wiretiger.console.vo.WiretigerPipeListVO;
import org.hum.wiretiger.core.pipe.PipeManager;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeService {
	
	private PipeManager pipeMonitor = PipeManager.get();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WiretigerPipeListVO> list() {
		Collection<PipeHolder> all = pipeMonitor.getAll();
		List<WiretigerPipeListVO> requestList = new ArrayList<>();
		all.forEach(item -> {
			WiretigerPipeListVO vo = new WiretigerPipeListVO();
			vo.setRequestId(item.getId());
			vo.setUri(item.getUri() == null ? "waitting.." : getPath(item.getUri()));
			vo.setResponseCode((item.getResponses() == null || item.getResponses().isEmpty()) ? "pending.." : item.getResponses().get(0).status().code() + "");
			vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
			vo.setStatus(item.getCurrentStatus().getDesc());
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

	public WiretigerPipeDetailVO getById(int id) {
		PipeHolder pipe = pipeMonitor.getById(id);
		if (pipe == null) {
			return new WiretigerPipeDetailVO();
		}
		WiretigerPipeDetailVO detailVo = new WiretigerPipeDetailVO();
		if (pipe.getRequests() != null && !pipe.getRequests().isEmpty()) {
			detailVo.setRequestString(HttpMessageUtil.appendRequest(new StringBuilder(), pipe.getRequests()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		}
		if (pipe.getResponses() != null && !pipe.getResponses().isEmpty()) {
			detailVo.setResponseString(HttpMessageUtil.appendResponse(new StringBuilder(), pipe.getResponses()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		}
		detailVo.setStatusTimeline(parseTimeLine(pipe.getStatusTimeline()));
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
	
	private List<Map<String, String>> parseTimeLine(Map<Long, PipeStatus> pipeStatus) {
		if (pipeStatus == null || pipeStatus.isEmpty()) {
			return Collections.emptyList();
		}
		List<Map<String, String>> result = new ArrayList<>();
		for (Entry<Long, PipeStatus> entry : pipeStatus.entrySet()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("status", entry.getValue().getDesc());
			map.put("time", DATE_TIME_FORMATTER.format(new Date(entry.getKey()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
			result.add(map);
		}
		Collections.sort(result, new Comparator<Map<String, String>>() {
			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				}
				return o1.get("time").compareTo(o2.get("time"));
			}
		});
		return result;
	}
}
