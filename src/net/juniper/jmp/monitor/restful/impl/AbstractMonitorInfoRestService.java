package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.juniper.jmp.core.ctx.ApiContext;
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
	private static final String ASYNCIDTHREADS = "ASYNCIDTHREADS";
	private static final String CALLIDTHREADS = "CALLIDTHREADS";
	private static final String ATTACHTHREADS = "ATTACHTHREADS";
	
	/**
	 * get all serverinfos according to client request
	 * @param ips
	 * @return
	 */
	protected List<TargetServerInfo> getServers(String[] ips) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		String sesId = ApiContext.getSessionId();
		for(String ip : ips) {
			TargetServerInfo server = serverMap.get(ip);
			if(server != null){
				if(server.getSessionId() == null || server.getSessionId().equals(sesId))
					serverList.add(server);
				else
					logger.warn("can't monitor ip:" + ip + " for occupied by " + server.getOccupiedBy());
			}
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
	
	/**
	 * @param stages
	 */
	protected void addAsyncSummary(StageInfoBaseDump[] stages){
		for(int i = 0; i < stages.length; i ++){
			StageInfoBaseDump stage = stages[i];
			if(stage.isUpdated())
				continue;
			String callId = stage.getCallId();
			Map<String, List<StageInfoBaseDump>> attachThreads = (Map<String, List<StageInfoBaseDump>>) ApiContext.getGlobalSessionCache().getCache(ATTACHTHREADS);

			List<String> firstLevelKeys = new ArrayList<String>();
			
			Iterator<Entry<String, List<StageInfoBaseDump>>> entryIt = attachThreads.entrySet().iterator();
			while(entryIt.hasNext()){
				Entry<String, List<StageInfoBaseDump>> entry = entryIt.next();
				String key = entry.getKey();
				if(key.startsWith(callId)){
					List<StageInfoBaseDump> cStageList = entry.getValue();
					addAsyncSummary(cStageList.toArray(new StageInfoBaseDump[0]));
					firstLevelKeys.add(key);
					if(key.equals(callId)){
						stage.setStages(stage.getStages() + cStageList.size());
					}
				}
			}
			if(firstLevelKeys.size() > 0){
				calculateFirstLevel(firstLevelKeys);
				Iterator<String> keyIt = firstLevelKeys.iterator();
				while(keyIt.hasNext()){
					List<StageInfoBaseDump> cStageList = attachThreads.get(keyIt.next());
					increaseParent(stage, cStageList);
				}
			}
//			List<StageInfoBaseDump> cStageList = attachThreads.get(callId);
//			if(cStageList != null){
//				addAsyncSummary(cStageList.toArray(new StageInfoBaseDump[0]));
//			}
//			Iterator<Entry<String, List<StageInfoBaseDump>>> entryIt = attachThreads.entrySet().iterator();
//			while(entryIt.hasNext()){
//				Entry<String, List<StageInfoBaseDump>> entry = entryIt.next();
//				String key = entry.getKey();
//				if(key.startsWith(callId)){
//					hasAsync = true;
//				}
//			}
			stage.setUpdated(true);
		}
	}
	
//	private boolean hasAsynChildren(StageInfoBaseDump stage, Map<String, List<StageInfoBaseDump>> attachThreads) {
//		String[] keys = attachThreads.keySet().toArray(new String[0]);
//		for(int i = 0; i < keys.length; i ++){
//			if(keys[i].startsWith(stage.getCallId()))
//				return true;
//		}
//		return false;
//	}

	private void calculateFirstLevel(List<String> firstLevelKeys) {
		List<String> removeKeys = new ArrayList<String>();
		int size = firstLevelKeys.size();
		for(int i = 0; i < size; i ++){
			String key = firstLevelKeys.get(i);
			Iterator<String> keyIt = firstLevelKeys.iterator();
			while(keyIt.hasNext()){
				String k = keyIt.next();
				if(!key.equals(k) && key.startsWith(k)){
					removeKeys.add(key);
					break;
				}
			}
		}
		firstLevelKeys.removeAll(removeKeys);
	}

	protected void addAndIncreaseAsyncRequestedChildren(StageInfoBaseDump stage){
		Map<String, List<StageInfoBaseDump>> attachThreads = (Map<String, List<StageInfoBaseDump>>) ApiContext.getGlobalSessionCache().getCache(ATTACHTHREADS);
		List<StageInfoBaseDump> attachList = attachThreads.get(stage.getCallId());
		if(attachList != null){
			stage.getChildrenStages().addAll(attachList);
		}
		List<StageInfoBaseDump> clist = stage.getChildrenStages();
		if(clist.size() > 0)
			addAsyncSummary(clist.toArray(new StageInfoBaseDump[0]));
	}
	
	private void increaseParent(StageInfoBaseDump currStage, List<StageInfoBaseDump> stageList) {
		Iterator<StageInfoBaseDump> stageIt = stageList.iterator();
		while(stageIt.hasNext()){
			StageInfoBaseDump cstage = stageIt.next();
			currStage.setSumSqlCount(currStage.getSumSqlCount() + cstage.getSumSqlCount());
			currStage.setSumStageCount(currStage.getSumStageCount() + cstage.getSumStageCount() + 1);
			currStage.setDuration(currStage.getDuration() + cstage.getDuration());
		}
	}

	/**
	 * Extract async parent and async children, and reorganize them if needed
	 * @param threadList
	 */
	protected void reorganizeAsyncResult(List<ThreadInfoDump> threadList) {
		Map<String, ThreadInfoDump> asyncIdThreadsMap = new HashMap<String, ThreadInfoDump>();
		Map<String, List<StageInfoBaseDump>> attachThreads = new HashMap<String, List<StageInfoBaseDump>>();
		Map<String, ThreadInfoDump> callIdThreadsMap = new HashMap<String, ThreadInfoDump>();
		ApiContext.getGlobalSessionCache().addCache(ASYNCIDTHREADS, asyncIdThreadsMap);
		ApiContext.getGlobalSessionCache().addCache(ATTACHTHREADS, attachThreads);
		ApiContext.getGlobalSessionCache().addCache(CALLIDTHREADS, callIdThreadsMap);
		
		//find all async parent thread
		Iterator<ThreadInfoDump> it = threadList.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			if(thread.getAsyncId() != null){
				asyncIdThreadsMap.put(thread.getAsyncId(), thread);
				callIdThreadsMap.put(thread.getCallId(), thread);
			}
		}
		
		//find all async child thread
		it = threadList.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			String attachId = thread.getAttachToAsyncId();
			if(attachId != null){
				ThreadInfoDump t = asyncIdThreadsMap.get(attachId);
				if(t == null){
					logger.error("can not find owner thread for attach id:" + attachId);
					continue;
				}
				
				logger.error("======== find owner thread for attach id:" + attachId);
				String parentCallId = t.getAsyncCallId();
				List<StageInfoBaseDump> stageList = attachThreads.get(parentCallId);
				if(stageList == null){
					stageList = new ArrayList<StageInfoBaseDump>();
					attachThreads.put(parentCallId, stageList);
				}
				stageList.add(thread);
			}
		}
		
		Set<String> keys = attachThreads.keySet();
		it = threadList.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			if(thread.getAttachToAsyncId() != null){
				it.remove();
				continue;
			}
			String callId = thread.getCallId();
			if(thread.isAlreadyEnded()){
				if(!keys.contains(callId)){
					it.remove();
					continue;
				}
			}
//			List<StageInfoBaseDump> stageList = attachThreads.get(callId);
//			if(stageList != null)
//				thread.setStages(thread.getStages() + stageList.size());
		}
	}

//	protected boolean isNeedRemoveEnded() {
//		return false;
//	}

//	/**
//	 * get stage by id in all stage array and their children
//	 * @param array
//	 * @param asyncCallId
//	 * @return
//	 */
//	protected StageInfoBaseDump doGetChildrenStage(StageInfoBaseDump[] stages, String stageId){
//		throw new RuntimeException("not implemented");
//	}
}
