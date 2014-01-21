package net.juniper.jmp.monitor.services;

import java.util.List;
import java.util.Map;

import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;
/**
 * 
 * @author juntaod
 *
 */
public interface IClientInfoService {
	public Map<TargetServerInfo, Object> getThreadInfos(List<TargetServerInfo> servers);
	public Map<TargetServerInfo, Object> getCpuInfo(List<TargetServerInfo> servers);
	public Map<TargetServerInfo, Object> getMemInfo(List<TargetServerInfo> servers);
	public Map<TargetServerInfo, Object> getPeriodThreadInfos(List<TargetServerInfo> servers, String startTime, String endTime, String fetchType);
	public Map<TargetServerInfo, Object> getSqlInfos(List<TargetServerInfo> servers, String startTime, String endTime, String fetchType);
	public boolean isNodeLive(TargetServerInfo server);
	public boolean isServerLive(TargetServerInfo server);
	public Map<TargetServerInfo, Object> startRecord(List<TargetServerInfo> server, String recordId);
	public void endRecord(List<TargetServerInfo> server, String recordId);
	public Map<TargetServerInfo, Object> getRecordThreadInfos(List<TargetServerInfo> server, String recordId);
	public StageInfoBaseDump[] getStagesByParentId(TargetServerInfo server, String callId);
	public StageInfoBaseDump getStageById(TargetServerInfo server, String callId);
}
