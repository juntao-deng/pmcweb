package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.core.ctx.Pageable;
import net.juniper.jmp.core.ctx.impl.PageImpl;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.SqlInfoRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.tracer.dumper.info.SqlInfoDump;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;

public class SqlInfoRestServiceImpl extends AbstractMonitorInfoRestService implements SqlInfoRestService {
	private static final String SQLHISINFOS = "sqlhisinfos";
	@Inject
	private IClientInfoService service;
	@Override
	public PageResult<SqlInfoDump> getSqlInfos(String startTs, String endTs) {
		String ipstr = ApiContext.getParameter("ips");
		String fetchType = ApiContext.getParameter("fetchType");
		if(fetchType == null)
			fetchType = "";
		
		if(endTs == null || endTs.equals("")){
			endTs = "01/01/2020 00:00";
		}
		String cacheKey = ipstr + startTs + endTs + fetchType;
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(SQLHISINFOS);
		if(cacheObj == null || !cacheObj.key.equals(cacheKey)){
			
			String[] ips = ipstr.split(",");
			List<TargetServerInfo> servers = getServers(ips);
	//		String startTs = ApiContext.getParameter("startts");
	//		String endTs = ApiContext.getParameter("endts");
			Map<TargetServerInfo, Object> reqResults = service.getSqlInfos(servers, startTs, endTs, fetchType);
			List<SqlInfoDump> or = new ArrayList<SqlInfoDump>();
			Iterator<Entry<TargetServerInfo, Object>> it = reqResults.entrySet().iterator();
			while(it.hasNext()){
				Entry<TargetServerInfo, Object> entry = it.next();
				SqlInfoDump[] result = (SqlInfoDump[]) entry.getValue();
				if(result != null){
					bindServer(entry.getKey(), result);
					or.addAll(Arrays.asList(result));
	//				dr.addAll(detachResult(or));
				}
			}
			
			cacheObj = new CacheObject();
			cacheObj.key = cacheKey;
			cacheObj.list = or;
			ApiContext.getGlobalSessionCache().addCache(SQLHISINFOS, cacheObj);
		}
		
		List<SqlInfoDump> or = cacheObj.list;
		Pageable p = ApiContext.getPagingContext().getPageable();
		int pageIndex = p.getPageNumber();
		int pageSize = p.getPageSize();
		int jump = pageIndex * pageSize;
		List<SqlInfoDump> pageResults = new ArrayList<SqlInfoDump>();
		int totalSize = or.size();
		for(int i = jump; i < (jump + pageSize) && i < totalSize; i ++){
			pageResults.add(or.get(i).detach());
		}
		Page<SqlInfoDump> page = new PageImpl<SqlInfoDump>(pageResults, p, totalSize);
		return new PageResult<SqlInfoDump>(page);
	}
	
	class CacheObject{
		protected String key;
		protected List<SqlInfoDump> list;
	}

	@Override
	public StageInfoBaseDump getStageInfo(String id, String sid) {
		CacheObject cacheObj = (CacheObject) ApiContext.getGlobalSessionCache().getCache(SQLHISINFOS);
		if(cacheObj == null)
			return null;
		List<SqlInfoDump> dr = (List<SqlInfoDump>) cacheObj.list;
		if(dr == null)
			return null;
		SqlInfoDump sqlDump = null;
		Iterator<SqlInfoDump> sqlIt = dr.iterator();
		while(sqlIt.hasNext()){
			SqlInfoDump sql = sqlIt.next();
			if(sql.getId().toString().equals(id)){
				sqlDump = sql;
				break;
			}
		}
		if(sqlDump != null){
			TargetServerInfo server = (TargetServerInfo) sqlDump.getUserObject();
			return service.getStageById(server, sid);
		}
		return null;
	}
}
