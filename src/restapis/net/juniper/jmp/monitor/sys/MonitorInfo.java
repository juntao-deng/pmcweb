package net.juniper.jmp.monitor.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
/**
 * 
 * @author juntaod
 *
 */
public class MonitorInfo {
	private TargetServerInfo currentServerInfo;
	private static MonitorInfo instance = new MonitorInfo();
	private Map<String, TargetServerInfo> allServers;
	private MonitorInfo() {
	}
	public TargetServerInfo getCurrentServerInfo() {
		return currentServerInfo;
	}
	
	public void setCurrentServerInfo(TargetServerInfo serverInfo){
		currentServerInfo = serverInfo;
	}
	
	public static MonitorInfo getInstance() {
		return instance;
	}
	public Map<String, TargetServerInfo> getAllServers() {
		return allServers;
	}
	
	public List<TargetServerInfo> getAllServersList() {
		if(allServers == null)
			return null;
		List<TargetServerInfo> list = new ArrayList<TargetServerInfo>();
		list.addAll(allServers.values());
		return list;
	}
	public void setAllServers(Map<String, TargetServerInfo> allServers) {
		this.allServers = allServers;
	}
}
