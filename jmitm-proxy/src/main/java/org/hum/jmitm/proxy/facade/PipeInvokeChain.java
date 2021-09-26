package org.hum.jmitm.proxy.facade;

import org.hum.jmitm.common.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author hudaming
 */
public abstract class PipeInvokeChain {
	
	private PipeInvokeChain next;
	
	public PipeInvokeChain(PipeInvokeChain next) {
		this.next = next;
	}

	public void clientConnect(PipeContext ctx) {
		boolean invokeNext = clientConnect0(ctx);
		if (next != null && invokeNext) {
			this.next.clientConnect(ctx);
		}
	}

	protected abstract boolean clientConnect0(PipeContext ctx);

	public void clientParsed(PipeContext ctx) {
		boolean invokeNext = clientParsed0(ctx);
		if (next != null && invokeNext) {
			this.next.clientParsed(ctx);
		}
	}
	
	protected abstract boolean clientParsed0(PipeContext ctx);
	
	public void clientRead(PipeContext ctx, FullHttpRequest request) {
		boolean invokeNext = clientRead0(ctx, request);
		if (next != null && invokeNext) {
			this.next.clientRead(ctx, request);
		}
	}
	
	protected abstract boolean clientRead0(PipeContext ctx, FullHttpRequest request) ;
	
	public void serverConnect(PipeContext ctx, InetAddress InetAddress) {
		boolean invokeNext = serverConnect0(ctx, InetAddress);
		if (next != null && invokeNext) {
			this.next.serverConnect(ctx, InetAddress);
		}
	}
	
	protected abstract boolean serverConnect0(PipeContext ctx, InetAddress InetAddress);
	
	public void serverHandshakeSucc(PipeContext ctx) {
		boolean invokeNext = serverHandshakeSucc0(ctx);
		if (next != null && invokeNext) {
			this.next.serverHandshakeSucc(ctx);
		}
	}
	
	protected abstract boolean serverHandshakeSucc0(PipeContext ctx);
	
	public void serverFlush(PipeContext ctx, FullHttpRequest request) {
		boolean invokeNext = serverFlush0(ctx, request);
		if (next != null && invokeNext) {
			this.next.serverFlush(ctx, request);
		}
	}
	
	protected abstract boolean serverFlush0(PipeContext ctx, FullHttpRequest request);
	
	public void serverRead(PipeContext ctx, FullHttpResponse response) {
		boolean invokeNext = serverRead0(ctx, response);
		if (next != null && invokeNext) {
			this.next.serverRead(ctx, response);
		}
	}
	
	protected abstract boolean serverRead0(PipeContext ctx, FullHttpResponse response) ;
	
	public void clientFlush(PipeContext ctx, FullHttpResponse response) {
		boolean invokeNext = clientFlush0(ctx, response);
		if (next != null && invokeNext) {
			this.next.clientFlush(ctx, response);
		}
	}
	protected abstract boolean clientFlush0(PipeContext ctx, FullHttpResponse response) ;
	
	public void clientClose(PipeContext ctx) {
		boolean invokeNext = clientClose0(ctx);
		if (next != null && invokeNext) {
			this.next.clientClose(ctx);
		}
	}
	protected abstract boolean clientClose0(PipeContext ctx) ;
	
	public void serverClose(PipeContext ctx) {
		boolean invokeNext = serverClose0(ctx);
		if (next != null && invokeNext) {
			this.next.serverClose(ctx);
		}
	}
	protected abstract boolean serverClose0(PipeContext ctx) ;
	
	public void clientError(PipeContext ctx, Throwable cause) {
		boolean invokeNext = clientError0(ctx, cause);
		if (next != null && invokeNext) {
			this.next.clientError(ctx, cause);
		}
	}
	
	protected abstract boolean clientError0(PipeContext ctx, Throwable cause);
	
	public void serverError(PipeContext ctx, Throwable cause) {
		boolean invokeNext = serverError0(ctx, cause);
		if (next != null && invokeNext) {
			this.next.serverError(ctx, cause);
		}
	}
	
	protected abstract boolean serverError0(PipeContext ctx, Throwable cause) ;

	public void clientHandshakeSucc(PipeContext ctx) {
		boolean invokeNext = clientHandshakeSucc0(ctx);
		if (next != null && invokeNext) {
			this.next.clientHandshakeSucc(ctx);
		}
	}
	
	protected abstract boolean clientHandshakeSucc0(PipeContext ctx) ;
	
	public void clientHandshakeFail(PipeContext ctx, Throwable cause) {
		boolean invokeNext = clientHandshakeFail0(ctx, cause);
		if (next != null && invokeNext) {
			this.next.clientHandshakeFail(ctx, cause);
		}
	}
	
	protected abstract boolean clientHandshakeFail0(PipeContext ctx, Throwable cause) ;
	
	public void serverConnectFailed(PipeContext ctx, Throwable cause) {
		boolean invokeNext = serverConnectFailed0(ctx, cause);
		if (next != null && invokeNext) {
			this.next.serverConnectFailed(ctx, cause);
		}
	}
	
	protected abstract boolean serverConnectFailed0(PipeContext ctx, Throwable cause);
	
	public void serverHandshakeFail(PipeContext ctx, Throwable cause) {
		boolean invokeNext = serverHandshakeFail0(ctx, cause);
		if (next != null && invokeNext) {
			this.next.serverHandshakeFail(ctx, cause);
		}
	}
	
	protected abstract boolean serverHandshakeFail0(PipeContext ctx, Throwable cause) ;
}