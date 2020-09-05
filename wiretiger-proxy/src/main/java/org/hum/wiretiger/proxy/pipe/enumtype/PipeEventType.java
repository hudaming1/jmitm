package org.hum.wiretiger.proxy.pipe.enumtype;

/**
 * 第一版事件类型先与状态一致，后面再扩展
 * @author hudaming
 */
public enum PipeEventType {

	// 建立连接
	Init(1, "Init"),
	// 与客户端握手完成
	ClientTlsFinish(3, "ClientTlsFinish"),
	// 与服务端握手完成
	ServerTlsFinish(4, "ServerTlsFinish"),
	// 已经解析HTTP请求 client -> proxy
	Parsed(20, "Parsed"), 
	// 已经与对端Server建立间接
	ServerConnected(30, "ServerConnected"),
	// 读取客户端请求
	Read(40, "Read"),
	// 请求已经转发 proxy -> server
	Forward(50, "Forward"),
	// 收到了对端Server的响应，server -> proxy
	Received(60, "Received"),
	// 将最终结果输出给客户端 proxy -> client
	Flushed(70, "Flushed"),
	// 断开连接(正常终态)
	ClientClosed(81, "ClientClosed"),
	ServerClosed(82, "ServerClosed"),
	// ERROR(异常终态)
	Error(90, "Error"),
	;
	
	private int code;
	private String desc;
	
	PipeEventType(int code, String desc) {
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
