package org.hum.wiretiger.console.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hum.wiretiger.console.helper.HttpMessageUtil;
import org.hum.wiretiger.console.vo.WtRequestDetailVO;
import org.hum.wiretiger.console.vo.WtRequestListQueryVO;
import org.hum.wiretiger.console.vo.WtRequestListVO;
import org.hum.wiretiger.core.request.RequestManager;
import org.hum.wiretiger.core.request.bean.WtRequest;

import io.netty.util.internal.StringUtil;

public class RequestService {

	public List<WtRequestListVO> list(WtRequestListQueryVO query) {
		List<WtRequestListVO> connList = new ArrayList<>();
		RequestManager.get().getList().forEach(request -> {
			if (!isMatch(query, request)) {
				return ;
			}
			WtRequestListVO conVo = new WtRequestListVO();
			conVo.setRequestId(request.getId());
			conVo.setUri(request.getRequest().uri());
			conVo.setResponseCode(request.getResponse().status().code() + "");
			connList.add(conVo);
		});
		return connList;
	}
	
	private boolean isMatch(WtRequestListQueryVO condition, WtRequest req) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (condition.getPipeId() != null && condition.getPipeId().equals(req.getPipeId())) {
			return true;
		}
		return false;
	}
	
	public WtRequestDetailVO getById(Long id) {
		WtRequest connection = RequestManager.get().getRequest(id);
		WtRequestDetailVO detailVo = new WtRequestDetailVO();
		detailVo.setRequest(HttpMessageUtil.appendRequest(new StringBuilder(), connection.getRequest()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		detailVo.setResponse(HttpMessageUtil.appendResponse(new StringBuilder(), connection.getResponse()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		if (connection.getResponseBytes() != null && connection.getResponseBytes().length > 0) {
			detailVo.setResponse(detailVo.getResponse() + "<br /><br />" + Arrays.toString(connection.getResponseBytes()));
		}
		return detailVo;
	}

}
