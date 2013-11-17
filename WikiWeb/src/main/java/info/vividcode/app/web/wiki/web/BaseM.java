package info.vividcode.app.web.wiki.web;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

public class BaseM {
    protected final UriInfo uriInfo;
    public BaseM(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
    public final String absPath(String relPathFromAbsBasePath) {
        return URI.create(uriInfo.getBaseUri().getRawPath())
                .resolve(relPathFromAbsBasePath).toASCIIString();
    }
}
