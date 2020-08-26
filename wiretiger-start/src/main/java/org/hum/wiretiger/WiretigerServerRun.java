package org.hum.wiretiger;

import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WtConsoleHttpConfig;
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
				
				WtServerBuilder.init(config).build().start();
			}
		}).start();
		
		System.out.println(ConsoleServer.class.getResource("/webroot").getFile());
//		WtConsoleHttpConfig consoleConfig = new WtConsoleHttpConfig();
//		consoleConfig.setPort(8080);
//		consoleConfig.setWebRoot(ConsoleServer.class.getResource("/webroot").getFile());
//		consoleConfig.setWebXmlPath(ConsoleServer.class.getResource("/webroot/WEB-INF/web.xml").getFile());
//		ConsoleServer.startJetty(consoleConfig);
	}
}
