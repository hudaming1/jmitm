package org.hum.wiretiger.core.pipe.enumtype;

/**
 * 管道状态
 * <pre>
 *   定义了一个「管道」的生命周期
 * </pre>
 */
public enum PipeStatus {

	// 建立连接
	Init(1, "Init"),
	// 已经解析HTTP请求 client -> proxy
	Parsed(2, "Parsed"), 
	// 读取客户端请求
	Read(3, "Read"),
	// 已经与对端Server建立间接
	Connected(4, "Connected"),
	// 请求已经转发 proxy -> server
	Forward(5, "Forward"),
	// 收到了对端Server的响应，server -> proxy
	Received(6, "Received"),
	// 将最终结果输出给客户端 proxy -> client
	Flushed(7, "Flushed"),
	// 断开连接(正常终态)
	Closed(8, "Closed"),
	// ERROR(异常终态)
	Error(9, "Error"),
	;
	
	private int code;
	private String desc;
	
	PipeStatus(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
