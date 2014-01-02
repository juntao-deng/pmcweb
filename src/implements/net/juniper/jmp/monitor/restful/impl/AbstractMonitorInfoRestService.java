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
		Map<String, List<StageInfoBaseDump>> attachThreads = (Map<String, List<StageInfoBaseDump>>) ApiContext.getGlobalSessionCache().getCache(ATTACHTHREADS);
		for(int i = 0; i < stages.length; i ++){
			StageInfoBaseDump stage = stages[i];
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
	
	protected void addAndIncreaseAsyncRequestedChildren(StageInfoBaseDump stage){
		Map<String, List<StageInfoBaseDump>> attachThreads = (Map<String, List<StageInfoBaseDump>>) ApiContext.getGlobalSessionCache().getCache(ATTACHTHREADS);
		List<StageInfoBaseDump> attachList = attachThreads.get(stage.getCallId());
		if(attachList != null){
			Iterator<StageInfoBaseDump> attachIt = attachList.iterator();
			while(attachIt.hasNext())
				stage.addChildStage(attachIt.next());
		}
		List<StageInfoBaseDump> clist = stage.getChildrenStages();
		if(clist.size() > 0)
			addAsyncSummary(clist.toArray(new StageInfoBaseDump[0]));
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
//			if(needRemoveEnded && thread.isAlreadyEnded())
//				it.remove();
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
				List<StageInfoBaseDump> stageList = attachThreads.get(t.getAsyncCallId());
				if(stageList == null){
					stageList = new ArrayList<StageInfoBaseDump>();
					attachThreads.put(t.getAsyncCallId(), stageList);
				}
				stageList.add(thread);
			}
		}
		
		boolean needRemoveEnded = isNeedRemoveEnded();
		if(needRemoveEnded){
			Set<String> keys = attachThreads.keySet();
//			Set<String> keySet = callIdThreadsMap.keySet();
			it = threadList.iterator();
			while(it.hasNext()){
				ThreadInfoDump thread = it.next();
				if(thread.isAlreadyEnded()){
					String callId = thread.getCallId();
					if(!keys.contains(callId) || thread.getAsyncId() != null){
						it.remove();
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
