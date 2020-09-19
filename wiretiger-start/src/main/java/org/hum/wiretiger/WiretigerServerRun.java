package org.hum.wiretiger;

import java.io.File;
import java.io.FileInputStream;

import org.hum.wiretiger.console.common.listener.Console4WsListener;
import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WtConsoleHttpConfig;
import org.hum.wiretiger.console.websocket.WebSocketServer;
import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.mock.RequestPicture;
import org.hum.wiretiger.proxy.mock.ResponsePicture;
import org.hum.wiretiger.proxy.server.WtServerBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
				
				// DEMO1：将「wiretiger.com」重定向到「localhost:8080」，等效于配置host:   wiretiger.com    127.0.0.1:8080
				config.addMock(new RequestPicture().eval(request -> {
					return "wiretiger.com".equals(request.headers().get("Host").split(":")[0]);
				}).rebuildRequest(request -> {
					request.headers().set("Host", "localhost:8080");
					return request;
				}).mock());
				
				// DEMO2：修改了百度首页的Logo，打开https://www.baidu.com，会发现首页Logo变成了360So
				config.addMock(new RequestPicture().eval(request -> {
					return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) &&
							("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri())); 
				}).rebuildRequest(request -> {
					log.info("hit baidu_logo");
					request.headers().set("Host", "p.ssl.qhimg.com:443");
					request.setUri("/t012cdb572f41b93733.png");
					return request;
				}).rebuildResponse(response -> {
					response.headers().add("wiretiger_mock", "redirect to 360_search");
					return response;
				}).mock());

				try {
					FileInputStream fileInputStream = new FileInputStream(new File(WiretigerServerRun.class.getResource("/mock/google.png").getFile()));
					byte[] bytes = new byte[fileInputStream.available()];
					fileInputStream.read(bytes);
					fileInputStream.close();
					// DEMO2：修改百度活动页的Logo，读取本地GoogleLogo文件
					config.addMock(new RequestPicture().eval(request -> {
						return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) && request.uri().contains("hqydong_4f3f63f09807e2a2535ee5c2b6100511"); 
					}).rebuildResponse(response -> {
						log.info("mock google logo=" + response);
						response.headers().add("wiretiger_mock", "mock google_logo");
						response.content().clear();
						response.content().writeBytes(bytes);
						response.headers().set("Content-Length", bytes.length);
						return response;
					}).mock());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// DEMO3：拦截所有响应，对响应打标记
				config.addMock(new ResponsePicture().eval(response -> {
					return true;
				}).rebuildResponse(response -> {
					response.headers().set("signby", "hudaming");
					return response;
				}).mock());
				
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
