package org.hum.wiredog.console.http.helper;

import org.hum.wiredog.console.common.Session;
import org.hum.wiredog.console.http.vo.WiredogPipeListVO;
import org.hum.wiredog.console.http.vo.WiredogSessionListVO;
import org.hum.wiredog.proxy.facade.PipeContext;
import org.hum.wiredog.proxy.pipe.enumtype.Protocol;

public class ConsoleHelper {

	public static WiredogPipeListVO parse2WtPipeListVO(PipeContext item) {
		WiredogPipeListVO vo = new WiredogPipeListVO();
		vo.setPipeId(item.getId() + "");
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getCurrentStatus().getDesc());
		return vo;
	}

	public static WiredogSessionListVO parse2WtSessionListVO(Session session) {
		WiredogSessionListVO listVO = new WiredogSessionListVO();
		listVO.setSessionId(session.getId() + "");
		listVO.setUri(session.getRequest().uri());
		listVO.setResponseCode(session.getResponse() == null ? "PENDING..." : session.getResponse().status().code() + "");
		return listVO;
	}

}
