package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ThreadInfoHisRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;
import net.juniper.jmp.tracer.dumper.info.ThreadInfoDump;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
/**
 * 
 * @author juntaod
 *
 */
public class ThreadInfoHisRestServiceImpl extends AbstractMonitorInfoRestService implements ThreadInfoHisRestService {
	private static final String THREADHISINFOS = "threadhisinfos";
	private IClientInfoService service = ServiceLocator.getService(IClientInfoService.class);
	@Override
	public PageResult<ThreadInfoDump> getThreadInfos(String startTs, String endTs) {
		String ipstr = ApiContext.getParameter("ips");
		String fetchType = ApiContext.getParameter("fetchType");
		if(fetchType == null)
			fetchType = "";
//		String cache = THREADHISINFOS + fetchType;
		if(endTs == null || endTs.equals("")){
			endTs = "01/01/2020 00:00";
		}
		String cacheKey = ipstr + startTs + endTs + fetchType;
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
		if(cacheObj == null || !cacheObj.key.equals(cacheKey)){
			
			String[] ips = ipstr.split(",");
			List<TargetServerInfo> servers = getServers(ips);
	//		String startTs = ApiContext.getParameter("startts");
	//		String endTs = ApiContext.getParameter("endts");
			Map<TargetServerInfo, Object> reqResults = service.getPeriodThreadInfos(servers, startTs, endTs, fetchType);
			List<ThreadInfoDump> or = new ArrayList<ThreadInfoDump>();
			Iterator<Entry<TargetServerInfo, Object>> it = reqResults.entrySet().iterator();
			while(it.hasNext()){
				Entry<TargetServerInfo, Object> entry = it.next();
				ThreadInfoDump[] result = (ThreadInfoDump[]) entry.getValue();
				if(result != null){
					bindServer(entry.getKey(), result);
					or.addAll(Arrays.asList(result));
	//				dr.addAll(detachResult(or));
				}
			}
			
			processForFetchType(fetchType, or);
			reorganizeAsyncResult(or);
			addAsyncSummary(or.toArray(new StageInfoBaseDump[0]));
			cacheObj = new CacheObject();
			cacheObj.key = cacheKey;
			cacheObj.list = or;
			ApiContext.getGlobalSessionCache().addCache(THREADHISINFOS, cacheObj);
		}
		
		List<ThreadInfoDump> or = cacheObj.list;
		Pageable p = ApiContext.getPagingContext().getPageable();
		int pageIndex = p.getPageNumber();
		int pageSize = p.getPageSize();
		int jump = pageIndex * pageSize;
		List<ThreadInfoDump> pageResults = new ArrayList<ThreadInfoDump>();
		int totalSize = or.size();
		for(int i = jump; i < (jump + pageSize) && i < totalSize; i ++){
			pageResults.add(or.get(i).detach());
		}
		Page<ThreadInfoDump> page = new PageImpl<ThreadInfoDump>(pageResults, p, totalSize);
		return new PageResult<ThreadInfoDump>(page);
	}

	private void processForFetchType(String fetchType, List<ThreadInfoDump> or) {
		if(fetchType.equals("")){
			Collections.sort(or, new Comparator<ThreadInfoDump>(){
				@Override
				public int compare(ThreadInfoDump o1, ThreadInfoDump o2) {
					return o1.getEndTs().compareTo(o2.getEndTs());
				}
			});
		}
	}
	
	@Override
	public ThreadInfoDump getThreadInfo(String id) {
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
		if(cacheObj == null)
			return null;
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) cacheObj.list;
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
	public StageInfoBaseDump[] getStageInfos(String id) {
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
		if(cacheObj == null)
			return null;
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) cacheObj.list;
		if(dr == null)
			return null;
		StageInfoBaseDump[] result = doGetChildrenStages(dr.toArray(new ThreadInfoDump[0]), id);
		return result;
	}
	
	private StageInfoBaseDump[] doGetChildrenStages(StageInfoBaseDump[] stages, String id){
		if(stages != null){
			for(int i = 0; i < stages.length; i ++){
				StageInfoBaseDump stage = stages[i];
				if(stage.getCallId().equals(id)){
					if(stage.getSumStageCount() == 0)
						return null;
					List<StageInfoBaseDump> clist = stage.getChildrenStages();
					if(clist == null){
						TargetServerInfo server = (TargetServerInfo) stage.getUserObject();
						StageInfoBaseDump[] cstages = service.getStagesByParentId(server, stage.getCallId());
						clist = new ArrayList<StageInfoBaseDump>();
						if(cstages != null){
							bindServer(server, cstages);
							clist.addAll(Arrays.asList(cstages));
						}
						List<StageInfoBaseDump> asyncList = this.getAsyncChildren(id);
						if(asyncList != null)
							clist.addAll(asyncList);
						if(clist.size() > 0)
							addAsyncSummary(clist.toArray(new StageInfoBaseDump[0]));
						stage.setChildrenStages(clist);
					}
					return detachStages(clist);
				}
				else{
					List<StageInfoBaseDump> slist = stage.getChildrenStages();
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

	private StageInfoBaseDump[] detachStages(List<StageInfoBaseDump> list) {
		if(list == null)
			return null;
		List<StageInfoBaseDump> stageList = new ArrayList<StageInfoBaseDump>();
		Iterator<StageInfoBaseDump> it = list.iterator();
		while(it.hasNext()){
			stageList.add(it.next().detach());
		}
		
		return stageList.toArray(new StageInfoBaseDump[0]);
	}

	@Override
	public StageInfoBaseDump getStageInfo(String id, String sid) {
//		String fetchType = ApiContext.getParameter("fetchType");
//		if(fetchType == null)
//			fetchType = "";
//		String cache = THREADHISINFOS + fetchType;
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADHISINFOS);
		if(cacheObj == null)
			return null;
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) cacheObj.list;
		if(dr == null)
			return null;
		Iterator<ThreadInfoDump> it = dr.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
			List<StageInfoBaseDump> slist = thread.getChildrenStages();
			if(slist != null){
				StageInfoBaseDump result = doGetChildrenStage(slist.toArray(new StageInfoBaseDump[0]), sid);
				if(result != null)
					return result.detach();
			}
		}
		return null;
	}

	/**
	 *	@Override
	 */
	protected StageInfoBaseDump doGetChildrenStage(StageInfoBaseDump[] slist, String sid) {
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
	
	class CacheObject{
		protected String key;
		protected List<ThreadInfoDump> list;
	}

	@Override
	public Object processAction(String action, MultivaluedMap<String, String> form) {
		return null;
	}
}
