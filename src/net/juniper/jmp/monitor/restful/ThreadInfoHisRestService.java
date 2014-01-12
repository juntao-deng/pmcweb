package net.juniper.jmp.monitor.restful;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.juniper.jmp.core.repository.PageResult;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;
import net.juniper.jmp.tracer.dumper.info.ThreadInfoDump;

@Path("/threadinfoshis")
public interface ThreadInfoHisRestService {
	
	@GET 
	@Path("/") 
	@Produces(MediaType.APPLICATION_JSON)
	public PageResult<ThreadInfoDump> getThreadInfos(@QueryParam("startTs") String startTs, @QueryParam("endTs") String endTs);
	
	@GET 
	@Path("/{id}/stageinfos") 
	@Produces(MediaType.APPLICATION_JSON)
	public StageInfoBaseDump[] getStageInfos(@PathParam("id") String id);
	
	@GET 
	@Path("/{id}/stageinfos/{sid}") 
	@Produces(MediaType.APPLICATION_JSON)
	public StageInfoBaseDump getStageInfo(@PathParam("id") String id, @PathParam("sid") String sid);
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ThreadInfoDump getThreadInfo(@PathParam("id") String id);
	
	@POST
	@Path("/action/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public Object processAction(@PathParam("action") String action, MultivaluedMap<String, String> form);
}
