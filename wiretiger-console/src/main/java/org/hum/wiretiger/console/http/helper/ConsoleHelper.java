package org.hum.wiretiger.console.http.helper;

import org.hum.wiretiger.console.http.vo.WiretigerPipeListVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;
import org.hum.wiretiger.proxy.facade.event.WiretigerSession;
import org.hum.wiretiger.proxy.facade.lite.WiretigerFullPipe;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

public class ConsoleHelper {

	public static WiretigerPipeListVO parse2WtPipeListVO(WiretigerPipe item) {
		WiretigerPipeListVO vo = new WiretigerPipeListVO();
		vo.setPipeId(item.getPipeId());
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
//		TODO
//		vo.setStatus(item.getStatus().name().getDesc());
		return vo;
	}

	public static WiretigerPipeListVO parse2WtPipeListVO(WiretigerFullPipe item) {
		
		String source = item.getSourceHost() + ":" + item.getSourcePort();
		
		String target = "?";
		if (item.getTargetHost() != null && item.getTargetPort() != null) {
			target = item.getTargetHost() + ":" + item.getTargetPort();
		}
		
		WiretigerPipeListVO vo = new WiretigerPipeListVO();
		vo.setPipeId(item.getPipeId());
		vo.setName(source + "->" + target);
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getStatus().getDesc());
		return vo;
	}

	public static WiretigerSessionListVO parse2WtSessionListVO(WiretigerSession session) {
		WiretigerSessionListVO conVo = new WiretigerSessionListVO();
		conVo.setSessionId(session.getSessionId());
		conVo.setUri(session.getUri());
		conVo.setResponseCode(session.getRespStatus());
		return conVo;
	}
}