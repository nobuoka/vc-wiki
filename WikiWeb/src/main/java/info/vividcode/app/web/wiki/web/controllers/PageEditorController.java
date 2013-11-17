package info.vividcode.app.web.wiki.web.controllers;

import java.net.URI;
import java.util.regex.Pattern;

import info.vividcode.app.web.wiki.model.PageManager;
import info.vividcode.app.web.wiki.model.PageManager.PageSourceResource;
import info.vividcode.app.web.wiki.web.BaseM;
import info.vividcode.app.web.wiki.web.MyApplication;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
            @Context UriInfo uriInfo,
            @FormParam("path") String path,
            @FormParam("title") String title,
            @FormParam("content") String content) {
        BaseM m = new BaseM(uriInfo);
        PageManager pm = MyApplication.getPageManager();
        content = Pattern.compile("(?:\r\n|\r)").matcher(content).replaceAll("\n");
        pm.createPage(path, new PageSourceResource(title, content));
        return Response.seeOther(URI.create(m.absPath(path))).build();
    }

}
