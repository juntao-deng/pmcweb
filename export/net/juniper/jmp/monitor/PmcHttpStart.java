package net.juniper.jmp.monitor;

import java.io.File;

import org.apache.catalina.startup.Tomcat;

public class PmcHttpStart {
	public PmcHttpStart() {
		
	}
	
	public void start() {
		try {
			long startTime = System.currentTimeMillis();
			Tomcat tomcat = new Tomcat();
			tomcat.enableNaming();
			String baseDir = getBaseDir();
			tomcat.setBaseDir(baseDir);
			tomcat.getServer().setParentClassLoader(PmcHttpStart.class.getClassLoader());
			
			initDataSource(tomcat);
			
	        tomcat.setPort(8080);
	        tomcat.addWebapp("/wtf", getWtfPath());
	        tomcat.addWebapp("/monitor", getMonitorPath());
	        tomcat.getConnector().setURIEncoding("UTF-8");
			tomcat.start();
			System.err.println("Tomcat started in " + (System.currentTimeMillis() - startTime) + " ms.");
			tomcat.getServer().await();
			
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDataSource(Tomcat tomcat) {
		System.setProperty("JMP_DS_PROVIDER", "net.juniper.jmp.monitor.ds.EmbededDataSourceProvider");
//		 NamingResources name = new NamingResources();
//		 ContextResource resource = new ContextResource();
//		 resource.setName("jdbc/MysqlDS");
//		 resource.setAuth("Container");
//		 resource.setType("javax.sql.DataSource");
//		 resource.setProperty("factory", "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory");
//		 resource.setProperty("maxActive", "20");
//		 resource.setProperty("maxIdle", "10");
//		 resource.setProperty("maxWait", "100000");
////		 resource.setProperty("username", "sa");
////		 resource.setProperty("password", "sa");
//		 resource.setProperty("driverClassName", "org.apache.derby.jdbc.EmbeddedDriver");
//		 resource.setProperty("url", "jdbc:derby:clientdb;create=true");
//		 name.addResource(resource);
//		 
//		 ContextResourceLink res = new ContextResourceLink();
//		 res.setName("jdbc/MysqlDS");
//		 res.setGlobal("jdbc/MysqlDS");
//		 res.setType("javax.sql.DataSource");
//		 name.addResourceLink(res);
//		 
//		 tomcat.getServer().setGlobalNamingResources(name);
	}
	
	private static String getBaseDir() {
		if(isWindows())
			return "c:/pmcweb/";
		else
			return "/var/tmp/pmcweb/";
	}

	private static String getWtfPath() {
		String path = System.getProperty("user.dir");
		String buildPath = path + "/wtf";
		File wtfDir = new File(buildPath);
		if(wtfDir.exists())
			return buildPath;
		path = path.substring(0, path.lastIndexOf(File.separatorChar));
		path = path.substring(0, path.lastIndexOf(File.separatorChar));
		return path + "/wtfbase/web";
	}
	
	private static String getMonitorPath() {
		String path = System.getProperty("user.dir");
		String buildPath = path + "/monitor";
		File monitorDir = new File(buildPath);
		if(monitorDir.exists())
			return buildPath;
		return path + "/web";
	}
	
	private static boolean isWindows() {
		String osName = System.getProperty("os.name");
		return osName.toLowerCase().indexOf("windows") != -1;
	}
}
