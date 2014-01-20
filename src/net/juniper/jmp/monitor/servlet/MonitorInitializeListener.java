package net.juniper.jmp.monitor.servlet;

import javax.servlet.ServletContextEvent;

import net.juniper.jmp.core.locator.SpringWebContextHelper;
import net.juniper.jmp.core.servlet.WtfContextListener;
import net.juniper.jmp.monitor.services.IServerInfoService;

import org.springframework.web.context.support.WebApplicationContextUtils;
/**
 * 
 * @author juntaod
 *
 */
public class MonitorInitializeListener extends WtfContextListener{
	IServerInfoService serverInfoService;
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		SpringWebContextHelper.setSpringCtx(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
		MonitorServletContextAware.getInstance().setContext(event.getServletContext());
		SpringWebContextHelper.getService(IServerInfoService.class).getAllServers();
		new Thread(new NodeStateThread()).start();
		new Thread(new ServerStateThread()).start();
	}
	
}
