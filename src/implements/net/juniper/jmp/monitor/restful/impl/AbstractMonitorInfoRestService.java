package net.juniper.jmp.monitor.restful.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.sys.MonitorInfo;
import net.juniper.jmp.tracer.dumper.info.AbstractDumpObject;
/**
 * 
 * @author juntaod
 *
 */
public abstract class AbstractMonitorInfoRestService {
	protected List<TargetServerInfo> getServers(String[] ips) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		List<TargetServerInfo> serverList = new ArrayList<TargetServerInfo>();
		for(String ip : ips) {
			TargetServerInfo server = serverMap.get(ip);
			if(server != null)
				serverList.add(server);
		}
		return serverList;
	}
	
	protected void bindServer(TargetServerInfo server, AbstractDumpObject[] result) {
		if(result != null && result.length > 0){
			for(AbstractDumpObject s : result){
				s.setUserObject(server);
			}
		}
	}
}
