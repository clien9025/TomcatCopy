package org.springframework.boot.web.embedded.tomcat;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.springframework.boot.web.server.*;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.util.Assert;
import webserver.HttpServer;

import javax.naming.NamingException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TomcatWebServer implements WebServer {

    private static final Log logger = LogFactory.getLog(TomcatWebServer.class);

    private static final AtomicInteger containerCounter = new AtomicInteger(-1);

    private final Object monitor = new Object();

    private final Map<Service, Connector[]> serviceConnectors = new HashMap<>();

    private final Tomcat tomcat;

    private final boolean autoStart;

    private final GracefulShutdown gracefulShutdown;

    private volatile boolean started;

    /**
     * Create a new {@link TomcatWebServer} instance.
     * @param tomcat the underlying Tomcat server
     */
    public TomcatWebServer(Tomcat tomcat) {
        this(tomcat, true);
    }

    /**
     * Create a new {@link TomcatWebServer} instance.
     * @param tomcat the underlying Tomcat server
     * @param autoStart if the server should be started
     */
    public TomcatWebServer(Tomcat tomcat, boolean autoStart) {
        this(tomcat, autoStart, Shutdown.IMMEDIATE);
    }

    /**
     * Create a new {@link TomcatWebServer} instance.
     * @param tomcat the underlying Tomcat server
     * @param autoStart if the server should be started
     * @param shutdown type of shutdown supported by the server
     * @since 2.3.0
     */
    public TomcatWebServer(Tomcat tomcat, boolean autoStart, Shutdown shutdown) {
        Assert.notNull(tomcat, "Tomcat Server must not be null");
        this.tomcat = tomcat;
        this.autoStart = autoStart;
        this.gracefulShutdown = (shutdown == Shutdown.GRACEFUL) ? new GracefulShutdown(tomcat) : null;
        initialize();
    }

    private void initialize() throws WebServerException {
//        logger.info("Tomcat initialized with port(s): " + getPortsDescription(false));
//        synchronized (this.monitor) {
//            try {
//                addInstanceIdToEngineName();
//
//                Context context = findContext();
//                context.addLifecycleListener((event) -> {
//                    if (context.equals(event.getSource()) && Lifecycle.START_EVENT.equals(event.getType())) {
//                        // Remove service connectors so that protocol binding doesn't
//                        // happen when the service is started.
//                        removeServiceConnectors();
//                    }
//                });
//
//                // Start the server to trigger initialization listeners
//                this.tomcat.start();
//
//                // We can re-throw failure exception directly in the main thread
//                rethrowDeferredStartupExceptions();
//
//                try {
//                    ContextBindings.bindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
//                }
//                catch (NamingException ex) {
//                    // Naming is not enabled. Continue
//                }
//
//                // Unlike Jetty, all Tomcat threads are daemon threads. We create a
//                // blocking non-daemon to stop immediate shutdown
//                startDaemonAwaitThread();
//            }
//            catch (Exception ex) {
//                stopSilently();
//                destroySilently();
//                throw new WebServerException("Unable to start embedded Tomcat", ex);
//            }
//        }
        throw new UnsupportedOperationException();
    }

    private Context findContext() {
//        for (Container child : this.tomcat.getHost().findChildren()) {
//            if (child instanceof Context context) {
//                return context;
//            }
//        }
//        throw new IllegalStateException("The host does not contain a Context");
        throw new UnsupportedOperationException();
    }

    private void addInstanceIdToEngineName() {
//        int instanceId = containerCounter.incrementAndGet();
//        if (instanceId > 0) {
//            Engine engine = this.tomcat.getEngine();
//            engine.setName(engine.getName() + "-" + instanceId);
//        }
        throw new UnsupportedOperationException();
    }

    private void removeServiceConnectors() {
//        for (Service service : this.tomcat.getServer().findServices()) {
//            Connector[] connectors = service.findConnectors().clone();
//            this.serviceConnectors.put(service, connectors);
//            for (Connector connector : connectors) {
//                service.removeConnector(connector);
//            }
//        }
        throw new UnsupportedOperationException();
    }

    private void rethrowDeferredStartupExceptions() throws Exception {
//        Container[] children = this.tomcat.getHost().findChildren();
//        for (Container container : children) {
//            if (container instanceof TomcatEmbeddedContext embeddedContext) {
//                TomcatStarter tomcatStarter = embeddedContext.getStarter();
//                if (tomcatStarter != null) {
//                    Exception exception = tomcatStarter.getStartUpException();
//                    if (exception != null) {
//                        throw exception;
//                    }
//                }
//            }
//            if (!LifecycleState.STARTED.equals(container.getState())) {
//                throw new IllegalStateException(container + " failed to start");
//            }
//        }
        throw new UnsupportedOperationException();
    }

    private void startDaemonAwaitThread() {
//        Thread awaitThread = new Thread("container-" + (containerCounter.get())) {
//
//            @Override
//            public void run() {
//                TomcatWebServer.this.tomcat.getServer().await();
//            }
//
//        };
//        awaitThread.setContextClassLoader(getClass().getClassLoader());
//        awaitThread.setDaemon(false);
//        awaitThread.start();
    }

//    @Override
//    public void start() throws WebServerException {
//        synchronized (this.monitor) {
//            if (this.started) {
//                return;
//            }
//            try {
//                addPreviouslyRemovedConnectors();
//                Connector connector = this.tomcat.getConnector();
//                if (connector != null && this.autoStart) {
//                    performDeferredLoadOnStartup();
//                }
//                checkThatConnectorsHaveStarted();
//                this.started = true;
//                logger.info("Tomcat started on port(s): " + getPortsDescription(true) + " with context path '"
//                        + getContextPath() + "'");
//            }
//            catch (ConnectorStartFailedException ex) {
//                stopSilently();
//                throw ex;
//            }
//            catch (Exception ex) {
//                PortInUseException.throwIfPortBindingException(ex, () -> this.tomcat.getConnector().getPort());
//                throw new WebServerException("Unable to start embedded Tomcat server", ex);
//            }
//            finally {
//                Context context = findContext();
//                ContextBindings.unbindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
//            }
//        }
//    }

    private void checkThatConnectorsHaveStarted() {
//        checkConnectorHasStarted(this.tomcat.getConnector());
//        for (Connector connector : this.tomcat.getService().findConnectors()) {
//            checkConnectorHasStarted(connector);
//        }
        throw new UnsupportedOperationException();
    }

    private void checkConnectorHasStarted(Connector connector) {
//        if (LifecycleState.FAILED.equals(connector.getState())) {
//            throw new ConnectorStartFailedException(connector.getPort());
//        }
        throw new UnsupportedOperationException();
    }

    private void stopSilently() {
//        try {
//            stopTomcat();
//        }
//        catch (LifecycleException ex) {
//            // Ignore
//        }
//    }
//
//    private void destroySilently() {
//        try {
//            this.tomcat.destroy();
//        }
//        catch (LifecycleException ex) {
//            // Ignore
//        }
        throw new UnsupportedOperationException();
    }

    // todo 这个方法不能转换类型，暂时有问题
    private void stopTomcat() throws LifecycleException {
//        if (Thread.currentThread().getContextClassLoader() instanceof TomcatEmbeddedWebappClassLoader) {
//            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
//        }
//        this.tomcat.stop();
    }

    private void addPreviouslyRemovedConnectors() {
//        Service[] services = this.tomcat.getServer().findServices();
//        for (Service service : services) {
//            Connector[] connectors = this.serviceConnectors.get(service);
//            if (connectors != null) {
//                for (Connector connector : connectors) {
//                    service.addConnector(connector);
//                    if (!this.autoStart) {
//                        stopProtocolHandler(connector);
//                    }
//                }
//                this.serviceConnectors.remove(service);
//            }
//        }
        throw new UnsupportedOperationException();
    }

    private void stopProtocolHandler(Connector connector) {
//        try {
//            connector.getProtocolHandler().stop();
//        }
//        catch (Exception ex) {
//            logger.error("Cannot pause connector: ", ex);
//        }
        throw new UnsupportedOperationException();
    }

    private void performDeferredLoadOnStartup() {
//        try {
//            for (Container child : this.tomcat.getHost().findChildren()) {
//                if (child instanceof TomcatEmbeddedContext embeddedContext) {
//                    embeddedContext.deferredLoadOnStartup();
//                }
//            }
//        }
//        catch (Exception ex) {
//            if (ex instanceof WebServerException webServerException) {
//                throw webServerException;
//            }
//            throw new WebServerException("Unable to start embedded Tomcat connectors", ex);
//        }
        throw new UnsupportedOperationException();
    }

    Map<Service, Connector[]> getServiceConnectors() {
        return this.serviceConnectors;
    }

//    @Override
//    public void stop() throws WebServerException {
//        synchronized (this.monitor) {
//            boolean wasStarted = this.started;
//            try {
//                this.started = false;
//                try {
//                    if (this.gracefulShutdown != null) {
//                        this.gracefulShutdown.abort();
//                    }
//                    stopTomcat();
//                    this.tomcat.destroy();
//                }
//                catch (LifecycleException ex) {
//                    // swallow and continue
//                }
//            }
//            catch (Exception ex) {
//                throw new WebServerException("Unable to stop embedded Tomcat", ex);
//            }
//            finally {
//                if (wasStarted) {
//                    containerCounter.decrementAndGet();
//                }
//            }
//        }
//    }

    private String getPortsDescription(boolean localPort) {
//        StringBuilder ports = new StringBuilder();
//        for (Connector connector : this.tomcat.getService().findConnectors()) {
//            if (ports.length() != 0) {
//                ports.append(' ');
//            }
//            int port = localPort ? connector.getLocalPort() : connector.getPort();
//            ports.append(port).append(" (").append(connector.getScheme()).append(')');
//        }
//        return ports.toString();
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPort() {
//        Connector connector = this.tomcat.getConnector();
//        if (connector != null) {
//            return connector.getLocalPort();
//        }
//        return -1;
        throw new UnsupportedOperationException();
    }

    private String getContextPath() {
//        return Arrays.stream(this.tomcat.getHost().findChildren())
//                .filter(TomcatEmbeddedContext.class::isInstance)
//                .map(TomcatEmbeddedContext.class::cast)
//                .map(TomcatEmbeddedContext::getPath)
//                .collect(Collectors.joining(" "));
        throw new UnsupportedOperationException();
    }

    /**
     * Returns access to the underlying Tomcat server.
     * @return the Tomcat server
     */
    public Tomcat getTomcat() {
        return this.tomcat;
    }

    @Override
    public void shutDownGracefully(GracefulShutdownCallback callback) {
//        if (this.gracefulShutdown == null) {
//            callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
//            return;
//        }
//        this.gracefulShutdown.shutDownGracefully(callback);
        throw new UnsupportedOperationException();
    }






    /**
     * +++++++++++++++++++++++++++++++++ 下面的都是自己写的代码 +++++++++++++++++++++++++++++++++
     */

    private ServletContext servletContext;
    private HttpServer httpServer;

    private int port = 8080;

    // 自己为了实现异步启动自己的 webserver 而创建的线程池
    private Thread serverThread;

    private StandardContext context;// todo 这个地方是 null， 没有赋值和初始化的


    public TomcatWebServer() {
        this.httpServer = new HttpServer();
        this.servletContext = createServletContext();
        // todo Tomcat 需要初始化，java: variable autoStart might not have been initialized
        this.tomcat = new Tomcat();
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
        // todo 这里的 context 可以不传，怎么在下面一层的代码传 context 这个参数，这一层不要参数行不行？
        return new ApplicationContext(context);
    }

    @Override
    public void start() throws WebServerException {
        // 启动自定义 HttpServer

        // 重新使用线程来启动 webserver ，避免阻塞 springboot 主线程
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
        // 实现一个关闭线程的操作，仅测试用下
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

}
