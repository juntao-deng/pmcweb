package net.juniper.jmp.monitor.restful;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.monitor.mo.info.CpuSummary;

@Path("/cpuinfos")
public interface CpuInfoRestService {
	
	@GET 
	@Path("/") 
	@Produces(MediaType.APPLICATION_JSON)
	public PageResult<CpuSummary> getCpuSummaries();
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public CpuSummary getCpuSummary(@PathParam("id") String id);
	
	@POST
	@Path("/action/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object processAction(@PathParam("action") String action, MultivaluedMap<String, String> form);
}
