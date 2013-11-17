package info.vividcode.app.web.wiki.web.controllers;

import info.vividcode.app.web.wiki.model.PageManager;
import info.vividcode.app.web.wiki.model.PageManager.PageHtmlResource;
import info.vividcode.app.web.wiki.web.BaseM;
import info.vividcode.app.web.wiki.web.MyApplication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/{path: (?!-/).*}")
public class WikiPageController {

    public class M extends BaseM {
        public final PageHtmlResource pr;
        public M(UriInfo uriInfo, PageHtmlResource pr) {
            super(uriInfo);
            this.pr = pr;
        }
    }

    @GET
    public Response getPage(@Context UriInfo uriInfo, @PathParam("path") String path) {
        PageManager pm = MyApplication.getPageManager();
        PageHtmlResource pr = pm.getPage(path);
        if (pr == null) {
            return Response.status(Status.NOT_FOUND).entity("/" + path + " is not found").build();
        }
        return Response.ok(new Viewable("/wiki_page.jsp", new M(uriInfo, pr))).build();
    }

}
