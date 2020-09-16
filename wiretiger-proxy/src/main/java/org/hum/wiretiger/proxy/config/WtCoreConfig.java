package org.hum.wiretiger.proxy.config;

import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.proxy.mock.Mock;

import lombok.Data;

@Data
public class WtCoreConfig {

	// 监听端口
	private Integer port;
	// 线程池
	private Integer threads;
	//
	private boolean isDebug;
	//
	private List<Mock> mockList = new ArrayList<Mock>();
	
	public void addMock(Mock mock) {
		this.mockList.add(mock);
	}
}
