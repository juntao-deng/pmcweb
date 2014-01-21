package net.juniper.jmp.tags.restful;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.juniper.jmp.tags.model.TagMO;

@Path("/tags") 
public interface TagsRestService {
   @GET 
   @Path("/") 
   @Produces(MediaType.APPLICATION_JSON)
   public TagMO[] getTags();
} 