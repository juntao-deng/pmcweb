package net.juniper.jmp.monitor.restful;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.juniper.jmp.core.ctx.Page;
import net.juniper.jmp.tracer.dumper.info.SqlInfoDump;
import net.juniper.jmp.tracer.dumper.info.StageInfoBaseDump;

@Path("/sqlinfos")
public interface SqlInfoRestService {
	
	@GET 
	@Path("/") 
	@Produces(MediaType.APPLICATION_JSON)
	public Page<SqlInfoDump> getSqlInfos(@QueryParam("startTs") String startTs, @QueryParam("endTs") String endTs);
	
	@GET 
	@Path("/{id}/stageinfos/{sid}") 
	@Produces(MediaType.APPLICATION_JSON)
	public StageInfoBaseDump getStageInfo(@PathParam("id") String id, @PathParam("sid") String sid);
}
