package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.PathSegment;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.info.dump.StageInfoBaseDump;
import net.juniper.jmp.monitor.info.dump.ThreadInfoDump;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ThreadInfoHisRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;
/**
 * 
 * @author juntaod
 *
 */
public class ThreadInfoHisRestServiceImpl implements ThreadInfoHisRestService {
	private static final String THREADHISINFOS = "threadhisinfos";
	private IClientInfoService service = ServiceLocator.getService(IClientInfoService.class);
	@Override
	public PageResult<ThreadInfoDump> getThreadInfos(String startTs, String endTs) {
		String ipstr = ApiContext.getParameter("ips");
		String[] ips = ipstr.split(",");
		List<TargetServerInfo> servers = getServers(ips);
//		String startTs = ApiContext.getParameter("startts");
//		String endTs = ApiContext.getParameter("endts");
		Map<TargetServerInfo, Object> reqResults = service.getPeriodThreadInfos(servers, startTs, endTs);
		List<ThreadInfoDump> dr = new ArrayList<ThreadInfoDump>();
		List<ThreadInfoDump> or = new ArrayList<ThreadInfoDump>();
		Iterator<Entry<TargetServerInfo, Object>> it = reqResults.entrySet().iterator();
		while(it.hasNext()){
			Entry<TargetServerInfo, Object> entry = it.next();
			ThreadInfoDump[] result = (ThreadInfoDump[]) entry.getValue();
			if(result != null){
				or.addAll(Arrays.asList(result));
				dr.addAll(detachResult(or));
			}
		}
//		Collections.sort(dr, new Comparator<ThreadInfoDump>(){
//			@Override
//			public int compare(ThreadInfoDump o1, ThreadInfoDump o2) {
//				if(o1.getDuration() > o2.getDuration())
//					return -1;
//				else if(o1.getDuration() < o2.getDuration())
//					return 1;
//				return 0;
//			}
//		});
		ApiContext.getGlobalSessionCache().addCache(THREADHISINFOS, or);
		return new PageResult<ThreadInfoDump>(dr);
	}

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

	private List<TargetServerInfo> getServers(String[] ips) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		for(String ip : ips) {
			TargetServerInfo server = serverMap.get(ip);
			if(server != null)
				serverList.add(server);
		}
		return serverList;
	}
	
	@Override
	public ThreadInfoDump getThreadInfo(String id) {
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
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
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
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
					return t.getChildrenStages();
				}
				else{
					StageInfoBaseDump[] slist = t.getChildrenStages();
					if(slist != null){
						StageInfoBaseDump[] result = doGetChildrenStages(slist, id);
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
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
		if(dr == null)
			return null;
		Iterator<ThreadInfoDump> it = dr.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			StageInfoBaseDump[] slist = thread.getChildrenStages();
			if(slist != null){
				StageInfoBaseDump result = doGetChildrenStage(slist, sid);
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
			StageInfoBaseDump[] clist = s.getChildrenStages();
			if(clist != null){
				StageInfoBaseDump result = doGetChildrenStage(clist, sid);
				if(result != null)
					return result;
			}
		}
		return null;
	}
}
