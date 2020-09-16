package org.hum.wiretiger;

import org.hum.wiretiger.console.common.listener.Console4WsListener;
import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WtConsoleHttpConfig;
import org.hum.wiretiger.console.websocket.WebSocketServer;
import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.mock.picture.RequestPicture;
import org.hum.wiretiger.proxy.server.WtServerBuilder;

public class WiretigerServerRun {
	
	public static void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// config
				WtCoreConfig config = new WtCoreConfig();
				config.setPort(52007);
				config.setThreads(Runtime.getRuntime().availableProcessors());
				config.setDebug(false);
				// mock request
				config.addMock(new RequestPicture().eval(request -> {
					return "www.baidu.com".equals(request.headers().get("Host"));
				}).rebuildRequest(request -> {
					request.setUri("www.google.com");
					return request;
				}).rebuildResponse(response -> {
					response.headers().add("wiretiger_mock", System.currentTimeMillis());
					return response;
				}).mock());
				
				// mock response
//				config.addMock(new ResponsePicture().eval(response -> {
//					
//					return "123456".equals(response.headers().get("wiretiger_mock"));
//				}).rebuildResponse(response -> {
//					
//					return response;
//				}).mock());
				
				WtServerBuilder.init(config).addEventListener(new Console4WsListener()).build().start();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					WtConsoleHttpConfig consoleConfig = new WtConsoleHttpConfig();
					consoleConfig.setPort(8080);
					consoleConfig.setWebRoot(ConsoleServer.class.getResource("/webroot").getFile());
					consoleConfig.setWebXmlPath(ConsoleServer.class.getResource("/webroot/WEB-INF/web.xml").getFile());
					ConsoleServer.startJetty(consoleConfig);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();


		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new WebSocketServer(52996).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
		WiretigerServerRun.start();
	}
}
