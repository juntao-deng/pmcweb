package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.core.ctx.Pageable;
import net.juniper.jmp.core.ctx.impl.PageImpl;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ThreadInfoActionRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;
import net.juniper.jmp.tracer.dumper.info.ThreadInfoDump;
/**
 * 
 * @author juntaod
 *
 */
public class ThreadInfoActionRestServiceImpl extends AbstractMonitorInfoRestService implements ThreadInfoActionRestService {
	private static final String THREADACTIONINFOS = "threadactioninfos";
	private static Integer RECORD_INDEX = 0;
	private IClientInfoService service;
	@Override
	public PageResult<ThreadInfoDump> getThreadInfos(String recordId) {
		String ipstr = ApiContext.getParameter("ips");
		String cacheKey = ipstr + recordId;
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADACTIONINFOS);
		if(cacheObj == null || !cacheObj.key.equals(cacheKey)){
			
			String[] ips = ipstr.split(",");
			List<TargetServerInfo> servers = getServers(ips);
	//		String startTs = ApiContext.getParameter("startts");
	//		String endTs = ApiContext.getParameter("endts");
			Map<TargetServerInfo, Object> reqResults = service.getRecordThreadInfos(servers, recordId);
			List<ThreadInfoDump> or = new ArrayList<ThreadInfoDump>();
			Iterator<Entry<TargetServerInfo, Object>> it = reqResults.entrySet().iterator();
			while(it.hasNext()){
				Entry<TargetServerInfo, Object> entry = it.next();
				List<ThreadInfoDump> result = (List<ThreadInfoDump>) entry.getValue();
				if(result != null){
					or.addAll(result);
	//				dr.addAll(detachResult(or));
				}
			}
			reorganizeAsyncResult(or);
			addAsyncSummary(or.toArray(new StageInfoBaseDump[0]));
			Collections.sort(or, new Comparator<ThreadInfoDump>(){
				@Override
				public int compare(ThreadInfoDump o1, ThreadInfoDump o2) {
					return o1.getEndTs().compareTo(o2.getEndTs());
				}
			});
			
			cacheObj = new CacheObject();
			cacheObj.key = cacheKey;
			cacheObj.list = or;
			ApiContext.getGlobalSessionCache().addCache(THREADACTIONINFOS, cacheObj);
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
	
	@Override
	public ThreadInfoDump getThreadInfo(String id) {
		CacheObject cache = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADACTIONINFOS);
		if(cache == null)
			return null;
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) cache.list;
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
		CacheObject cache = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADACTIONINFOS);
		if(cache == null)
			return null;
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) cache.list;
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
					List<StageInfoBaseDump> clist = stage.getChildrenStages();
					if(clist == null){
						clist = new ArrayList<StageInfoBaseDump>();
						stage.setChildrenStages(clist);
					}
					this.addAndIncreaseAsyncRequestedChildren(stage);
					return clist.toArray(new StageInfoBaseDump[0]);
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

	@Override
	public StageInfoBaseDump getStageInfo(String id, String sid) {
		CacheObject cache = (CacheObject) ApiContext.getGlobalSessionCache().getCache(THREADACTIONINFOS);
		if(cache == null)
			return null;
		List<ThreadInfoDump> dr = (List<ThreadInfoDump>) cache.list;
		if(dr == null)
			return null;
		Iterator<ThreadInfoDump> it = dr.iterator();
		while(it.hasNext()){
			ThreadInfoDump thread = it.next();
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
	
	class CacheObject{
		protected String key;
		protected List<ThreadInfoDump> list;
	}

	@Override
	public Object processAction(String action, MultivaluedMap<String, String> form) {
		if(action.equals("startRecord")){
			List<TargetServerInfo> servers = getServersFromForm(form);
			String recordId = "r_" + getRecordId((RECORD_INDEX ++));
			service.startRecord(servers, recordId);
			Map<String, String> map = new HashMap<String, String>();
			map.put("recordId", recordId);
			return map;
		}
		else if(action.equals("endRecord")){
			List<TargetServerInfo> servers = getServersFromForm(form);
			String recordId = form.getFirst("recordId");
			service.endRecord(servers, recordId);
		}
		return null;
	}

	private List<TargetServerInfo> getServersFromForm(MultivaluedMap<String, String> form) {
		String ipstr = form.getFirst("ips");
		String[] ips = ipstr.split(",");
		//only one server one times
		List<TargetServerInfo> servers = getServers(ips);
		if(servers == null || servers.size() == 0)
			return null;
		return servers;
	}
	
	/**
	 * ensure the id's length is 4
	 * @param index
	 * @return
	 */
	private String getRecordId(Integer index) {
		String id = "" + index;
		int length = id.length();
		for(int i = 0; i < (4 - length); i ++)
			id = "0" + id;
		return id;
	}
}
