package org.springframework.boot.web.embedded.tomcat;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;

/**
 * @author zhanyang
 */
public class TomcatServletWebServerFactory extends AbstractServletWebServerFactory
        implements ConfigurableServletWebServerFactory, ResourceLoaderAware {

    private ResourceLoader resourceLoader;


    /**
     * Factory method called to create the {@link TomcatWebServer}. Subclasses can
     * override this method to return a different {@link TomcatWebServer} or apply
     * additional processing to the Tomcat server.
     * @param tomcat the Tomcat server.
     * @return a new {@link TomcatWebServer} instance
     */
    protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
        return new TomcatWebServer(tomcat, getPort() >= 0, getShutdown());
    }
    private final TomcatWebServer zhanYang = new TomcatWebServer();

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        zhanYang.setServletContext(initializers);
        return zhanYang;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
