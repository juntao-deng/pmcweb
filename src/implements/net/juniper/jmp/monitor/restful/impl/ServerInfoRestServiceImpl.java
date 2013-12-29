package net.juniper.jmp.monitor.restful.impl;

import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.locator.ServiceLocator;
import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ServerInfoRestService;
import net.juniper.jmp.monitor.services.IServerInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;

import org.springframework.data.domain.Page;
/**
 * 
 * @author juntaod
 *
 */
public class ServerInfoRestServiceImpl implements ServerInfoRestService {
	private IServerInfoService service = ServiceLocator.getService(IServerInfoService.class);
	@Override
	public PageResult<TargetServerInfo> getServerInfos() {
		Page<TargetServerInfo> page = service.getServers(ApiContext.getPagingContext());
		return new PageResult<TargetServerInfo>(page);
	}

	@Override
	public TargetServerInfo getServerInfo(String id) {
		Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
		Iterator<TargetServerInfo> sit = serverMap.values().iterator();
		while(sit.hasNext()){
			TargetServerInfo server = sit.next();
			if(server.getId().toString().equals(id))
				return server;
		}
		return null;
	}

	@Override
	public Object processAction(String action, MultivaluedMap<String, String> form){
		if(action.equals("getServerList")){
			String aliveNode = form.getFirst("aliveNode");
			if(aliveNode != null && aliveNode.equals("true")){
				return service.getAliveNodeServers();
			}
		}
		return null;
	}

	@Override
	public TargetServerInfo updateServerInfo(Integer id, TargetServerInfo device) {
		TargetServerInfo result = service.saveServer(device);
		return result;
	}

	@Override
	public TargetServerInfo addServerInfo(TargetServerInfo device) {
		TargetServerInfo result = service.saveServer(device);
		return result;
	}

	@Override
	public void deleteServerInfo(Integer id) {
		
	}

}
