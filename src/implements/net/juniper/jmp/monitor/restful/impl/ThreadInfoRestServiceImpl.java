package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.PathSegment;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.info.StageInfoBase;
import net.juniper.jmp.monitor.info.ThreadInfo;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ThreadInfoRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;
/**
 * 
 * @author juntaod
 *
 */
public class ThreadInfoRestServiceImpl implements ThreadInfoRestService {
	private static final String THREADINFOS = "threadinfos";
	private IClientInfoService service = ServiceLocator.getService(IClientInfoService.class);
	@Override
	public PageResult<ThreadInfo> getThreadInfos() {
		String ipstr = ApiContext.getParameter("ips");
		String[] ips = ipstr.split(",");
		List<TargetServerInfo> servers = getServers(ips);
		Map<TargetServerInfo, Object> reqResults = service.getThreadInfos(servers);
		List<ThreadInfo> dr = new ArrayList<ThreadInfo>();
		List<ThreadInfo> or = new ArrayList<ThreadInfo>();
		Iterator<Entry<TargetServerInfo, Object>> it = reqResults.entrySet().iterator();
		while(it.hasNext()){
			Entry<TargetServerInfo, Object> entry = it.next();
			List<ThreadInfo> result = (List<ThreadInfo>) entry.getValue();
			if(result != null){
				or.addAll(result);
				dr.addAll(detachResult(result));
			}
		}
		Collections.sort(dr, new Comparator<ThreadInfo>(){
			@Override
			public int compare(ThreadInfo o1, ThreadInfo o2) {
				if(o1.getDuration() > o2.getDuration())
					return -1;
				else if(o1.getDuration() < o2.getDuration())
					return 1;
				return 0;
			}
		});
		ApiContext.getGlobalSessionCache().addCache(THREADINFOS, or);
		return new PageResult<ThreadInfo>(dr);
	}

	private List<ThreadInfo> detachResult(List<ThreadInfo> result) {
		List<ThreadInfo> infoList = new ArrayList<ThreadInfo>();
		Iterator<ThreadInfo> it = result.iterator();
		while(it.hasNext()){
			ThreadInfo ti = it.next();
			ThreadInfo dti = (ThreadInfo) ti.detach();
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
	public ThreadInfo getThreadInfo(String id) {
		List<ThreadInfo> dr = (List<ThreadInfo>) ApiContext.getGlobalSessionCache().getCache(THREADINFOS);
		if(dr == null)
			return null;
		Iterator<ThreadInfo> it = dr.iterator();
		while(it.hasNext()){
			ThreadInfo t = it.next();
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
	public StageInfoBase[] getStageInfos(String id) {
		List<ThreadInfo> dr = (List<ThreadInfo>) ApiContext.getGlobalSessionCache().getCache(THREADINFOS);
		if(dr == null)
			return null;
		StageInfoBase[] result = doGetChildrenStages(dr, id);
		return result;
	}
	
	private StageInfoBase[] doGetChildrenStages(List<? extends StageInfoBase> stages, String id){
		Iterator<? extends StageInfoBase> it = stages.iterator();
		while(it.hasNext()){
			StageInfoBase t = it.next();
			if(t.getCallId().equals(id)){
				List<StageInfoBase> slist = t.getChildrenStageList();
				return slist == null ? new StageInfoBase[0] : slist.toArray(new StageInfoBase[0]);
			}
			else{
				List<StageInfoBase> slist = t.getChildrenStageList();
				if(slist != null){
					StageInfoBase[] result = doGetChildrenStages(slist, id);
					if(result != null)
						return result;
				}
			}
		}
		return null;
	}

	@Override
	public StageInfoBase getStageInfo(String id, String sid) {
		List<ThreadInfo> dr = (List<ThreadInfo>) ApiContext.getGlobalSessionCache().getCache(THREADINFOS);
		if(dr == null)
			return null;
		Iterator<ThreadInfo> it = dr.iterator();
		while(it.hasNext()){
			StageInfoBase stage = it.next();
			List<StageInfoBase> slist = stage.getChildrenStageList();
			if(slist != null){
				StageInfoBase result = doGetChildrenStage(slist, sid);
				if(result != null)
					return result;
			}
		}
		return null;
	}

	private StageInfoBase doGetChildrenStage(List<StageInfoBase> slist, String sid) {
		Iterator<StageInfoBase> it = slist.iterator();
		while(it.hasNext()){
			StageInfoBase s = it.next();
			if(s.getCallId().equals(sid))
				return s;
			List<StageInfoBase> clist = s.getChildrenStageList();
			if(clist != null){
				StageInfoBase result = doGetChildrenStage(clist, sid);
				if(result != null)
					return result;
			}
		}
		return null;
	}
}
