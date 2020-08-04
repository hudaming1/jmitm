package org.hum.wiretiger.core.external.pipe_monitor;

/**
 * 管道状态
 * <pre>
 *   定义了一个「管道」的生命周期
 * </pre>
 */
public enum PipeStatus {

	// 建立连接
	Init,
	// 已经解析HTTP请求 client -> proxy
	Parsed, 
	// 已经与对端Server建立间接
	Connected,
	// 请求已经转发 proxy -> server
	Forward,
	// 收到了对端Server的响应，server -> proxy
	Received,
	// 将最终结果输出给客户端 proxy -> client
	Flushed,
	// 断开连接(正常终态)
	Closed,
	// ERROR(异常终态)
	Error
	;
}
