package org.hum.wiretiger.console.http.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerSessionDetailVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListQueryVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.proxy.facade.lite.WiretigerFullSession;
import org.hum.wiretiger.proxy.facade.lite.WiretigerSessionManagerLite;

public class SessionService {
	
	private WiretigerSessionManagerLite sessionManagerLite = WiretigerSessionManagerLite.get();

	public List<WiretigerSessionListVO> list(WiretigerSessionListQueryVO query) {
		List<WiretigerSessionListVO> connList = new ArrayList<>();
		sessionManagerLite.getAll().forEach(session -> {
			if (!isMatch(query, session)) {
				return ;
			}
			connList.add(ConsoleHelper.parse2WtSessionListVO(session));
		});
		return connList;
	}
	
	private boolean isMatch(WiretigerSessionListQueryVO condition, WiretigerFullSession req) {
		if (condition == null || condition.isEmpty()) {
			return true;
		} else if (condition.getPipeId() != null && condition.getPipeId().equals(req.getPipeId())) {
			return true;
		}
		return false;
	}
	
	public WiretigerSessionDetailVO getById(Long id) throws IOException {
		WiretigerSessionDetailVO detailVo = new WiretigerSessionDetailVO();
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
