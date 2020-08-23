package org.hum.wiretiger.console.helper;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.console.vo.WtPipeListVO;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;

public class PipeHolderHelper {

	public static WtPipeListVO parse(PipeHolder item) {
		WtPipeListVO vo = new WtPipeListVO();
		vo.setPipeId(item.getId());
		vo.setName(item.getName());
		vo.setProtocol(item.getProtocol() == null ? Protocol.UNKNOW.getDesc() : item.getProtocol().getDesc());
		vo.setStatus(item.getCurrentStatus().getDesc());
		return vo;
	}
}
