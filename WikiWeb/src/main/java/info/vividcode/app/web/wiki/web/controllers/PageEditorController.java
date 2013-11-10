package info.vividcode.app.web.wiki.web.controllers;

import info.vividcode.app.web.wiki.model.PageManager;
import info.vividcode.app.web.wiki.model.PageManager.PageResource;
import info.vividcode.app.web.wiki.web.MyApplication;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/-/page-editor")
public class PageEditorController {

    @GET
    public Response getHtml() {
        return Response.ok(new Viewable("/page_editor.jsp")).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response editWikiPage(
            @FormParam("path") String path,
            @FormParam("title") String title,
            @FormParam("content") String content) {
        PageManager pm = MyApplication.getPageManager();
        pm.createPage(path, new PageResource(title, content));
        return Response.ok("edited!!" + path + ", " + title + ", " + content).build();
    }

}
