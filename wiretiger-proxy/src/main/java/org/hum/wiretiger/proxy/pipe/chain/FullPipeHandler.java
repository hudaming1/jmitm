package org.hum.wiretiger.proxy.pipe.chain;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * TODO 将所有abstract增加返回值，根据返回值判断是否需要调用下一个节点
 * @author hudaming
 *
 */
public abstract class FullPipeHandler {
	
	private FullPipeHandler next;
	
	public FullPipeHandler(FullPipeHandler next) {
		this.next = next;
	}

	public void clientConnect(WtPipeContext ctx) {
		clientConnect0(ctx);
		if (next != null) {
			this.next.clientConnect(ctx);
		}
	}

	protected abstract void clientConnect0(WtPipeContext ctx);

	public void clientParsed(WtPipeContext ctx) {
		clientParsed0(ctx);
		if (next != null) {
			this.next.clientParsed(ctx);
		}
	}
	
	protected abstract void clientParsed0(WtPipeContext ctx);
	
	public void clientRead(WtPipeContext ctx, FullHttpRequest request) {
		clientRead0(ctx, request);
		if (next != null) {
			this.next.clientRead(ctx, request);
		}
	}
	
	protected abstract void clientRead0(WtPipeContext ctx, FullHttpRequest request) ;
	
	public void serverConnect(WtPipeContext ctx, InetAddress InetAddress) {
		serverConnect0(ctx, InetAddress);
		if (next != null) {
			this.next.serverConnect(ctx, InetAddress);
		}
	}
	
	protected abstract void serverConnect0(WtPipeContext ctx, InetAddress InetAddress);
	
	public void serverHandshakeSucc(WtPipeContext ctx) {
		serverHandshakeSucc0(ctx);
		if (next != null) {
			this.next.serverHandshakeSucc(ctx);
		}
	}
	
	protected abstract void serverHandshakeSucc0(WtPipeContext ctx);
	
	public void serverFlush(WtPipeContext ctx, FullHttpRequest request) {
		serverFlush0(ctx, request);
		if (next != null) {
			this.next.serverFlush(ctx, request);
		}
	}
	
	protected abstract void serverFlush0(WtPipeContext ctx, FullHttpRequest request);
	
	public void serverRead(WtPipeContext ctx, FullHttpResponse response) {
		serverRead0(ctx, response);
		if (next != null) {
			this.next.serverRead(ctx, response);
		}
	}
	
	protected abstract void serverRead0(WtPipeContext ctx, FullHttpResponse response) ;
	
	public void clientFlush(WtPipeContext ctx, FullHttpResponse response) {
		clientFlush0(ctx, response);
		if (next != null) {
			this.next.clientFlush(ctx, response);
		}
	}
	protected abstract void clientFlush0(WtPipeContext ctx, FullHttpResponse response) ;
	
	public void clientClose(WtPipeContext ctx) {
		clientClose0(ctx);
		if (next != null) {
			this.next.clientClose(ctx);
		}
	}
	protected abstract void clientClose0(WtPipeContext ctx) ;
	
	public void serverClose(WtPipeContext ctx) {
		serverClose0(ctx);
		if (next != null) {
			this.next.serverClose(ctx);
		}
	}
	protected abstract void serverClose0(WtPipeContext ctx) ;
	
	public void clientError(WtPipeContext ctx, Throwable cause) {
		clientError0(ctx, cause);
		if (next != null) {
			this.next.clientError(ctx, cause);
		}
	}
	
	protected abstract void clientError0(WtPipeContext ctx, Throwable cause);
	
	
	public void serverError(WtPipeContext ctx, Throwable cause) {
		serverError0(ctx, cause);
		if (next != null) {
			this.next.serverError(ctx, cause);
		}
	}
	
	protected abstract void serverError0(WtPipeContext ctx, Throwable cause) ;

	public void clientHandshakeSucc(WtPipeContext ctx) {
		clientHandshakeSucc0(ctx);
		if (next != null) {
			this.next.clientHandshakeSucc(ctx);
		}
	}
	
	protected abstract void clientHandshakeSucc0(WtPipeContext ctx) ;
	
	public void clientHandshakeFail(WtPipeContext ctx, Throwable cause) {
		clientHandshakeFail0(ctx, cause);
		if (next != null) {
			this.next.clientHandshakeFail(ctx, cause);
		}
	}
	
	protected abstract void clientHandshakeFail0(WtPipeContext ctx, Throwable cause) ;
	
	public void serverConnectFailed(WtPipeContext ctx, Throwable cause) {
		serverConnectFailed0(ctx, cause);
		if (next != null) {
			this.next.serverConnectFailed(ctx, cause);
		}
	}
	
	protected abstract void serverConnectFailed0(WtPipeContext ctx, Throwable cause);
	
	public void serverHandshakeFail(WtPipeContext ctx, Throwable cause) {
		serverHandshakeFail0(ctx, cause);
		if (next != null) {
			this.next.serverHandshakeFail(ctx, cause);
		}
	}
	
	protected abstract void serverHandshakeFail0(WtPipeContext ctx, Throwable cause) ;
}