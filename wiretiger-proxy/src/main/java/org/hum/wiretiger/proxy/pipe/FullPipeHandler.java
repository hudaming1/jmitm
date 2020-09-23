package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

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

	public abstract void clientConnect0(WtPipeContext ctx);

	public void clientDisconnect(WtPipeContext ctx) {
		clientDisconnect0(ctx);
		if (next != null) {
			this.next.clientDisconnect(ctx);
		}
	}
	
	public abstract void clientDisconnect0(WtPipeContext ctx);

	public void clientParsed(WtPipeContext ctx) {
		clientParsed0(ctx);
		if (next != null) {
			this.next.clientParsed(ctx);
		}
	}
	
	public abstract void clientParsed0(WtPipeContext ctx);
	
	public void clientRead(WtPipeContext ctx, FullHttpRequest request) {
		clientRead0(ctx, request);
		if (next != null) {
			this.next.clientRead(ctx, request);
		}
	}
	
	public abstract void clientRead0(WtPipeContext ctx, FullHttpRequest request) ;
	
	public void serverConnect(WtPipeContext ctx) {
		serverConnect0(ctx);
		if (next != null) {
			this.next.serverConnect(ctx);
		}
	}
	
	public abstract void serverConnect0(WtPipeContext ctx);
	
	public void serverHandshakeSucc(WtPipeContext ctx) {
		serverHandshakeSucc0(ctx);
		if (next != null) {
			this.next.serverHandshakeSucc(ctx);
		}
	}
	
	public abstract void serverHandshakeSucc0(WtPipeContext ctx);
	
	public void serverFlush(WtPipeContext ctx, FullHttpRequest request) {

		if (next != null) {
			this.next.serverFlush(ctx, request);
		}
	}
	
	public abstract void serverRead0(WtPipeContext ctx, FullHttpResponse response) ;
	
	public void serverRead(WtPipeContext ctx, FullHttpResponse response) {
		serverRead0(ctx, response);
		if (next != null) {
			this.next.serverRead(ctx, response);
		}
	}
	
	public abstract void clientFlush0(WtPipeContext ctx, FullHttpResponse response) ;
	
	public void clientFlush(WtPipeContext ctx, FullHttpResponse response) {
		clientFlush0(ctx, response);
		if (next != null) {
			this.next.clientFlush(ctx, response);
		}
	}
	
	public abstract void clientClose0(WtPipeContext ctx) ;
	
	public void clientClose(WtPipeContext ctx) {
		clientClose0(ctx);
		if (next != null) {
			this.next.clientClose(ctx);
		}
	}
	
	public abstract void serverClose0(WtPipeContext ctx) ;
	
	public void serverClose(WtPipeContext ctx) {
		serverClose0(ctx);
		if (next != null) {
			this.next.serverClose(ctx);
		}
	}
	
	public abstract void clientError0(WtPipeContext ctx);
	
	public void clientError(WtPipeContext ctx) {
		clientError0(ctx);
		if (next != null) {
			this.next.clientError(ctx);
		}
	}
	
	public abstract void serverError0(WtPipeContext ctx) ;
	
	public void serverError(WtPipeContext ctx) {
		serverError0(ctx);
		if (next != null) {
			this.next.serverError(ctx);
		}
	}
	
	public abstract void clientHandshakeSucc0(WtPipeContext ctx) ;

	public void clientHandshakeSucc(WtPipeContext ctx) {
		clientHandshakeSucc0(ctx);
		if (next != null) {
			this.next.clientHandshakeSucc(ctx);
		}
	}
	
	public abstract void clientHandshakeFail0(WtPipeContext ctx) ;
	
	public void clientHandshakeFail(WtPipeContext ctx) {
		clientHandshakeFail0(ctx);
		if (next != null) {
			this.next.clientHandshakeFail(ctx);
		}
	}
	
	public abstract void serverConnectFailed0(WtPipeContext ctx);
	
	public void serverConnectFailed(WtPipeContext ctx) {
		serverConnectFailed0(ctx);
		if (next != null) {
			this.next.serverConnectFailed(ctx);
		}
	}
	
	public abstract void serverHandshakeFail0(WtPipeContext ctx) ;
	
	public void serverHandshakeFail(WtPipeContext ctx) {
		serverHandshakeFail0(ctx);
		if (next != null) {
			this.next.serverHandshakeFail(ctx);
		}
	}
	
}