package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.sys.MonitorInfo;
import net.juniper.jmp.tracer.dumper.info.AbstractDumpObject;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;
import net.juniper.jmp.tracer.dumper.info.ThreadInfoDump;

import org.apache.log4j.Logger;
/**
 * 
 * @author juntaod
 *
 */
public abstract class AbstractMonitorInfoRestService {
	private Logger logger = Logger.getLogger(AbstractMonitorInfoRestService.class);
	private Map<String, ThreadInfoDump> asyncIdThreadsMap = new HashMap<String, ThreadInfoDump>();
	private Map<String, ThreadInfoDump> callIdThreadsMap = new HashMap<String, ThreadInfoDump>();
	private Map<String, List<StageInfoBaseDump>> attachThreads = new HashMap<String, List<StageInfoBaseDump>>();
	
	/**
	 * get all serverinfos according to client request
	 * @param ips
	 * @return
	 */
	protected List<TargetServerInfo> getServers(String[] ips) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		for(String ip : ips) {
			TargetServerInfo server = serverMap.get(ip);
			if(server != null)
				serverList.add(server);
		}
		return serverList;
	}
	
	/**
	 * bind server information to the dumped objects
	 * @param server
	 * @param result
	 */
	protected void bindServer(TargetServerInfo server, AbstractDumpObject[] result) {
		if(result != null && result.length > 0){
			for(AbstractDumpObject s : result){
				s.setUserObject(server);
			}
		}
	}
	
	protected void addAsyncSummary(StageInfoBaseDump[] stageInfoBaseDumps){
		for(int i = 0; i < stageInfoBaseDumps.length; i ++){
			StageInfoBaseDump stage = stageInfoBaseDumps[i];
			String callId = stage.getCallId();
			Iterator<Entry<String, List<StageInfoBaseDump>>> entryIt = attachThreads.entrySet().iterator();
			while(entryIt.hasNext()){
				Entry<String, List<StageInfoBaseDump>> entry = entryIt.next();
				String key = entry.getKey();
				if(key.startsWith(callId)){
					increaseParent(stage, entry.getValue());
				}
			}
		}
	}
	
	protected List<StageInfoBaseDump> getAsyncChildren(String callId){
		return attachThreads.get(callId);
	}
	
	private void increaseParent(StageInfoBaseDump stage, List<StageInfoBaseDump> stageList) {
		Iterator<StageInfoBaseDump> stageIt = stageList.iterator();
		while(stageIt.hasNext()){
			StageInfoBaseDump s = stageIt.next();
			stage.setSumSqlCount(stage.getSumSqlCount() + s.getSumSqlCount());
			stage.setSumStageCount(stage.getSumStageCount() + s.getSumStageCount() + 1);
		}
	}

	/**
	 * Extract async parent and async children, and reorganize them if needed
	 * @param threadList
	 */
	protected void reorganizeAsyncResult(List<ThreadInfoDump> threadList) {
		asyncIdThreadsMap.clear();
		callIdThreadsMap.clear();
		attachThreads.clear();
		Iterator<ThreadInfoDump> it = threadList.iterator();
		boolean needRemoveEnded = isNeedRemoveEnded();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			if(thread.getAsyncId() != null){
				asyncIdThreadsMap.put(thread.getAsyncId(), thread);
				callIdThreadsMap.put(thread.getCallId(), thread);
			}
			if(needRemoveEnded && thread.isAlreadyEnded())
				it.remove();
		}
		
		it = threadList.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			if(thread.getAttachToAsyncId() != null){
				ThreadInfoDump t = asyncIdThreadsMap.get(thread.getAttachToAsyncId());
				if(t == null){
					logger.error("can not find owner thread for attach id:" + thread.getAttachToAsyncId());
					continue;
				}
				List<StageInfoBaseDump> stageList = attachThreads.get(t.getAsyncCallId());
				if(stageList == null){
					stageList = new ArrayList<StageInfoBaseDump>();
					attachThreads.put(t.getAsyncCallId(), stageList);
				}
				stageList.add(thread);
				it.remove();
			}
		}
		
		//Get back the removed but effective parent thread
		if(needRemoveEnded){
			String[] keys = attachThreads.keySet().toArray(new String[0]);
			Set<String> keySet = callIdThreadsMap.keySet();
			for(int i = 0; i < keys.length; i ++){
				String key = keys[i];
				if(keySet.contains(key)){
					ThreadInfoDump thread = callIdThreadsMap.get(key);
					if(thread.isAlreadyEnded()){
						threadList.add(thread);
					}
				}
			}
		}
	}

	protected boolean isNeedRemoveEnded() {
		return false;
	}

	/**
	 * get stage by id in all stage array and their children
	 * @param array
	 * @param asyncCallId
	 * @return
	 */
	protected StageInfoBaseDump doGetChildrenStage(StageInfoBaseDump[] stages, String stageId){
		throw new RuntimeException("not implemented");
	}
}
