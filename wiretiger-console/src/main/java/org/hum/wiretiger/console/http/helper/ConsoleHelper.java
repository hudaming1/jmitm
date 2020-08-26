package org.hum.wiretiger.console.http.helper;

import org.hum.wiretiger.console.http.vo.WtPipeListVO;
import org.hum.wiretiger.console.http.vo.WtSessionListVO;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;
import org.hum.wiretiger.proxy.facade.event.WiretigerSession;
import org.hum.wiretiger.proxy.facade.lite.WiretigerFullPipe;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

public class ConsoleHelper {

	public static WtPipeListVO parse2WtPipeListVO(WiretigerPipe item) {
		WtPipeListVO vo = new WtPipeListVO();
		vo.setPipeId(item.getPipeId());
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
//		TODO
//		vo.setStatus(item.getStatus().name().getDesc());
		return vo;
	}

	public static WtPipeListVO parse2WtPipeListVO(WiretigerFullPipe item) {
		WtPipeListVO vo = new WtPipeListVO();
		vo.setPipeId(item.getPipeId());
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
//		TODO
//		vo.setStatus(item.getStatus().name().getDesc());
		return vo;
	}

	public static WtSessionListVO parse2WtSessionListVO(WiretigerSession session) {
		WtSessionListVO conVo = new WtSessionListVO();
		conVo.setSessionId(session.getSessionId());
		conVo.setUri(session.getUri());
		conVo.setResponseCode(session.getRespStatus());
		return conVo;
	}
}
