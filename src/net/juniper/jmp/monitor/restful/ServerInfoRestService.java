package net.juniper.jmp.monitor.restful;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;

@Path("/serverinfos")
public interface ServerInfoRestService {
	
	@GET 
	@Path("/") 
	@Produces(MediaType.APPLICATION_JSON)
	public Page<TargetServerInfo> getServerInfos();
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TargetServerInfo getServerInfo(@PathParam("id") String id);
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TargetServerInfo updateServerInfo(@PathParam("id") Integer id, TargetServerInfo device);
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public TargetServerInfo addServerInfo(TargetServerInfo device);
 
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteServerInfo(@PathParam("id") String id);
	
	@POST
	@Path("/action/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object processAction(@PathParam("action") String action, MultivaluedMap<String, String> form);
}
