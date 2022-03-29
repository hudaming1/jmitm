package org.hum.jmitm.proxy.config;

import java.util.ArrayList;
import java.util.List;

import org.hum.jmitm.proxy.mock.Mock;

import lombok.Data;

@Data
public class JmitmCoreConfig {

	// 监听端口
	private int port;
	// 线程池
	private int threads = Runtime.getRuntime().availableProcessors() * 2;
	//
	private boolean isDebug;
	/**
	 * 是否打开Mock总开关(true-打开Mock；false-关闭mock)
	 * true-MockList全部生效；false-MockList全部禁止。
	 */
	private boolean masterMockStwich = true;
	/**
	 * Mock清单：决定了对哪些「请求」或「响应」进行拦截，以及拦截后进行什么样的处理
	 * 确保Interceptor和Rebuilder必须均至少出现一次
	 */
	private List<Mock> mockList = new ArrayList<Mock>();
	/**
	 * 是否对HTTPS协议进行解析（解析的前提是需要客户端安装wiredogCA）
	 * 如果客户端不希望wiredog对客户端引入CA，即可以将该值设为false（虽然浏览器
	 * 或OS支持对HTTP和HTTPS区别进行配置，但现实情况是部分移动端在代理时对两个协议作为一个整体对待，因此考虑加入该参数针对这种场景做兼容），
	 * 这样wiredog只拦截HTTP的请求和响应，对HTTPS请求和响不做解析，直接放行处理
	 */
	private boolean isParseHttps = true;
	
	public void addMock(Mock mock) {
		// TODO check mock is complete
		this.mockList.add(mock);
	}
}
