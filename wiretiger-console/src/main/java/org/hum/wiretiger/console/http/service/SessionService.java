package org.hum.wiretiger.console.http.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hum.wiretiger.console.common.HttpConstant;
import org.hum.wiretiger.console.common.codec.IContentCodec;
import org.hum.wiretiger.console.common.codec.impl.CodecFactory;
import org.hum.wiretiger.console.common.util.HttpMessageUtil;
import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WtSessionDetailVO;
import org.hum.wiretiger.console.http.vo.WtSessionListQueryVO;
import org.hum.wiretiger.console.http.vo.WtSessionListVO;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.StringUtil;

public class SessionService {

	public List<WtSessionListVO> list(WtSessionListQueryVO query) {
		List<WtSessionListVO> connList = new ArrayList<>();
//		SessionManager.get().getList().forEach(session -> {
//			if (!isMatch(query, session)) {
//				return ;
//			}
//			connList.add(ConsoleHelper.parse2WtSessionListVO(session));
//		});
		return connList;
	}
//	
//	private boolean isMatch(WtSessionListQueryVO condition, WtSession req) {
//		if (condition == null || condition.isEmpty()) {
//			return true;
//		} else if (condition.getPipeId() != null && condition.getPipeId().equals(req.getPipeId())) {
//			return true;
//		}
//		return false;
//	}
	
	public WtSessionDetailVO getById(Long id) throws IOException {
		WtSessionDetailVO detailVo = new WtSessionDetailVO();
//		WtSession session = SessionManager.get().getRequest(id);
//		detailVo.setRequest(HttpMessageUtil.appendRequest(new StringBuilder(), session.getRequest()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
//		detailVo.setResponseHeader(HttpMessageUtil.appendResponse(new StringBuilder(), session.getResponse()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
//		if (session.getResponseBytes() != null && session.getResponseBytes().length > 0) {
//			HttpHeaders headers = session.getResponse().headers();
//			detailVo.setResponseBody4Source(Arrays.toString(session.getResponseBytes()));
//			byte[] respBytes = session.getResponseBytes();
//			// 是否需要解压
//			if (headers.contains(HttpConstant.ContentEncoding)) {
//				IContentCodec contentCodec = CodecFactory.create(headers.get(HttpConstant.ContentEncoding));
//				if (contentCodec != null) {
//					respBytes = contentCodec.decompress(respBytes);
//					detailVo.setResponseBody4Source(Arrays.toString(respBytes));
//				}
//			} 
//			// 是否支持转成字符串
//			if (HttpMessageUtil.isSupportParseString(headers.get(HttpConstant.ContentType))) {
//				detailVo.setResponseBody4Parsed(HttpMessageUtil.unescape(new String(respBytes)));
//			} 
//		} else {
//			detailVo.setResponseBody4Source("No Response..");
//			detailVo.setResponseBody4Parsed("No Response..");
//		}
		return detailVo;
	}
	
}
