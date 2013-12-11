package net.juniper.jmp.monitor.services;

import java.util.List;
import java.util.Map;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
/**
 * 
 * @author juntaod
 *
 */
public interface IClientInfoService {
	public Map<TargetServerInfo, Object> getThreadInfos(List<TargetServerInfo> servers);
	public Map<TargetServerInfo, Object> getCpuInfo(List<TargetServerInfo> servers);
	public Map<TargetServerInfo, Object> getMemInfo(List<TargetServerInfo> servers);
	public Map<TargetServerInfo, Object> getThreadInfos(List<TargetServerInfo> servers, String startTime, String endTime);
	public boolean isNodeLive(TargetServerInfo server);
	public boolean isServerLive(TargetServerInfo server);
}
