package net.juniper.jmp.monitor.services.impl;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.core.ctx.PagingContext;
import net.juniper.jmp.monitor.jpa.ServerEntity;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.services.IServerInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;
import net.juniper.jmp.persist.IJmpPersistence;
import net.juniper.jmp.utils.IMoEntityConvertor;
import net.juniper.jmp.utils.MoEntityConvertor;

import org.hibernate.dialect.function.TrimFunctionTemplate.Specification;
/**
 * 
 * @author juntaod
 *
 */
public class ServerInfoServiceImpl implements IServerInfoService {
	@Inject
	private IJmpPersistence em;
	private IMoEntityConvertor<TargetServerInfo, ServerEntity> convertor;
	@Override
	public Map<String, TargetServerInfo> getAllServers() {
		Map<String, TargetServerInfo> allServers = MonitorInfo.getInstance().getAllServers();
		if(allServers == null){
			allServers = new ConcurrentHashMap<String, TargetServerInfo>();
			Pageable pageable = null;
			Page<ServerEntity> results = em.findAll(ServerEntity.class, null, pageable);
			Page<TargetServerInfo> serverPage = getConvertor().convertFromEntity2Mo(results, TargetServerInfo.class);
			List<TargetServerInfo> serverList = serverPage.getContent();
			Iterator<TargetServerInfo> it = serverList.iterator();
			while(it.hasNext()){
				TargetServerInfo server = it.next();
				allServers.put(server.getAddress(), server);
			}
			MonitorInfo.getInstance().setAllServers(allServers);
		}
		return allServers;
	}

	@Override
	public Page<TargetServerInfo> getServers(PagingContext pagingContext) {
		Pageable pageable = null;
		if(pagingContext != null){
			spec = pagingContext.getSpec(ServerEntity.class);
			pageable = pagingContext.getPageable();
		}
		Page<ServerEntity> results = em.findAll(ServerEntity.class, pagingContext.pageable);
		Page<TargetServerInfo> servers = getConvertor().convertFromEntity2Mo(results, TargetServerInfo.class);
		fillStateFromCache(servers);
		return servers;
	}

	@Override
	public TargetServerInfo saveServer(TargetServerInfo server) {
		if(server == null)
			return null;
		ServerEntity entity = getConvertor().convertFromMo2Entity(server, ServerEntity.class);
		ServerEntity result = serverRep.save(entity);
		TargetServerInfo serverInfo = getConvertor().convertFromEntity2Mo(result, TargetServerInfo.class);
		getAllServers().put(result.getAddress(), serverInfo);
		return serverInfo;
	}
	
	@Override
	public TargetServerInfo updateServer(TargetServerInfo server) {
		if(server == null)
			return null;
		ServerEntity entity = getConvertor().convertFromMo2Entity(server, ServerEntity.class);
		ServerEntity result = serverRep.save(entity);
		TargetServerInfo serverInfo = getConvertor().convertFromEntity2Mo(result, TargetServerInfo.class);
		getAllServers().put(result.getAddress(), serverInfo);
		return serverInfo;
	}
	
	private void fillStateFromCache(Page<TargetServerInfo> servers) {
		if(servers == null)
			return;
		Iterator<TargetServerInfo> it = servers.iterator();
		while(it.hasNext()){
			TargetServerInfo server = it.next();
			TargetServerInfo cacheServer = getAllServers().get(server.getAddress());
			server.setAlive(cacheServer.isAlive());
			server.setNodeAlive(cacheServer.isNodeAlive());
			server.setSessionId(cacheServer.getSessionId());
			server.setOccupiedBy(cacheServer.getOccupiedBy());
		}
	}
	@Override
	public void deleteServer(Integer id) {
		serverRep.delete(id);
	}
	
	@Override
	public void deleteServers(Integer[] ids) {
		for(int i = 0; i < ids.length; i ++){
			MonitorInfo.getInstance().removeServerById(ids[i]);
			serverRep.delete(ids[i]);
		}
	}

	@Override
	public List<TargetServerInfo> getAliveNodeServers() {
		Map<String, TargetServerInfo> allServers = getAllServers();
		List<TargetServerInfo> aliveNodeServers = new ArrayList<TargetServerInfo>();
		Iterator<TargetServerInfo> it = allServers.values().iterator();
		while(it.hasNext()){
			TargetServerInfo server = it.next();
			if(server.isNodeAlive())
				aliveNodeServers.add(server);
		}
		return aliveNodeServers;
	}

	private IMoEntityConvertor<TargetServerInfo, ServerEntity> getConvertor(){
		if(convertor == null){
			convertor = new MoEntityConvertor<TargetServerInfo, ServerEntity>(){
				@Override
				public List<ServerEntity> convertFromMo2Entity(List<TargetServerInfo> moList, Class<ServerEntity> clazz) {
					List<ServerEntity> items = super.convertFromMo2Entity(moList, clazz);
					//further process
					return items;
				}
				@Override
				public List<TargetServerInfo> convertFromEntity2Mo(List<ServerEntity> entityList, Class<TargetServerInfo> clazz) {
					List<TargetServerInfo> items = super.convertFromEntity2Mo(entityList, clazz);
					//further process
					return items;
				}
			};
		}
		return convertor;
	}

	@Override
	public List<TargetServerInfo> getOccupiedAndFreeNodeServers(String sesId) {
		Map<String, TargetServerInfo> allServers = getAllServers();
		List<TargetServerInfo> aliveNodeServers = new ArrayList<TargetServerInfo>();
		Iterator<TargetServerInfo> it = allServers.values().iterator();
		while(it.hasNext()){
			TargetServerInfo server = it.next();
			//if(server.isNodeAlive()){
			if(server.isAlive()){
				if(server.getSessionId() == null || server.getSessionId().equals(sesId))
					aliveNodeServers.add(server);
			}
		}
		return aliveNodeServers;
	}
}
