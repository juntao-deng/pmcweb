package net.juniper.jmp.monitor.services.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.proxy.HttpProxy;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author juntaod
 *
 */
public class ClientInfoServiceImpl implements IClientInfoService{
	static final String SERVICE_NAME = "net.juniper.jmp.monitor.service.IMonitorService";
	private Logger logger = LoggerFactory.getLogger(ClientInfoServiceImpl.class);
	
	@Override
	public Map<TargetServerInfo, Object> getThreadInfos(List<TargetServerInfo> servers){
		return callService(servers, "getThreadInfos", null);
	}
	
	private Object callService(TargetServerInfo server, String methodName, Object[] params){
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		serverList.add(server);
		Map<TargetServerInfo, Object> resultMap = callService(serverList, methodName, params);
		return resultMap == null ? null : resultMap.get(server);
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
	public Map<TargetServerInfo, Object> getPeriodThreadInfos(List<TargetServerInfo> servers, String startTime, String endTime, String whereClause) {
		return callService(servers, "getPeriodThreadInfos", new Object[]{startTime, endTime, whereClause});
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
			Object results = HttpProxy.getInstance(server).request(SERVICE_NAME, "getServerState", null, 3000);
			if(results == null)
				return false;
			return true;
		} 
		catch (NoRouteToHostException e){
			logger.error("can not connect to server:" + server.getAddress());
			return false;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Map<TargetServerInfo, Object> startRecord(List<TargetServerInfo> servers, String recordId) {
		return callService(servers, "startRecord", new String[]{recordId}, 10000);
	}

	@Override
	public void endRecord(List<TargetServerInfo> servers, String recordId) {
		callService(servers, "endRecord", new String[]{recordId}, 10000);
//		return (ThreadInfoDump[]) results.get(server);
	}

	@Override
	public Map<TargetServerInfo, Object> getRecordThreadInfos(List<TargetServerInfo> servers, String recordId) {
		return callService(servers, "getRecordResult", new String[]{recordId}, 10000);
	}

	@Override
	public StageInfoBaseDump[] getStagesByParentId(TargetServerInfo server, String callId) {
		return (StageInfoBaseDump[]) callService(server, "getStagesByParentId", new String[]{callId});
	}

	@Override
	public Map<TargetServerInfo, Object> getSqlInfos(List<TargetServerInfo> servers, String startTime, String endTime, String fetchType) {
		return callService(servers, "getSqlInfos", new String[]{startTime, endTime, fetchType});
	}

	@Override
	public StageInfoBaseDump getStageById(TargetServerInfo server, String callId) {
		return (StageInfoBaseDump) callService(server, "getStageById", new String[]{callId});
	}
}

class HttpRequestThread implements Runnable{
	private Logger logger = LoggerFactory.getLogger(HttpRequestThread.class);
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
			Object result = HttpProxy.getInstance(serverInfo).request(ClientInfoServiceImpl.SERVICE_NAME, methodName, params, timeout);
			resultsMap.put(serverInfo, result);
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
