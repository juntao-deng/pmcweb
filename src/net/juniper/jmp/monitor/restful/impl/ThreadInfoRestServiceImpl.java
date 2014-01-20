package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.PathSegment;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.core.locator.SpringWebContextHelper;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ThreadInfoRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;
import net.juniper.jmp.tracer.dumper.info.ThreadInfoDump;

import org.springframework.stereotype.Service;
/**
 * 
 * @author juntaod
 *
 */
@Service(value="net.juniper.jmp.monitor.restful.ThreadInfoRestService")
public class ThreadInfoRestServiceImpl extends AbstractMonitorInfoRestService implements ThreadInfoRestService{
	private static final String THREADINFOS = "threadinfos";
	
	@Override
	public Page<ThreadInfoDump> getThreadInfos() {
		String ipstr = ApiContext.getParameter("ips");
		String[] ips = ipstr.split(",");
		List<TargetServerInfo> servers = getServers(ips);
		IClientInfoService clientService = SpringWebContextHelper.getService(IClientInfoService.class);
		Map<TargetServerInfo, Object> reqResults = clientService.getThreadInfos(servers);
		List<ThreadInfoDump> or = new ArrayList<ThreadInfoDump>();
		Iterator<Entry<TargetServerInfo, Object>> it = reqResults.entrySet().iterator();
		while(it.hasNext()){
			Entry<TargetServerInfo, Object> entry = it.next();
			ThreadInfoDump[] result = (ThreadInfoDump[]) entry.getValue();
			if(result != null){
				or.addAll(Arrays.asList(result));
			}
		}
//		reorganizeAsyncResult(or);
//		addAsyncSummary(or.toArray(new StageInfoBaseDump[0]));
		
		List<ThreadInfoDump> dr = new ArrayList<ThreadInfoDump>();
		dr.addAll(detachResult(or));
		Collections.sort(dr, new Comparator<ThreadInfoDump>(){
			@Override
			public int compare(ThreadInfoDump o1, ThreadInfoDump o2) {
				if(o1.getDuration() > o2.getDuration())
					return -1;
				else if(o1.getDuration() < o2.getDuration())
					return 1;
				return 0;
			}
		});
		ApiContext.getGlobalSessionCache().addCache(THREADINFOS, or);
		return new Page<ThreadInfoDump>(dr);
	}

//	@Override
//	protected void reorganizeAsyncResult(List<ThreadInfoDump> or) {
//		Iterator<ThreadInfoDump> it = or.iterator();
//		Map<String, ThreadInfoDump> asyncThreads = new HashMap<String, ThreadInfoDump>();
//		Map<String, ThreadInfoDump> attachThreads = new HashMap<String, ThreadInfoDump>();
//		while(it.hasNext()){
//			ThreadInfoDump thread = it.next();
//			boolean needRemove = false;
//			if(thread.getAsyncId() != null){
//				if(thread.isAlreadyEnded())
//					needRemove = true;
//				asyncThreads.put(thread.getAsyncId(), thread);
//			}
//			else if(thread.getAttachToAsyncId() != null){
//				needRemove = true;
//				attachThreads.put(thread.getAttachToAsyncId(), thread);
//			}
//			if(needRemove)
//				it.remove();
//		}
//		Iterator<Entry<String, ThreadInfoDump>> attachIt = attachThreads.entrySet().iterator();
//		while(attachIt.hasNext()){
//			Entry<String, ThreadInfoDump> attach = attachIt.next();
//			String attachId = attach.getKey();
//			ThreadInfoDump attachThread = attach.getValue();
//			ThreadInfoDump asyncThread = asyncThreads.get(attachId);
//			if(asyncThread != null){
//				asyncThread.setDuration(asyncThread.getDuration() + attachThread.getDuration());
//				if(asyncThread.isAlreadyEnded() && !or.contains(asyncThread)){
//					or.add(asyncThread);
//				}
//				
//				if(asyncThread.getAsyncCallId().equals(asyncThread.getCallId())){
//					asyncThread.addChildStage(attachThread);
//				}
//				else{
//					List<StageInfoBaseDump> slist = asyncThread.getChildrenStages();
//					if(slist != null){
//						StageInfoBaseDump result = doGetChildrenStage(slist.toArray(new StageInfoBaseDump[0]), asyncThread.getAsyncCallId());
//						if(result != null){
//							result.addChildStage(attachThread);
//						}
//					}
//					
//				}
//			}
//		}
//	}

