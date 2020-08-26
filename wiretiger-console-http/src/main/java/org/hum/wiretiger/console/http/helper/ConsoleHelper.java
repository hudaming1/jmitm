package org.hum.wiretiger.console.http.helper;

import org.hum.wiretiger.console.http.vo.WtPipeListVO;
import org.hum.wiretiger.console.http.vo.WtSessionListVO;
import org.hum.wiretiger.facade.enumtype.Protocol;
import org.hum.wiretiger.facade.proxy.WireTigerPipe;
import org.hum.wiretiger.facade.proxy.WiretigerSession;

public class ConsoleHelper {

	public static WtPipeListVO parse2WtPipeListVO(WireTigerPipe item) {
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
