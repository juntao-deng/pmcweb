package net.juniper.jmp.monitor.restful.impl;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.ApiContext;
import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.restful.ServerInfoRestService;
import net.juniper.jmp.monitor.services.IServerInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;

/**
 * 
 * @author juntaod
 *
 */
public class ServerInfoRestServiceImpl implements ServerInfoRestService {
	@Inject
	private IServerInfoService service;
	@Override
	public Page<TargetServerInfo> getServerInfos() {
		Page<TargetServerInfo> page = service.getServers(ApiContext.getPagingContext());
		return page;
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
				return service.getOccupiedAndFreeNodeServers(ApiContext.getSessionId());
			}
		}
		else if(action.equals("occupy")){
			String serverStr = form.getFirst("serveriplist");
			String[] servers = serverStr.split(",");
			String sesId = ApiContext.getSessionId();
			String clientIp = ApiContext.getClientInfo().getClientIp();
			Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
			for(String ip : servers) {
				TargetServerInfo server = serverMap.get(ip);
				if(server != null){
					server.setOccupiedBy(clientIp);
					server.setSessionId(sesId);
				}
			}
		}
		else if(action.equals("release")){
			String serverStr = form.getFirst("serveriplist");
			String[] servers = serverStr.split(",");
			Map<String, TargetServerInfo> serverMap = MonitorInfo.getInstance().getAllServers();
			for(String ip : servers) {
				TargetServerInfo server = serverMap.get(ip);
				if(server != null){
					server.setOccupiedBy(TargetServerInfo.FREE);
					server.setSessionId(null);
				}
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
	public void deleteServerInfo(String id) {
		String[] ids = id.split(",");
		Integer[] intIds = new Integer[ids.length];
		for(int i = 0; i < ids.length; i ++){
			intIds[i] = Integer.parseInt(ids[i]);
		}
		service.deleteServers(intIds);
	}

}
