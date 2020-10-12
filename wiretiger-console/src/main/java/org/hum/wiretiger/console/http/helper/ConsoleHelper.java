package org.hum.wiretiger.console.http.helper;

import org.hum.wiretiger.console.common.WtSession;
import org.hum.wiretiger.console.http.vo.WiretigerPipeListVO;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;

public class ConsoleHelper {

	public static WiretigerPipeListVO parse2WtPipeListVO(WtPipeContext item) {
		WiretigerPipeListVO vo = new WiretigerPipeListVO();
		vo.setPipeId(item.getId() + "");
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getCurrentStatus().getDesc());
		return vo;
	}

	public static WiretigerSessionListVO parse2WtSessionListVO(WtSession session) {
		WiretigerSessionListVO listVO = new WiretigerSessionListVO();
		listVO.setSessionId(session.getId() + "");
		listVO.setUri(session.getRequest().uri());
		listVO.setResponseCode(session.getResponse() == null ? "PENDING..." : session.getResponse().status().code() + "");
		return listVO;
	}

}
