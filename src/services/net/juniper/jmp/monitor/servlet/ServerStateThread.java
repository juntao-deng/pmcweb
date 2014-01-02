package net.juniper.jmp.monitor.servlet;

import java.util.concurrent.CountDownLatch;

import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.services.IServerInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Detecting if the server is still running every 10 seconds. And update the cached server state
 * @author juntaod
 *
 */
public class ServerStateThread implements Runnable {
	private Logger logger = LoggerFactory.getLogger(NodeStateThread.class);
	private IServerInfoService serverService = ServiceLocator.getService(IServerInfoService.class);
	@Override
	public void run() {
			try{
				TargetServerInfo[] servers = serverService.getAllServers().values().toArray(new TargetServerInfo[0]);
				if(servers.length > 0){
					CountDownLatch countDown = new CountDownLatch(servers.length);
					for(int i = 0; i < servers.length; i ++){
						TargetServerInfo server = servers[i];
						new Thread(new ConnectThread(server, countDown)).start();
					}
					try {
						countDown.await();
					} 
					catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			finally{
				try {
					Thread.sleep(10000);
				} 
				catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
	}
}

class ConnectThread implements Runnable{
	private IClientInfoService clientInfoService = ServiceLocator.getService(IClientInfoService.class);
	private TargetServerInfo server;
	private CountDownLatch countDown;
	public ConnectThread(TargetServerInfo server, CountDownLatch countDown){
		this.server = server;
		this.countDown = countDown;
	}
	@Override
	public void run() {
		try{
			boolean serverAlive = clientInfoService.isServerLive(server);
			if(!serverAlive){
				server.setNodeAlive(false);
			}
			server.setAlive(serverAlive);
		}
		finally{
			countDown.countDown();
		}
	}
	
}
