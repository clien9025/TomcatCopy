package com.example.demowebserver;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import webserver.HttpServer;

public class ZhanYang implements WebServer {

    private ServletContext servletContext;
    private HttpServer httpServer;

    private int port = 8080;

    public ZhanYang() {
        this.httpServer = new HttpServer();
        this.servletContext = createServletContext();
    }

    public void setServletContext(ServletContextInitializer... initializers){
        for (ServletContextInitializer initializer : initializers) {
            try {
                initializer.onStartup(this.servletContext);
            } catch (ServletException e) {
                e.printStackTrace();
                throw new RuntimeException("放入 servletContext 失败");
            }
        }
    }

    private ServletContext createServletContext() {
        // 创建并返回一个 ServletContext 实例
        return new MockServletContext();
    }

    @Override
    public void start() throws WebServerException {
        // 启动自定义 HttpServer
        httpServer.await();
    }

    @Override
    public void stop() throws WebServerException {
        // 停止自定义 HttpServer
    }

    @Override
    public int getPort() {
        // 返回 HttpServer 的端口
        return port;
    }

    // 其他必要的方法 ...
}
