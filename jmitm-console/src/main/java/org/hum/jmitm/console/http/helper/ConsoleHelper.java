package org.hum.jmitm.console.http.helper;

import org.hum.jmitm.console.common.Session;
import org.hum.jmitm.console.http.vo.JmitmPipeListVO;
import org.hum.jmitm.console.http.vo.JmitmSessionListVO;
import org.hum.jmitm.proxy.facade.PipeContext;
import org.hum.jmitm.proxy.pipe.enumtype.Protocol;

public class ConsoleHelper {

	public static JmitmPipeListVO parse2WtPipeListVO(PipeContext item) {
		JmitmPipeListVO vo = new JmitmPipeListVO();
		vo.setPipeId(item.getId() + "");
		vo.setName(item.getSourceHost() + ":" + item.getSourcePort() + "->" + item.getTargetHost() + ":" + item.getTargetPort());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getCurrentStatus().getDesc());
		return vo;
	}

	public static JmitmSessionListVO parse2WtSessionListVO(Session session) {
		JmitmSessionListVO listVO = new JmitmSessionListVO();
		listVO.setSessionId(session.getId() + "");
		listVO.setUri(session.getRequest().uri());
		listVO.setResponseCode(session.getResponse() == null ? "PENDING..." : session.getResponse().status().code() + "");
		return listVO;
	}

}