	private List<ThreadInfoDump> detachResult(List<ThreadInfoDump> result) {
		List<ThreadInfoDump> infoList = new ArrayList<ThreadInfoDump>();
		Iterator<ThreadInfoDump> it = result.iterator();
		while(it.hasNext()){
			ThreadInfoDump ti = it.next();
			ThreadInfoDump dti = (ThreadInfoDump) ti.detach();
			infoList.add(dti);
		}
		return infoList;
	}

//	private List<TargetServerInfo> getServers(String[] ips) {
//		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
//		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
//		for(String ip : ips) {
//			TargetServerInfo server = serverMap.get(ip);
//			if(server != null)
//				serverList.add(server);
//		}
//		return serverList;
//	}
	
	@Override
	public ThreadInfoDump getThreadInfo(String id) {
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) ApiContext.getGlobalSessionCache().getCache(THREADINFOS);
		if(dr == null)
			return null;
		Iterator<ThreadInfoDump> it = dr.iterator();
		while(it.hasNext()){
			ThreadInfoDump t = it.next();
			if(t.getCallId().equals(id)){
				return t;
			}
		}
		return null;
	}

	@Override
	public void processAction(PathSegment seg) {

	}

	@Override
	public StageInfoBaseDump[] getStageInfos(String id) {
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) ApiContext.getGlobalSessionCache().getCache(THREADINFOS);
		if(dr == null)
			return null;
		StageInfoBaseDump[] result = doGetChildrenStages(dr.toArray(new ThreadInfoDump[0]), id);
		return result;
	}
	
	private StageInfoBaseDump[] doGetChildrenStages(StageInfoBaseDump[] stages, String id){
		if(stages != null){
			for(int i = 0; i < stages.length; i ++){
				StageInfoBaseDump t = stages[i];
				if(t.getCallId().equals(id)){
					List<StageInfoBaseDump> clist = t.getChildrenStages();
					if(clist == null){
						clist = new ArrayList<StageInfoBaseDump>();
						t.setChildrenStages(clist);
					}
//					this.addAndIncreaseAsyncRequestedChildren(t);
					return clist.toArray(new StageInfoBaseDump[0]);
				}
				else{
					List<StageInfoBaseDump> slist = t.getChildrenStages();
					if(slist != null){
						StageInfoBaseDump[] result = doGetChildrenStages(slist.toArray(new StageInfoBaseDump[0]), id);
						if(result != null)
							return result;
					}
				}
			}
		}
		return null;
	}

	@Override
	public StageInfoBaseDump getStageInfo(String id, String sid) {
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) ApiContext.getGlobalSessionCache().getCache(THREADINFOS);
		if(dr == null)
			return null;
		Iterator<ThreadInfoDump> it = dr.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			if(!thread.getCallId().equals(id))
				continue;
			List<StageInfoBaseDump> slist = thread.getChildrenStages();
			if(slist != null){
				StageInfoBaseDump result = doGetChildrenStage(slist.toArray(new StageInfoBaseDump[0]), sid);
				if(result != null)
					return result;
			}
		}
		return null;
	}

	private StageInfoBaseDump doGetChildrenStage(StageInfoBaseDump[] slist, String sid) {
		for(int i = 0; i < slist.length; i ++){
			StageInfoBaseDump s = slist[i];
			if(s.getCallId().equals(sid))
				return s;
			List<StageInfoBaseDump> clist = s.getChildrenStages();
			if(clist != null){
				StageInfoBaseDump result = doGetChildrenStage(clist.toArray(new StageInfoBaseDump[0]), sid);
				if(result != null)
					return result;
			}
		}
		return null;
	}
	
}
