package org.hum.wiretiger.core.external.conmonitor;

public enum ConnectionStatus {

	// 建立连接
	Active,
	// 已经解析HTTP请求 client -> proxy
	Parsed, 
	// 已经与对端Server建立间接
	Connected,
	// 请求已经转发 proxy -> server
	Forward,
	// 收到了对端Server的响应，server -> proxy
	Read,
	// 将最终结果输出给客户端 proxy -> client
	Flushed,
	// 断开连接
	InActive
	;

	public final static String STATUS = "CON_STATUS";
}
