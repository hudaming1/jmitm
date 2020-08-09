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
import org.hum.wiretiger.console.vo.WireTigerConnectionDetailVO;
import org.hum.wiretiger.console.vo.WireTigerConnectionListVO;
import org.hum.wiretiger.core.pipe.PipeManager;
import org.hum.wiretiger.core.pipe.bean.Pipe;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PipeService {
	
	private PipeManager pipeMonitor = PipeManager.get();
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public List<WireTigerConnectionListVO> list() {
//		Collection<PipeHolder> all = pipeMonitor.getAll();
//		List<WireTigerConnectionListVO> requestList = new ArrayList<>();
//		all.forEach(item -> {
//			WireTigerConnectionListVO vo = new WireTigerConnectionListVO();
//			vo.setRequestId(item.getId());
//			vo.setUri(item.getRequest() == null ? "waitting.." : getPath(item.getRequest().uri()));
//			vo.setResponseCode((item.getResponseList() == null || item.getResponseList().isEmpty()) ? "pending.." : item.getResponseList().get(0).status().code() + "");
//			vo.setProtocol(Protocol.getEnum(item.getProtocol()).getDesc());
//			vo.setStatus(item.getStatus().toString());
//			requestList.add(vo);
//		});
//		return requestList;
		return null;
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
//		Pipe pipe = pipeMonitor.getById(id);
//		if (pipe == null) {
//			return new WireTigerConnectionDetailVO();
//		}
//		WireTigerConnectionDetailVO detailVo = new WireTigerConnectionDetailVO();
//		if (pipe.getRequest() != null) {
//			detailVo.setRequestString(HttpMessageUtil.appendRequest(new StringBuilder(), pipe.getRequest()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
//		}
//		if (pipe.getResponseList() != null && !pipe.getResponseList().isEmpty()) {
//			detailVo.setResponseString(HttpMessageUtil.appendResponse(new StringBuilder(), pipe.getResponseList()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
//		}
//		detailVo.setStatusTimeline(parseTimeLine(pipe.getStatusTimeline()));
//		return detailVo;
		return null;
	}
	
	private List<Map<String, String>> parseTimeLine(Map<Long, PipeStatus> pipeStatus) {
		if (pipeStatus == null || pipeStatus.isEmpty()) {
			return Collections.emptyList();
		}
		List<Map<String, String>> result = new ArrayList<>();
		for (Entry<Long, PipeStatus> entry : pipeStatus.entrySet()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("status", entry.getValue().toString());
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
