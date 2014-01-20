package net.juniper.jmp.monitor.servlet;

import java.util.Iterator;

import net.juniper.jmp.core.locator.SpringWebContextHelper;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.services.IServerInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detecting if the node is still running every 10 seconds. And update the cached server state
 * @author juntaod
 *
 */
public class NodeStateThread implements Runnable {
	private Logger logger = LoggerFactory.getLogger(NodeStateThread.class);
	@Override
	public void run() {
		IServerInfoService serverService = SpringWebContextHelper.getService(IServerInfoService.class);
		IClientInfoService clientInfoService = SpringWebContextHelper.getService(IClientInfoService.class);
		while(true){
			try{
				Iterator<TargetServerInfo> it = serverService.getAllServers().values().iterator();
				while(it.hasNext()){
					TargetServerInfo server = it.next();
					boolean nodeAlive = clientInfoService.isNodeLive(server);
					logger.info("node is alive:" + nodeAlive + ", ip:" + server.getAddress());
					if(nodeAlive){
						server.setNodeAlive(true);
					}
					else{
						//one more chance, for node may be too busy to response
						if(server.isNodeAlive()){
							if(server.getFailCount() == 0){
								server.setFailCount(1);
							}
							else{
								server.setFailCount(0);
								server.setNodeAlive(false);
							}
						}
						else{
							server.setNodeAlive(false);
						}
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

}
