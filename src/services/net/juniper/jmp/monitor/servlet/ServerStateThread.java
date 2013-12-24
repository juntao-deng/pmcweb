package net.juniper.jmp.monitor.servlet;

import java.util.Iterator;

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
	private IClientInfoService clientInfoService = ServiceLocator.getService(IClientInfoService.class);
	@Override
	public void run() {
		while(true){
			try{
				Iterator<TargetServerInfo> it = serverService.getAllServers().values().iterator();
				while(it.hasNext()){
					TargetServerInfo server = it.next();
					boolean serverAlive = clientInfoService.isServerLive(server);
					if(!serverAlive){
						server.setNodeAlive(false);
					}
					server.setAlive(serverAlive);
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
}
