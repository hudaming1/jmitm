package org.hum.wiretiger.console.service;

import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.console.helper.HttpMessageUtil;
import org.hum.wiretiger.console.vo.WiretigerConnectionDetailVO;
import org.hum.wiretiger.console.vo.WiretigerConnectionListVO;
import org.hum.wiretiger.core.connection.ConnectionManager;
import org.hum.wiretiger.core.connection.bean.WiretigerConnection;

import io.netty.util.internal.StringUtil;

public class ConnectionService {

	public List<WiretigerConnectionListVO> list() {
		List<WiretigerConnectionListVO> connList = new ArrayList<>();
		ConnectionManager.get().getLis().forEach(con -> {
			WiretigerConnectionListVO conVo = new WiretigerConnectionListVO();
			conVo.setRequestId(con.getId());
			conVo.setUri(con.getRequest().uri());
			conVo.setResponseCode(con.getResponse().status().code() + "");
			connList.add(conVo);
		});
		return connList;
	}

	public WiretigerConnectionDetailVO getById(Long id) {
		WiretigerConnection connection = ConnectionManager.get().getConnection(id);
		WiretigerConnectionDetailVO detailVo = new WiretigerConnectionDetailVO();
		detailVo.setRequest(HttpMessageUtil.appendRequest(new StringBuilder(), connection.getRequest()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		detailVo.setResponse(HttpMessageUtil.appendResponse(new StringBuilder(), connection.getResponse()).toString().replaceAll(StringUtil.NEWLINE, "<br />"));
		return detailVo;
	}

}
