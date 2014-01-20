package net.juniper.jmp.monitor.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.core.ctx.PagingContext;
import net.juniper.jmp.monitor.jpa.ServerEntity;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.services.IServerInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;
import net.juniper.jmp.persist.IJmpPersistenceManager;
import net.juniper.jmp.persist.JmpPersistenceContext;
import net.juniper.jmp.utils.IMoEntityConvertor;
import net.juniper.jmp.utils.MoEntityConvertor;

import org.springframework.stereotype.Service;
/**
 * 
 * @author juntaod
 *
 */
@Service(value="net.juniper.jmp.monitor.services.IServerInfoService")
public class ServerInfoServiceImpl implements IServerInfoService {
	private IMoEntityConvertor<TargetServerInfo, ServerEntity> convertor;
	@Override
	public Map<String, TargetServerInfo> getAllServers() {
		Map<String, TargetServerInfo> allServers = MonitorInfo.getInstance().getAllServers();
		if(allServers == null){
			allServers = new ConcurrentHashMap<String, TargetServerInfo>();
			IJmpPersistenceManager em = JmpPersistenceContext.getInstance();
			try{
				List<ServerEntity> results = em.findAll(ServerEntity.class, null);
				List<TargetServerInfo> serverList = getConvertor().convertFromEntity2Mo(results, TargetServerInfo.class);
				Iterator<TargetServerInfo> it = serverList.iterator();
				while(it.hasNext()){
					TargetServerInfo server = it.next();
					allServers.put(server.getAddress(), server);
				}
				MonitorInfo.getInstance().setAllServers(allServers);
			}
			finally{
				em.release();
			}
		}
		return allServers;
	}

	@Override
	public Page<TargetServerInfo> getServers(PagingContext pagingContext) {
		IJmpPersistenceManager em = JmpPersistenceContext.getInstance();
		try{
			Page<ServerEntity> results = em.findAll(ServerEntity.class, null, pagingContext.getPageable());
			Page<TargetServerInfo> servers = getConvertor().convertFromEntity2Mo(results, TargetServerInfo.class);
			fillStateFromCache(servers);
			return servers;
		}
		finally{
			em.release();
		}
	}

	@Override
	public TargetServerInfo saveServer(TargetServerInfo server) {
		if(server == null)
			return null;
		
		IJmpPersistenceManager em = JmpPersistenceContext.getInstance();
		try{
			ServerEntity entity = getConvertor().convertFromMo2Entity(server, ServerEntity.class);
			ServerEntity result = em.insert(entity);
			TargetServerInfo serverInfo = getConvertor().convertFromEntity2Mo(result, TargetServerInfo.class);
			getAllServers().put(result.getAddress(), serverInfo);
			return serverInfo;
		}
		finally{
			em.release();
		}
	}
	
	@Override
	public TargetServerInfo updateServer(TargetServerInfo server) {
		if(server == null)
			return null;
		
		IJmpPersistenceManager em = JmpPersistenceContext.getInstance();
		try{
			ServerEntity entity = getConvertor().convertFromMo2Entity(server, ServerEntity.class);
			ServerEntity result = em.update(entity);
			TargetServerInfo serverInfo = getConvertor().convertFromEntity2Mo(result, TargetServerInfo.class);
			getAllServers().put(result.getAddress(), serverInfo);
			return serverInfo;
		}
		finally{
			em.release();
		}
	}
	
	private void fillStateFromCache(Page<TargetServerInfo> servers) {
		if(servers == null)
			return;
		Iterator<TargetServerInfo> it = servers.getRecords().iterator();
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
		IJmpPersistenceManager em = JmpPersistenceContext.getInstance();
		try{
			em.deleteByPK(ServerEntity.class, id);
		}
		finally{
			em.release();
		}
	}
	
	@Override
	public void deleteServers(Integer[] ids) {
		IJmpPersistenceManager em = JmpPersistenceContext.getInstance();
		try{
			for(int i = 0; i < ids.length; i ++){
				MonitorInfo.getInstance().removeServerById(ids[i]);
				em.deleteByPK(ServerEntity.class, ids[i]);
			}
		}
		finally{
			em.release();
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
