package com.example.demowebserver;

import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;

public class ZhanYangServletWebServerFactory extends AbstractServletWebServerFactory
        implements ConfigurableServletWebServerFactory, ResourceLoaderAware {

    private ResourceLoader resourceLoader;
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
