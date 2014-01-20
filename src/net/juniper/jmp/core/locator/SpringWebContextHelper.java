package net.juniper.jmp.core.locator;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;

public final class SpringWebContextHelper {
	private static WebApplicationContext springCtx;
	public static void setSpringCtx(WebApplicationContext ctx){
		springCtx = ctx;
	}
	
	public static WebApplicationContext getSpringCtx() {
		return springCtx;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T getService(Class<T> clazz){
		try {
			return (T) getSpringCtx().getBean(clazz.getName());
		} 
		catch (BeansException e) {
			e.printStackTrace();
		}
		return null;
	}

}
