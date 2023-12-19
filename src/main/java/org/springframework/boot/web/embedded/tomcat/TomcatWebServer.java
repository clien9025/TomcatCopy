package org.springframework.boot.web.embedded.tomcat;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import webserver.HttpServer;

import java.io.PrintWriter;
import java.net.Socket;

public class TomcatWebServer implements WebServer {

    private ServletContext servletContext;
    private HttpServer httpServer;

    private int port = 8080;

    private Thread serverThread;

    private StandardContext context;// todo 这个地方是 null， 没有赋值和初始化的

    public TomcatWebServer() {
        this.httpServer = new HttpServer();
        this.servletContext = createServletContext();
    }

    public void setServletContext(ServletContextInitializer... initializers) {
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
        return new ApplicationContext(context);
    }

    @Override
    public void start() throws WebServerException {
        // 启动自定义 HttpServer

        // todo 重新使用线程来启动 webserver ，避免阻塞 springboot 主线程
        serverThread = new Thread(() -> {
            try {
                httpServer.await();
            } catch (Exception e) {
                throw new WebServerException("Error starting HTTP server", e);
            }
        });
        serverThread.start();
    }

    @Override
    public void stop() throws WebServerException {
        // 停止自定义 HttpServer
        // todo 实现一个关闭线程的操作，仅测试用下
        try {
            // 创建一个 Socket 连接到 HttpServer 并发送 SHUTDOWN 命令
            try (Socket socket = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("GET /SHUTDOWN HTTP/1.1");
                out.println("Host: localhost:" + port);
                out.println("Connection: Close");
                out.println();
            }
            serverThread.interrupt();
            try {
                serverThread.join(); // 等待线程结束
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new WebServerException("Error stopping HTTP server", e);
            }
        } catch (Exception e) {
            throw new WebServerException("Error sending shutdown command to HTTP server", e);
        }
    }

    @Override
    public int getPort() {
        // 返回 HttpServer 的端口
        return port;
    }

    // 其他必要的方法 ...
}
