package org.hum.wiretiger.console.service;

import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.console.vo.WiretigerConnectionListVO;
import org.hum.wiretiger.core.connection.ConnectionManager;

public class ConnectionService {

	public List<WiretigerConnectionListVO> list() {
		List<WiretigerConnectionListVO> connList = new ArrayList<>();
		ConnectionManager.get().getLis().forEach(con -> {
			WiretigerConnectionListVO conVo = new WiretigerConnectionListVO();
			conVo.setUri(con.getRequest().uri());
			conVo.setResponseCode(con.getResponse().status().code() + "");
			connList.add(conVo);
		});
		return connList;
	}

}
