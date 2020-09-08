package org.hum.wiretiger.console.http.helper;

import org.hum.wiretiger.console.http.vo.WiretigerPipeListVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;
import org.hum.wiretiger.proxy.facade.event.WiretigerSession;
import org.hum.wiretiger.proxy.facade.lite.WiretigerFullPipe;
import org.hum.wiretiger.proxy.facade.lite.WiretigerSimpleSession;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

public class ConsoleHelper {

	public static WiretigerPipeListVO parse2WtPipeListVO(WiretigerPipe item) {
		WiretigerPipeListVO vo = new WiretigerPipeListVO();
		vo.setPipeId(item.getPipeId());
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getStatus().getDesc());
		return vo;
	}

	public static WiretigerPipeListVO parse2WtPipeListVO(WiretigerFullPipe item) {
		
		WiretigerPipeListVO vo = new WiretigerPipeListVO();
		vo.setPipeId(item.getPipeId());
		vo.setName(item.getPipeName());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getStatus().getDesc());
		return vo;
	}

	public static WiretigerSessionListVO parse2WtSessionListVO(WiretigerSimpleSession session) {
		WiretigerSessionListVO listVO = new WiretigerSessionListVO();
		listVO.setSessionId(session.getSessionId() + "");
		listVO.setUri(session.getUri());
		listVO.setResponseCode(session.getResponseCode());
		return listVO;
	}

	public static WiretigerSessionListVO parse2WtSessionListVO(WiretigerSession session) {
		WiretigerSessionListVO listVO = new WiretigerSessionListVO();
		listVO.setSessionId(session.getSessionId() + "");
		listVO.setUri(session.getUri());
		listVO.setResponseCode(session.getResponseCode());
		return listVO;
	}
}
