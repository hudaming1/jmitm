package org.hum.wiredog.console.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hum.wiredog.console.http.config.wiredogConsoleConfig;

public class ConsoleServer {

	private static Server server;
	public static wiredogConsoleConfig wiredogConsoleConfig;
	
	public ConsoleServer(wiredogConsoleConfig config) {
		wiredogConsoleConfig = config;
		server = new Server(config.getHttpPort());
		server.setHandler(getWebAppContext(config));
	}

	public void startJetty() throws Exception {
		server.start();
	}

	public static void stopJetty() throws Exception {
		server.stop();
	}

	private static WebAppContext getWebAppContext(wiredogConsoleConfig config) {
		WebAppContext context = new WebAppContext();
		context.setDescriptor(config.getWebXmlPath()); 
		context.setResourceBase(config.getWebRoot());
		context.setParentLoaderPriority(true);
		return context;
	}

}
