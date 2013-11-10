package info.vividcode.app.web.wiki.web.controllers;

import info.vividcode.app.web.wiki.model.PageManager;
import info.vividcode.app.web.wiki.model.PageManager.PageResource;
import info.vividcode.app.web.wiki.web.MyApplication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/{path: (?!-/).*}")
public class WikiPageController {

    @GET
    public Response getPage(@PathParam("path") String path) {
        PageManager pm = MyApplication.getPageManager();
        PageResource pr = pm.getPage(path);
        if (pr == null) {
            return Response.status(Status.NOT_FOUND).entity("/" + path + " is not found").build();
        }
        return Response.ok(new Viewable("/wiki_page.jsp", pr)).build();
    }

}
