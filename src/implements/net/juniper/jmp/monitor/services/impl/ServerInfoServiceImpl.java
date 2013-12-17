package net.juniper.jmp.monitor.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.juniper.jmp.core.ctx.PagingContext;
import net.juniper.jmp.monitor.dao.ServerRepository;
import net.juniper.jmp.monitor.jpa.ServerEntity;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;
import net.juniper.jmp.monitor.services.IServerInfoService;
import net.juniper.jmp.monitor.sys.MonitorInfo;
import net.juniper.jmp.utils.IMoEntityConvertor;
import net.juniper.jmp.utils.MoEntityConvertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
/**
 * 
 * @author juntaod
 *
 */
@Service(value="net.juniper.jmp.monitor.services.IServerInfoService")
public class ServerInfoServiceImpl implements IServerInfoService {
//	private Logger logger = LoggerFactory.getLogger(ServerInfoServiceImpl.class);
	private IMoEntityConvertor<TargetServerInfo, ServerEntity> convertor;
	@Autowired
	private ServerRepository serverRep;
	@Override
	public Map<String, TargetServerInfo> getAllServers() {
		Map<String, TargetServerInfo> allServers = MonitorInfo.getInstance().getAllServers();
		if(allServers == null){
			allServers = new ConcurrentHashMap<String, TargetServerInfo>();
			Specification<ServerEntity> spec = null;
			Pageable pageable = null;
			Page<ServerEntity> results = serverRep.findAll(spec, pageable);
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
		Specification<ServerEntity> spec = null;
		Pageable pageable = null;
		if(pagingContext != null){
			spec = pagingContext.getSpec(ServerEntity.class);
			pageable = pagingContext.getPageable();
		}
		Page<ServerEntity> results = serverRep.findAll(spec, pageable);
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
		}
	}
	@Override
	public void deleteServer(Integer id) {
		serverRep.delete(id);
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
}
