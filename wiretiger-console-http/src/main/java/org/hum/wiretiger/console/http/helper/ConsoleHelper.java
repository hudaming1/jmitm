package org.hum.wiretiger.console.http.helper;

import org.hum.wiretiger.api.enumtype.Protocol;
import org.hum.wiretiger.console.http.vo.WtPipeListVO;
import org.hum.wiretiger.console.http.vo.WtSessionListVO;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.session.bean.WtSession;

public class ConsoleHelper {

	public static WtPipeListVO parse2WtPipeListVO(PipeHolder item) {
		WtPipeListVO vo = new WtPipeListVO();
		vo.setPipeId(item.getId());
		vo.setName(item.getName());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getCurrentStatus().getDesc());
		return vo;
	}

	public static WtSessionListVO parse2WtSessionListVO(WtSession session) {
		WtSessionListVO conVo = new WtSessionListVO();
		conVo.setSessionId(session.getId());
		conVo.setUri(session.getRequest().uri());
		conVo.setResponseCode(session.getResponse().status().code() + "");
		return conVo;
	}
}
