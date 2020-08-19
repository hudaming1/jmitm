package org.hum.wiretiger.console.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hum.wiretiger.console.helper.HttpMessageUtil;
import org.hum.wiretiger.console.vo.WtSessionDetailVO;
import org.hum.wiretiger.console.vo.WtSessionListQueryVO;
import org.hum.wiretiger.console.vo.WtSessionListVO;
import org.hum.wiretiger.core.session.SessionManager;
import org.hum.wiretiger.core.session.bean.WtSession;

import io.netty.util.internal.StringUtil;

public class SessionService {

	public List<WtSessionListVO> list(WtSessionListQueryVO query) {
		List<WtSessionListVO> connList = new ArrayList<>();
		SessionManager.get().getList().forEach(session -> {
			if (!isMatch(query, session)) {
				return ;
			}
			WtSessionListVO conVo = new WtSessionListVO();
			conVo.setSessionId(session.getId());
			conVo.setUri(session.getRequest().uri());
			conVo.setResponseCode(session.getResponse().status().code() + "");
			connList.add(conVo);
		});
		return connList;
	}
	
	private boolean isMatch(WtSessionListQueryVO condition, WtSession req) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (condition.getPipeId() != null && condition.getPipeId().equals(req.getPipeId())) {
			return true;
		}
		return false;
	}
	
	public WtSessionDetailVO getById(Long id) throws IOException {
		WtSession session = SessionManager.get().getRequest(id);
		WtSessionDetailVO detailVo = new WtSessionDetailVO();
		detailVo.setRequest(HttpMessageUtil.appendRequest(new StringBuilder(), session.getRequest()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		detailVo.setResponseHeader(HttpMessageUtil.appendResponse(new StringBuilder(), session.getResponse()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		if (session.getResponseBytes() != null && session.getResponseBytes().length > 0) {
			detailVo.setResponseBody4Source(Arrays.toString(session.getResponseBytes()));
			// TODO 判断response的content-type
			detailVo.setResponseBody4Parsed(HttpMessageUtil.unescape(new String(HttpMessageUtil.ungzip(session.getResponseBytes()))));
		} else {
			detailVo.setResponseBody4Source("No Response..");
			detailVo.setResponseBody4Parsed("No Response..");
		}
		return detailVo;
	}
	
}
