package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.info.MemInfo;
import net.juniper.jmp.monitor.mo.info.MemSummary;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.MemInfoRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;

public class MemInfoRestServiceImpl implements MemInfoRestService {
	private IClientInfoService service = ServiceLocator.getService(IClientInfoService.class);
	private static List<MemSummary> cacheList = new ArrayList<MemSummary>();
	static {
		initMemSummary();
	}
	@Override
	public PageResult<MemSummary> getMemSummaries() {
		String ipstr = ApiContext.getParameter("ips");
		String[] ips = ipstr.split(",");
		//only one server one times
		List<String> oneIps = new ArrayList<String>();
		oneIps.add(ips[0]);
		List<TargetServerInfo> servers = getServers(oneIps);
		if(servers == null || servers.size() == 0)
			return null;
		Map<TargetServerInfo, Object> reqResults = service.getMemInfo(servers);
		MemInfo memInfo = (MemInfo) reqResults.get(servers.get(0));
		if(memInfo == null){
			memInfo = new MemInfo();
		}
		//max
		cacheList.get(0).queue(memInfo.getMax());
		//total
		cacheList.get(1).queue(memInfo.getTotal());
		//used
		cacheList.get(2).queue(memInfo.getTotal() - memInfo.getFree());
		return new PageResult<MemSummary>(cacheList);
	}

	private static void initMemSummary() {
		MemSummary maxSummary = new MemSummary();
		maxSummary.setSeries("max");
		List<Integer> maxValues = maxSummary.getValues();
		for(int i = 0; i < 30; i ++){
			maxValues.add(2000);
		}
		
		MemSummary totalSummary = new MemSummary();
		totalSummary.setSeries("total");
		List<Integer> totalValues = totalSummary.getValues();
		for(int i = 0; i < 30; i ++){
			totalValues.add(1200);
		}
		
		MemSummary usedSummary = new MemSummary();
		usedSummary.setSeries("used");
		List<Integer> usedValues = usedSummary.getValues();
		for(int i = 0; i < 30; i ++){
			usedValues.add(150);
		}
		
		cacheList.add(maxSummary);
		cacheList.add(totalSummary);
		cacheList.add(usedSummary);
	}

	@Override
	public MemSummary getMemSummary(String id) {
		return null;
	}

	@Override
	public Object processAction(String action,
			MultivaluedMap<String, String> form) {
		return null;
	}

	private List<TargetServerInfo> getServers(List<String> ips) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		Iterator<String> ipIt = ips.iterator();
		while(ipIt.hasNext()){
			TargetServerInfo server = serverMap.get(ipIt.next());
			if(server != null)
				serverList.add(server);
		}
		return serverList;
	}
}
