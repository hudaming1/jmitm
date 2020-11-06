package org.hum.wiretiger.console.http.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.common.util.DateUtils;
import org.hum.wiretiger.console.common.WtConsoleConstant;
import org.hum.wiretiger.console.common.WtSession;
import org.hum.wiretiger.console.common.chain.SessionManagerInvokeChain;
import org.hum.wiretiger.console.common.codec.IContentCodec;
import org.hum.wiretiger.console.common.codec.impl.CodecFactory;
import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerSessionDetailVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListQueryVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;
import org.hum.wiretiger.proxy.util.HttpRequestCodec;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionService {
	
	private final static WiretigerSessionListQueryVO QUERY = new WiretigerSessionListQueryVO();
	
	public List<WiretigerSessionListVO> list(WiretigerSessionListQueryVO query) {
		// HTTP和Websocket共用一个Query
		changeCondition(query);
		
		List<WiretigerSessionListVO> sessionList = new ArrayList<>();
		SessionManagerInvokeChain.getAll().forEach(session -> {
			if (!isMatch(session)) {
				return ;
			}
			sessionList.add(ConsoleHelper.parse2WtSessionListVO(session));
		});
		return sessionList;
	}

	private static boolean isShowWireTigerConsoleSession(WtSession session) {
		if (!session.getRequest().headers().contains(WtConsoleConstant.WIRETIGER_CONSOLE_IDEN)) {
			return true;
		}
		return ConsoleServer.WiretigerConsoleConfig.isShowConsoleSession();
	}
	
	public boolean clearAll() {
		SessionManagerInvokeChain.clearAll();
		log.info("session clear");
		return true;
	}
	
	private void changeCondition(WiretigerSessionListQueryVO newCondition) {
		QUERY.setHost(newCondition.getHost());
		QUERY.setKeyword(newCondition.getKeyword());
		QUERY.setPipeId(newCondition.getPipeId());
	}
	
	public static boolean isMatch(WtSession session) {
		if (!isShowWireTigerConsoleSession(session)) {
			return false;
		} else if (QUERY == null || QUERY.isEmpty()) {
			return true;
		} else if (QUERY.getPipeId() != null && !QUERY.getPipeId().equals(session.getPipeId())) {
			return false;
		} else if (QUERY.getKeyword() != null && !QUERY.getKeyword().isEmpty() 
				&& !session.getRequest().uri().contains(QUERY.getKeyword())) {
			return false;
		} else if (QUERY.getHost() != null && !QUERY.getHost().isEmpty() &&
				// 没有header直接认为不匹配
				(session.getRequest().headers() == null || session.getRequest().headers().isEmpty()) ||
				// header中没有Host属性，认为不匹配
				(session.getRequest().headers() != null && !session.getRequest().headers().contains("Host")) ||
				// header中的Host不包含keyword，认为不匹配
				(!session.getRequest().headers().get("Host").contains(QUERY.getHost()))
				) {
			return false;
		} 
		return true;
	}
	
	public WtSession getWtSessionById(Long id) {
		return SessionManagerInvokeChain.getById(id);
	}
	
	public WiretigerSessionDetailVO getById(Long id) {
		try {
			WiretigerSessionDetailVO detailVo = new WiretigerSessionDetailVO();
			WtSession simpleSession = SessionManagerInvokeChain.getById(id);
			detailVo.setRequestHeader(HttpRequestCodec.encode(simpleSession.getRequest(), HttpConstant.RETURN_LINE));
			detailVo.setCreateTime(DateUtils.formatTime(new Date(simpleSession.getRequestTime())));
			if (simpleSession.getRequestBytes() != null) {
				detailVo.setRequestBody4Source(Arrays.toString(simpleSession.getRequestBytes()));
				detailVo.setRequestBody4Parsed(new String(simpleSession.getRequestBytes()));
			} else {
				detailVo.setRequestBody4Source("");
				detailVo.setRequestBody4Parsed("");
			}
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
				detailVo.setResponseBody4Source("No Response Body");
				detailVo.setResponseBody4Parsed("No Response Body");
			}
			return detailVo;
		} catch (Throwable ce) {
			log.error("getById.error, id={}", id, ce);
			return null;
		}
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
