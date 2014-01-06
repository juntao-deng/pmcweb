package net.juniper.jmp.monitor.services;

import java.util.List;
import java.util.Map;

import net.juniper.jmp.core.ctx.PagingContext;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;

import org.springframework.data.domain.Page;

/**
 * 
 * @author juntaod
 *
 */
public interface IServerInfoService {
	public Map<String, TargetServerInfo> getAllServers();
	public Page<TargetServerInfo> getServers(PagingContext pagingContext);
	public List<TargetServerInfo> getAliveNodeServers();
	public List<TargetServerInfo> getOccupiedAndFreeNodeServers(String sesId);
	public TargetServerInfo saveServer(TargetServerInfo server);
	public TargetServerInfo updateServer(TargetServerInfo server);
	public void deleteServer(Integer id);
}
