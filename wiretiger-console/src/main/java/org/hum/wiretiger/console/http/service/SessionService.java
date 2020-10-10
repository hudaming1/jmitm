package org.hum.wiretiger.console.http.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.console.common.WtSessionManager;
import org.hum.wiretiger.console.common.codec.IContentCodec;
import org.hum.wiretiger.console.common.codec.impl.CodecFactory;
import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerSessionDetailVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListQueryVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.proxy.session.bean.WtSession;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;

import io.netty.handler.codec.http.HttpHeaders;

public class SessionService {
	
	private WtSessionManager sessionManagerLite = WtSessionManager.get();

	public List<WiretigerSessionListVO> list(WiretigerSessionListQueryVO query) {
		List<WiretigerSessionListVO> sessionList = new ArrayList<>();
		sessionManagerLite.getAll().forEach(session -> {
			if (!isMatch(query, session)) {
				return ;
			}
			sessionList.add(ConsoleHelper.parse2WtSessionListVO(session));
		});
		return sessionList;
	}
	
	private boolean isMatch(WiretigerSessionListQueryVO condition, WtSession session) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (condition.getPipeId() != null && condition.getPipeId().equals(session.getPipeId())) {
			return true;
		} else if (condition.getKeyword() != null && !condition.getKeyword().isEmpty() && session.getRequest().uri().contains(condition.getKeyword())) {
			return true;
		}
		return false;
	}
	
	public WiretigerSessionDetailVO getById(Long id) throws IOException {
		WiretigerSessionDetailVO detailVo = new WiretigerSessionDetailVO();
		WtSession simpleSession = sessionManagerLite.getById(id);
		detailVo.setRequest(convert2RequestString(simpleSession));
		detailVo.setResponseHeader(convert2RepsonseHeader(simpleSession));
		detailVo.setPipeId(simpleSession.getPipeId());
		
		if (simpleSession.getResponseBytes() != null && simpleSession.getResponseBytes().length > 0) {
			HttpHeaders headers = simpleSession.getResponse().headers();
			byte[] respBytes = simpleSession.getResponseBytes();
			// 是否需要解压
			if (headers.contains(HttpConstant.ContentEncoding)) {
				respBytes = decompress(respBytes, headers.get(HttpConstant.ContentEncoding));
			}
			detailVo.setResponseBody4Source(Arrays.toString(respBytes));
			// 是否可解析
			if (HttpMessageUtil.isSupportParseString(headers.get(HttpConstant.ContentType))) {
				detailVo.setResponseBody4Parsed(HttpMessageUtil.unescape(new String(respBytes)));
			} else {
				detailVo.setResponseBody4Parsed("unsupport parsed type:" + headers.get(HttpConstant.ContentType));
			}
		} else {
			detailVo.setResponseBody4Source("No Response..");
			detailVo.setResponseBody4Parsed("No Response..");
		}
		return detailVo;
	}
	
	private String convert2RequestString(WtSession session) {
		StringBuilder request = new StringBuilder(session.getRequest().method().name() + " " + session.getRequest().uri() + " " + session.getRequest().protocolVersion()).append(HttpConstant.HTML_NEWLINE);
		for (Entry<String, String> header : session.getRequest().headers()) {
			request.append(header.getKey() + " : " + header.getValue()).append(HttpConstant.HTML_NEWLINE);
		}
		if (session.getRequest().content() != null) {
			// TODO
		}
		return request.toString();
	}
	
	private String convert2RepsonseHeader(WtSession session) {
		if (session.getResponse() == null) {
			return "";
		}
		StringBuilder headerString = new StringBuilder();
		for (Entry<String, String> header : session.getResponse().headers()) {
			headerString.append(header.getKey() + " : " + header.getValue()).append(HttpConstant.HTML_NEWLINE);
		}
		return headerString.toString();
	}
	
	private byte[] decompress(byte[] bytes, String type) throws IOException {
		IContentCodec contentCodec = CodecFactory.create(type);
		if (contentCodec != null) {
			return contentCodec.decompress(bytes);
		} else {
			return bytes;
		}
	}
}
