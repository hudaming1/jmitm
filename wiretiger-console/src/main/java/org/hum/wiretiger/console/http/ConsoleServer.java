package org.hum.wiretiger.console.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hum.wiretiger.console.http.config.WtConsoleHttpConfig;

public class ConsoleServer {

	private static Server server;

	public static void startJetty(WtConsoleHttpConfig config) throws Exception {
		server = new Server(config.getPort());
		server.setHandler(getWebAppContext(config));
		server.start();
		server.join();
	}

	public static void stopJetty() throws Exception {
		server.stop();
	}

	private static WebAppContext getWebAppContext(WtConsoleHttpConfig config) {
		WebAppContext context = new WebAppContext();
		context.setDescriptor(config.getWebXmlPath()); 
		context.setResourceBase(config.getWebRoot());
		context.setParentLoaderPriority(true);
		return context;
	}

}
