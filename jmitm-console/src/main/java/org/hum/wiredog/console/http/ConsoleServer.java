package org.hum.wiredog.console.http;

import java.util.Objects;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hum.wiredog.console.http.config.WiredogConsoleConfig;

public class ConsoleServer {

	private static Server server;
	public static WiredogConsoleConfig wiredogConsoleConfig;
	
	public ConsoleServer(WiredogConsoleConfig config) {
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

	private static WebAppContext getWebAppContext(WiredogConsoleConfig config) {
		WebAppContext context = new WebAppContext();
		context.setResourceBase(Objects.toString(config.getWebRoot(), ConsoleServer.class.getResource("/webroot").getFile()));
		context.setDescriptor(Objects.toString(config.getWebXmlPath(), ConsoleServer.class.getResource("/webroot/WEB-INF/web.xml").getFile())); 
		context.setParentLoaderPriority(true);
		return context;
	}

}
