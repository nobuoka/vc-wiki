package info.vividcode.app.web.wiki.web.controllers;

import java.util.List;

import info.vividcode.app.web.wiki.model.PageManager;
import info.vividcode.app.web.wiki.model.PageManager.PagePathResourcePair;
import info.vividcode.app.web.wiki.web.MyApplication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/-/search")
public class SearchController {

    @GET
    public Response getSearchResultHtml(@QueryParam("text") String text) {
        PageManager pm = MyApplication.getPageManager();
        List<PagePathResourcePair> pprps = pm.searchPages(text, 0, 10);
        return Response.ok(new Viewable("/search_result.jsp", pprps)).build();
    }

}
