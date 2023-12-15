package com.example.demowebserver;

import jakarta.servlet.Servlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author zhanyang
 */
@Configuration
public class ZhanYangServletWebServerFactoryConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ Servlet.class, TomcatWebServer.class })
    @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
    static class EmbeddedZhanYang {

        @Bean
        ZhanYangServletWebServerFactory zhanYangTomcatServletWebServerFactory() {
            return new ZhanYangServletWebServerFactory();
        }
    }
}


