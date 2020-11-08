package org.hum.wiredog.console.http.servlet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.Arrays;
import org.hum.wiredog.common.constant.HttpConstant;
import org.hum.wiredog.common.util.HttpMessageUtil;
import org.hum.wiredog.common.util.HttpRequestCodec;
import org.hum.wiredog.common.util.HttpResponseCodec;
import org.hum.wiredog.common.util.HttpMessageUtil.InetAddress;
import org.hum.wiredog.console.common.WtConsoleConstant;
import org.hum.wiredog.console.common.WtSession;
import org.hum.wiredog.console.common.chain.SessionManagerInvokeChain;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * http://localhost:8080/session/sendRequest
 */
public class SendRequestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

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
		wtSession.setRequestBytes(requestBody.getBytes());
		
		
		Socket socket = new Socket(remoteAddress.getHost().trim(), remoteAddress.getPort());
		socket.getOutputStream().write(HttpRequestCodec.encodeWithBody(fullHttpRequest, HttpConstant.RETURN_LINE).getBytes());
		socket.getOutputStream().flush();
		
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		
		String responseWithoutBody = readResponseLineAndHeader(dis);
		FullHttpResponse httpResponse = HttpResponseCodec.decode(responseWithoutBody);
		
		byte[] body = readResponseBody(httpResponse, dis);
		wtSession.setResponse(httpResponse, body, System.currentTimeMillis());
		SessionManagerInvokeChain.addSession(wtSession);
		
		socket.close();
		resp.getWriter().print(true);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private String readResponseLineAndHeader(DataInputStream inputStream) throws IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sbuilder = new StringBuilder();
		String line = null;
		while (!"".equals((line = inputStream.readLine()))) {
			if (line == null) {
				break;
			}
			sbuilder.append(line).append(HttpConstant.RETURN_LINE);
		}
		return sbuilder.toString();
	}
	
	private byte[] readResponseBody(FullHttpResponse httpResponse, DataInputStream dis) throws IOException {
		// 考虑body读取方式
		// 1.read by content-length
		if (httpResponse.headers().contains(HttpConstant.ContentLength)) {
			Integer contentLength = Integer.parseInt(httpResponse.headers().get(HttpConstant.ContentLength).trim());
			byte[] body = new byte[contentLength];
			dis.read(body);
			return body;
		}
		
		// 2.read by chunked
		if (httpResponse.headers().contains(HttpConstant.TransferEncoding)) {
			// chunked格式：#chunked_size#|\r\n|#chunked_data#|\r\n|#chunked_size#|\r\n|#chunked_data#|\r\n|0|\r\n|\r\n
			byte[] body = new byte[0];
			String chunkedSize = "";
			while (!"".equals((chunkedSize = dis.readLine()))) {
				if (chunkedSize == null) {
					break;
				}
				int len = Integer.parseInt(chunkedSize, 16);
				byte[] chunked = new byte[len];
				dis.read(chunked);
				body = Arrays.concatenate(body, chunked);
			}
			return body;
		}
		return null;
	}
}
