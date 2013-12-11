package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.info.CpuInfo;
import net.juniper.jmp.monitor.mo.info.CpuSummary;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.CpuInfoRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;

public class CpuInfoRestServiceImpl implements CpuInfoRestService {
	private IClientInfoService service = ServiceLocator.getService(IClientInfoService.class);
	private static Map<String, CpuSummary> cacheMap = new ConcurrentHashMap<String, CpuSummary>();

	private static void initCpuCache(String ip) {
		CpuSummary summary = new CpuSummary();
		summary.setSeries(ip);
		List<Integer> maxValues = summary.getValues();
		for(int i = 0; i < 30; i ++){
			if(i % 3 == 0)
				maxValues.add(70);
			else
				maxValues.add(50);
		}
		cacheMap.put(ip, summary);
	}
	
	@Override
	public PageResult<CpuSummary> getCpuSummaries() {
		String ipStr = ApiContext.getParameter("ips");
		String[] ips = ipStr.split(",");
		List<TargetServerInfo> servers = getServers(ips);
		if(servers == null || servers.size() == 0)
			return null;
		
		for(String ip : ips){
			if(cacheMap.get(ip) == null){
				initCpuCache(ip);
			}
		}
		List<CpuSummary> results = new ArrayList<CpuSummary>();
		Map<TargetServerInfo, Object> reqResults = service.getCpuInfo(servers);
		Iterator<TargetServerInfo> serverIt = servers.iterator();
		while(serverIt.hasNext()){
			TargetServerInfo server = serverIt.next();
			CpuInfo cpuInfo = (CpuInfo) reqResults.get(server);
			if(cpuInfo == null){
				cpuInfo = new CpuInfo();
			}
			CpuSummary summary = cacheMap.get(server.getAddress());
			summary.queue((int) (cpuInfo.getUsage()));
			results.add(summary);
		}
		return new PageResult<CpuSummary>(results);
	}

	@Override
	public CpuSummary getCpuSummary(String id) {
		return null;
	}

	@Override
	public Object processAction(String action,
			MultivaluedMap<String, String> form) {
		return null;
	}

	private List<TargetServerInfo> getServers(String[] ips) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		for(String ip : ips){
			TargetServerInfo server = serverMap.get(ip);
			if(server != null)
				serverList.add(server);
		}
		return serverList;
	}
}
