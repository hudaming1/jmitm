package org.hum.test;

import org.hum.jmitm.provider.JmitmBuilder;
import org.hum.jmitm.proxy.mock.CatchRequest;
import org.hum.jmitm.proxy.mock.Mock;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkUseCase0202 {
	
	public static void main(String[] args) throws Exception {
		JmitmBuilder wtBuilder = new JmitmBuilder();
		wtBuilder.parseHttps(true);
		wtBuilder.proxyPort(52007).threads(400);
		wtBuilder.consoleHttpPort(8888).consoleWsPort(52996);
		wtBuilder.pipeHistory(10).sessionHistory(200);
		wtBuilder.addMock(
				mockAllInternelRequestForwardLocalhost(),
				mockAllInternelRequestForwardLocalhost2()
				);
		
		wtBuilder.build().start(); 
	}
	
	private static final String USER_ID = "3102";

	private static Mock mockAllInternelRequestForwardLocalhost() {
		return new CatchRequest().eval(request -> {
			return request.uri().startsWith("/sms/basic/") && HttpMethod.OPTIONS != request.method();
		}).rebuildRequest(request -> {
			log.info("hit uri=" + request.uri());
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAllInternelRequestForwardLocalhost2() {
		return new CatchRequest().eval(request -> {
			return request.uri().startsWith("/sms/internal/") && HttpMethod.OPTIONS != request.method();
		}).rebuildRequest(request -> {
			log.info("hit uri=" + request.uri());
			request.uri("/migrate/" + request.uri());
			request.forward("localhost:8081").header("X-User-Id", USER_ID);
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
}
