package net.juniper.jmp.monitor;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class PmcWeb {
	public static void main(String[] args) {
		try {
			ClassLoader loader = getClassLoader();
			Class<?> c = Class.forName("net.juniper.jmp.monitor.PmcHttpStart", true, loader);
			Object instance = c.newInstance();
			Method m = c.getMethod("start", new Class[0]);
			m.invoke(instance, null);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static ClassLoader getClassLoader() throws MalformedURLException {
		String path = System.getProperty("user.dir");
		String lib = path + "/lib";
		File dir = new File(lib);
		if(dir.exists() && dir.isDirectory()){
			File[] fs = dir.listFiles();
			if(fs != null && fs.length > 0){
				URL[] urls = new URL[fs.length];
				for(int i = 0; i < fs.length; i ++){
					urls[i] = new URL("file:/" + fs[i].getAbsolutePath());
					System.out.println("jar:" + urls[i].toString());
				}
				URLClassLoader cls = new URLClassLoader(urls, PmcWeb.class.getClassLoader());
				return cls;
			}
		}
		return PmcWeb.class.getClassLoader();
	}
}
