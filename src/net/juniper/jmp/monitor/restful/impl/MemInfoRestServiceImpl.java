package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.core.locator.SpringWebContextHelper;
import net.juniper.jmp.monitor.mo.info.MemSummary;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.MemInfoRestService;
import net.juniper.jmp.monitor.services.IClientInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;
import net.juniper.jmp.tracer.info.MemInfo;

import org.springframework.stereotype.Service;

@Service(value="net.juniper.jmp.monitor.restful.MemInfoRestService")
public class MemInfoRestServiceImpl implements MemInfoRestService {
	private static List<MemSummary> cacheList = new ArrayList<MemSummary>();
	
	static {
		initMemSummary();
	}
	
	@Override
	public Page<MemSummary> getMemSummaries() {
		String ipstr = ApiContext.getParameter("ips");
		String[] ips = ipstr.split(",");
		//only one server one times
		List<String> oneIps = new ArrayList<String>();
		oneIps.add(ips[0]);
		List<TargetServerInfo> servers = getServers(oneIps);
		if(servers == null || servers.size() == 0)
			return null;
		Map<TargetServerInfo, Object> reqResults = SpringWebContextHelper.getService(IClientInfoService.class).getMemInfo(servers);
		MemInfo memInfo = (MemInfo) reqResults.get(servers.get(0));
		MemSummary maxList = cacheList.get(0);
		MemSummary totalList = cacheList.get(1);
		MemSummary usedList = cacheList.get(2);
		if(memInfo == null){
			memInfo = new MemInfo();
			memInfo.setMax(maxList.getValues().get(maxList.getValues().size() - 1));
			memInfo.setTotal(totalList.getValues().get(totalList.getValues().size() - 1));
			memInfo.setFree(memInfo.getTotal() - usedList.getValues().get(usedList.getValues().size() - 1));
		}
		//max
		cacheList.get(0).queue(memInfo.getMax());
		//total
		cacheList.get(1).queue(memInfo.getTotal());
		//used
		cacheList.get(2).queue(memInfo.getTotal() - memInfo.getFree());
		return new Page<MemSummary>(cacheList);
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
