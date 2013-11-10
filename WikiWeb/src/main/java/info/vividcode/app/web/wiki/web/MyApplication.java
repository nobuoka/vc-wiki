package info.vividcode.app.web.wiki.web;

import info.vividcode.app.web.wiki.model.PageManager;

import java.sql.SQLException;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspProperties;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
//import org.glassfish.jersey.servlet.ServletProperties;

@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    private static final String CONTROLLERS_PACKAGE_PREFIX = ".controllers";
    private static PageManager sPageManager;

    private static class Reloader implements ContainerLifecycleListener {
        @Override
        public void onStartup(Container container) {
            System.err.println("=== startup");
            sPageManager = PageManager.create();
        }

        @Override
        public void onReload(Container container) {
            // ignore or do whatever you want after reload has been done
            System.err.println("=== reload");
        }

        @Override
        public void onShutdown(Container container) {
            // ignore or do something after the container has been shutdown
            System.err.println("=== shutdown");
            sPageManager.close();
            sPageManager = null;
        }
    }

    public MyApplication() throws SQLException {
        // Add a package used to scan for components.
        packages(this.getClass().getPackage().getName() + CONTROLLERS_PACKAGE_PREFIX);

        // See: https://jersey.java.net/documentation/2.4/mvc.html
        register(JspMvcFeature.class);
        property(JspProperties.TEMPLATES_BASE_PATH, "/WEB-INF/jsp");
        //property(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/(static|(WEB-INF/jsp))/.*");

        // See: https://jersey.java.net/documentation/2.4/media.html#json.jackson
        register(JacksonFeature.class);

        //this.register(new Reloader());
        registerClasses(Reloader.class);
    }

    public static PageManager getPageManager() {
        return sPageManager;
    }

}
