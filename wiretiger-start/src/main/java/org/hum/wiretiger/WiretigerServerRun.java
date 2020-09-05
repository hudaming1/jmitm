package org.hum.wiretiger;

import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.console.common.listener.Console4WsListener;
import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WtConsoleHttpConfig;
import org.hum.wiretiger.console.websocket.WebSocketServer;
import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.server.WtServerBuilder;

public class WiretigerServerRun {

	public static void main(String[] args) throws Exception {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// config
				WtCoreConfig config = new WtCoreConfig();
				config.setPort(52007);
				config.setThreads(Runtime.getRuntime().availableProcessors());
				config.setDebug(false);
				config.setInterceptors(buildInterceptor());
				
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
	
	public static List<Mock> buildMock() {
		List<Mock> mocks = new ArrayList<>();
		
		Mock mock = new Mock();
		/*** 根据Request拦截 ****/
		InterceptorPicture picture4Req = new InterceptorPicture(InterceptorType.Request);
		/*** 根据Response拦截 ****/
		InterceptorPicture picture4Res = new InterceptorPicture(InterceptorType.Repsonse);
		// 自定义拦截逻辑
		picture4Res.eval((fullHttpRequest) -> {
			
		});
		// 拦截request.header.Host
		picture4Req.header("Host").equal("xxx");
		picture4Req.header("Host").like("xxx");
		// 通过自定义表达式拦截
		picture4Req.header("Host").eval((headerVal)-> {
			return headerVal + "XXX";
		});
		// 通过request.uri拦截
		picture4Req.uri("xxx");
		// 通过模糊匹配关键字(request.uri + request.header + request.body)拦截
		picture4Req.keyword("xxx");
		
		// 对请求进行改造
		criminal.rebuild(InterceptorType.Request).header("Host").modify("http://www.baidu.com");
		criminal.rebuild(InterceptorType.Request).header("Host").modify((headerVal -> {
			return headerVal + "XXX";
		}));

		// 对响应进行改造
		criminal.rebuild(InterceptorType.Response).header("Host").modify("http://www.baidu.com");
		criminal.rebuild(InterceptorType.Response).bdoy().modify((headerVal -> {
			return headerVal + "XXX";
		}));
		
		mock.setInterceporPicture(picture4Req);
		mocks.add(mock);
		return mocks;
	}
}
