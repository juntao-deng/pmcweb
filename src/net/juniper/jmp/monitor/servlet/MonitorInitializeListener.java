package net.juniper.jmp.monitor.servlet;

import javax.servlet.ServletContextEvent;

import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.servlet.WtfContextListener;
import net.juniper.jmp.monitor.services.IServerInfoService;
/**
 * 
 * @author juntaod
 *
 */
public class MonitorInitializeListener extends WtfContextListener{
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		MonitorServletContextAware.getInstance().setContext(event.getServletContext());
		IServerInfoService serverService = ServiceLocator.getService(IServerInfoService.class);
		serverService.getAllServers();
		new Thread(new NodeStateThread()).start();
		new Thread(new ServerStateThread()).start();
	}
	
}
