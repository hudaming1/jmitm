package org.hum.wiretiger.console;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class ConsoleServer {

	private static final String DEFAULT_WEBAPP = ConsoleServer.class.getResource("/webroot").getFile();
	private static final String DEFAULT_WEB = ConsoleServer.class.getResource("/webroot/WEB-INF/web.xml").getFile();
	private static Server server;


	public static void startJetty(int port) throws Exception {
		server = new Server(port);
		server.setHandler(getWebAppContext());
		server.start();
		server.join();
	}

	public static void stopJetty() throws Exception {
		server.stop();
	}

	private static WebAppContext getWebAppContext() {
		WebAppContext context = new WebAppContext();
		context.setDescriptor(DEFAULT_WEB); 
		context.setResourceBase(DEFAULT_WEBAPP);
		context.setParentLoaderPriority(true);
		return context;
	}

}
