package net.juniper.jmp.monitor.servlet;

import javax.servlet.ServletContext;
/**
 * 
 * @author juntaod
 *
 */
public class MonitorServletContextAware {
	private ServletContext context;
	private static MonitorServletContextAware instance = new MonitorServletContextAware();
	private MonitorServletContextAware() {
	}
	
	public static MonitorServletContextAware getInstance() {
		return instance;
	}
	
	public void setContext(ServletContext context){
		this.context = context;
	}
	
	public ServletContext getContext() {
		return this.context;
	}
}
