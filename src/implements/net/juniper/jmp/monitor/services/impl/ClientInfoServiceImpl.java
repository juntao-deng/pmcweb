package net.juniper.jmp.monitor.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.proxy.HttpProxy;
import net.juniper.jmp.monitor.services.IClientInfoService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
/**
 * 
 * @author juntaod
 *
 */
@Service(value="net.juniper.jmp.monitor.services.IClientInfoService")
public class ClientInfoServiceImpl implements IClientInfoService{
	static final String SERVICE_NAME = "net.juniper.jmp.monitor.service.IMonitorService";
	private Logger logger = Logger.getLogger(ClientInfoServiceImpl.class);
	public Map<TargetServerInfo, Object> getThreadInfos(List<TargetServerInfo> servers){
		return callService(servers, "getThreadInfos", null);
	}

	private Map<TargetServerInfo, Object> callService(List<TargetServerInfo> servers, String methodName, Object[] params){
		return callService(servers, methodName, params, -1);
	}
	
	private Map<TargetServerInfo, Object> callService(List<TargetServerInfo> servers, String methodName, Object[] params, int timeout){
		if(servers == null || servers.size() == 0)
			return null;
		CountDownLatch countDown = new CountDownLatch(servers.size());
		Iterator<TargetServerInfo> it = servers.iterator();
		Map<TargetServerInfo, Object> resultsMap = new HashMap<TargetServerInfo, Object>();
		while(it.hasNext()){
			TargetServerInfo serverInfo = it.next();
			if(!serverInfo.isAlive()){
				countDown.countDown();
				resultsMap.put(serverInfo, null);
				continue;
			}
			new Thread(new HttpRequestThread(countDown, serverInfo, methodName, params, timeout, resultsMap)).start();
		}
		try {
			countDown.await();
		} 
		catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return resultsMap;
	}
	
	@Override
	public Map<TargetServerInfo, Object> getCpuInfo(List<TargetServerInfo> servers) {
		return callService(servers, "getCpuInfo", null);
	}

	@Override
	public Map<TargetServerInfo, Object> getMemInfo(List<TargetServerInfo> servers) {
		return callService(servers, "getMemInfo", null);
	}

	@Override
	public Map<TargetServerInfo, Object> getThreadInfos(List<TargetServerInfo> servers, String startTime, String endTime) {
		return callService(servers, "getPeriodThreadInfos", new Object[]{startTime, endTime});
	}

	@Override
	public boolean isNodeLive(TargetServerInfo server) {
		List<TargetServerInfo> list = new ArrayList<TargetServerInfo>();
		list.add(server);
		Map<TargetServerInfo, Object> results = callService(list, "getState", null, 10000);
		return results.get(server) != null;
	}
	
	@Override
	public boolean isServerLive(TargetServerInfo server) {
		try {
			byte[] results = HttpProxy.getInstance(server).request(SERVICE_NAME, "getServerState", null, 10000);
			if(results == null)
				return false;
			return true;
		} 
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
}

class HttpRequestThread implements Runnable{
	private Logger logger = Logger.getLogger(HttpRequestThread.class);
	private CountDownLatch countDown;
	private Map<TargetServerInfo, Object> resultsMap;
	private TargetServerInfo serverInfo;
	private String methodName;
	private Object[] params;
	private int timeout;
	public HttpRequestThread(CountDownLatch countDown, TargetServerInfo serverInfo, String methodName, Object[] params, int timeout, Map<TargetServerInfo, Object> map){
		this.countDown = countDown;
		this.resultsMap = map;
		this.serverInfo = serverInfo;
		this.methodName = methodName;
		this.params = params;
		this.timeout = timeout;
	}
	@Override
	public void run() {
		ObjectInputStream objInput = null;
		try {
			byte[] results = HttpProxy.getInstance(serverInfo).request(ClientInfoServiceImpl.SERVICE_NAME, methodName, params, timeout);
			if(results == null)
				resultsMap.put(serverInfo, null);
			else{
				objInput = new ObjectInputStream(new ByteArrayInputStream(results));
				resultsMap.put(serverInfo, objInput.readObject());
			}
		} 
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			resultsMap.put(serverInfo, null);
		}
		finally{
			countDown.countDown();
			if(objInput != null){
				try {
					objInput.close();
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
}
