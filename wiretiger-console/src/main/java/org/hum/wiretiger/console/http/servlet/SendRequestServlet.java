package org.hum.wiretiger.console.http.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.console.common.WtConsoleConstant;
import org.hum.wiretiger.console.common.WtSession;
import org.hum.wiretiger.console.common.chain.SessionManagerInvokeChain;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;
import org.hum.wiretiger.proxy.util.HttpRequestCodec;
import org.hum.wiretiger.proxy.util.HttpResponseCodec;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * http://localhost:8080/session/rebuild
 */
public class SendRequestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final String RETURN_LINE = "\r\n";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Type", "text/plain; charset=utf-8");
		String httpRequestWithoutBody = req.getParameter("request");
		String requestBody = Objects.toString(req.getParameter("body"), "");
		
		FullHttpRequest fullHttpRequest = HttpRequestCodec.decode(httpRequestWithoutBody);
		fullHttpRequest.headers().set(HttpConstant.ContentLength, requestBody.getBytes().length);
		fullHttpRequest.content().clear();
		fullHttpRequest.content().writeBytes(requestBody.getBytes());
		
		InetAddress remoteAddress = HttpMessageUtil.parse2InetAddress(fullHttpRequest, false);
		
		WtSession wtSession = new WtSession(WtConsoleConstant.CONSOLE_SESSION_PIPE_ID, fullHttpRequest, System.currentTimeMillis());
		
		Socket socket = new Socket(remoteAddress.getHost(), remoteAddress.getPort());
		socket.getOutputStream().write(HttpRequestCodec.encodeWithBody(fullHttpRequest, RETURN_LINE).getBytes());
		socket.getOutputStream().flush();
		
		String responseWithoutBody = readResponseLineAndHeader(socket.getInputStream());
		FullHttpResponse httpResponse = HttpResponseCodec.decode(responseWithoutBody);
		
		byte[] body = readResponseBody(httpResponse, socket.getInputStream());
		wtSession.setResponse(httpResponse, body, System.currentTimeMillis());
		SessionManagerInvokeChain.addSession(wtSession);
		
		socket.close();
		resp.getWriter().print(true);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private String readResponseLineAndHeader(InputStream inputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sbuilder = new StringBuilder();
		String line = null;
		while (!"".equals((line = br.readLine()))) {
			if (line == null) {
				break;
			}
			sbuilder.append(line);
		}
		return sbuilder.toString();
	}
	
	private byte[] readResponseBody(FullHttpResponse httpResponse, InputStream inputStream) {
		return null;
	}
}
