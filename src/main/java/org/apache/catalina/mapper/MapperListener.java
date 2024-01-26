package org.apache.catalina.mapper;

import org.apache.catalina.*;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper listener.
 *
 * @author Remy Maucherat
 * @author Costin Manolache
 */
public class MapperListener extends LifecycleMBeanBase implements ContainerListener, LifecycleListener {


    private static final Log log = LogFactory.getLog(MapperListener.class);


    // ----------------------------------------------------- Instance Variables
    /**
     * Associated mapper.
     */
    private final Mapper mapper;

    /**
     * Associated service
     */
    private final Service service;


    /**
     * The string manager for this package.
     */
    private static final StringManager sm = StringManager.getManager(Constants.Package);

    /**
     * The domain (effectively the engine) this mapper is associated with
     */
    private final String domain = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Create mapper listener.
     *
     * @param service The service this listener is associated with
     */
    public MapperListener(Service service) {
        this.service = service;
        this.mapper = service.getMapper();
    }


    // ------------------------------------------------------- Lifecycle Methods

    @Override
    public void startInternal() throws LifecycleException {

        setState(LifecycleState.STARTING);

        Engine engine = service.getContainer();
        if (engine == null) {
            return;
        }

        findDefaultHost();

        addListeners(engine);

        Container[] conHosts = engine.findChildren();
        for (Container conHost : conHosts) {
            Host host = (Host) conHost;
            if (!LifecycleState.NEW.equals(host.getState())) {
                // Registering the host will register the context and wrappers
                registerHost(host);
            }
        }
    }


    @Override
    public void stopInternal() throws LifecycleException {
//        setState(LifecycleState.STOPPING);
//
//        Engine engine = service.getContainer();
//        if (engine == null) {
//            return;
//        }
//        removeListeners(engine);
        throw new UnsupportedOperationException();
    }


    @Override
    protected String getDomainInternal() {
        if (service instanceof LifecycleMBeanBase) {
            return service.getDomain();
        } else {
            return null;
        }
    }


    @Override
    protected String getObjectNameKeyProperties() {
        // Same as connector but Mapper rather than Connector
        return "type=Mapper";
    }

    // --------------------------------------------- Container Listener methods

    @Override
    public void containerEvent(ContainerEvent event) {

//        if (Container.ADD_CHILD_EVENT.equals(event.getType())) {
//            Container child = (Container) event.getData();
//            addListeners(child);
//            // If child is started then it is too late for life-cycle listener
//            // to register the child so register it here
//            if (child.getState().isAvailable()) {
//                if (child instanceof Host) {
//                    registerHost((Host) child);
//                } else if (child instanceof Context) {
//                    registerContext((Context) child);
//                } else if (child instanceof Wrapper) {
//                    // Only if the Context has started. If it has not, then it
//                    // will have its own "after_start" life-cycle event later.
//                    if (child.getParent().getState().isAvailable()) {
//                        registerWrapper((Wrapper) child);
//                    }
//                }
//            }
//        } else if (Container.REMOVE_CHILD_EVENT.equals(event.getType())) {
//            Container child = (Container) event.getData();
//            removeListeners(child);
//            // No need to unregister - life-cycle listener will handle this when
//            // the child stops
//        } else if (Host.ADD_ALIAS_EVENT.equals(event.getType())) {
//            // Handle dynamically adding host aliases
//            mapper.addHostAlias(((Host) event.getSource()).getName(), event.getData().toString());
//        } else if (Host.REMOVE_ALIAS_EVENT.equals(event.getType())) {
//            // Handle dynamically removing host aliases
//            mapper.removeHostAlias(event.getData().toString());
//        } else if (Wrapper.ADD_MAPPING_EVENT.equals(event.getType())) {
//            // Handle dynamically adding wrappers
//            Wrapper wrapper = (Wrapper) event.getSource();
//            Context context = (Context) wrapper.getParent();
//            String contextPath = context.getPath();
//            if ("/".equals(contextPath)) {
//                contextPath = "";
//            }
//            String version = context.getWebappVersion();
//            String hostName = context.getParent().getName();
//            String wrapperName = wrapper.getName();
//            String mapping = (String) event.getData();
//            boolean jspWildCard = ("jsp".equals(wrapperName) && mapping.endsWith("/*"));
//            mapper.addWrapper(hostName, contextPath, version, mapping, wrapper, jspWildCard,
//                    context.isResourceOnlyServlet(wrapperName));
//        } else if (Wrapper.REMOVE_MAPPING_EVENT.equals(event.getType())) {
//            // Handle dynamically removing wrappers
//            Wrapper wrapper = (Wrapper) event.getSource();
//
//            Context context = (Context) wrapper.getParent();
//            String contextPath = context.getPath();
//            if ("/".equals(contextPath)) {
//                contextPath = "";
//            }
//            String version = context.getWebappVersion();
//            String hostName = context.getParent().getName();
//
//            String mapping = (String) event.getData();
//
//            mapper.removeWrapper(hostName, contextPath, version, mapping);
//        } else if (Context.ADD_WELCOME_FILE_EVENT.equals(event.getType())) {
//            // Handle dynamically adding welcome files
//            Context context = (Context) event.getSource();
//
//            String hostName = context.getParent().getName();
//
//            String contextPath = context.getPath();
//            if ("/".equals(contextPath)) {
//                contextPath = "";
//            }
//
//            String welcomeFile = (String) event.getData();
//
//            mapper.addWelcomeFile(hostName, contextPath, context.getWebappVersion(), welcomeFile);
//        } else if (Context.REMOVE_WELCOME_FILE_EVENT.equals(event.getType())) {
//            // Handle dynamically removing welcome files
//            Context context = (Context) event.getSource();
//
//            String hostName = context.getParent().getName();
//
//            String contextPath = context.getPath();
//            if ("/".equals(contextPath)) {
//                contextPath = "";
//            }
//
//            String welcomeFile = (String) event.getData();
//
//            mapper.removeWelcomeFile(hostName, contextPath, context.getWebappVersion(), welcomeFile);
//        } else if (Context.CLEAR_WELCOME_FILES_EVENT.equals(event.getType())) {
//            // Handle dynamically clearing welcome files
//            Context context = (Context) event.getSource();
//
//            String hostName = context.getParent().getName();
//
//            String contextPath = context.getPath();
//            if ("/".equals(contextPath)) {
//                contextPath = "";
//            }
//
//            mapper.clearWelcomeFiles(hostName, contextPath, context.getWebappVersion());
//        }
        throw new UnsupportedOperationException();
    }


    // ------------------------------------------------------ Protected Methods

    private void findDefaultHost() {

        Engine engine = service.getContainer();
        String defaultHost = engine.getDefaultHost();

        boolean found = false;

        if (defaultHost != null && defaultHost.length() > 0) {
            Container[] containers = engine.findChildren();

            for (Container container : containers) {
                Host host = (Host) container;
                if (defaultHost.equalsIgnoreCase(host.getName())) {
                    found = true;
                    break;
                }

                String[] aliases = host.findAliases();
                for (String alias : aliases) {
                    if (defaultHost.equalsIgnoreCase(alias)) {
                        found = true;
                        break;
                    }
                }
            }
        }

        if (found) {
            mapper.setDefaultHostName(defaultHost);
        } else {
            log.error(sm.getString("mapperListener.unknownDefaultHost", defaultHost, service));
        }
    }


    /**
     * Register host.
     */
    private void registerHost(Host host) {
        String[] aliases = host.findAliases();
        mapper.addHost(host.getName(), aliases, host);

        for (Container container : host.findChildren()) {
            if (container.getState().isAvailable()) {
                registerContext((Context) container);
            }
        }

        // Default host may have changed
        findDefaultHost();

        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.registerHost", host.getName(), domain, service));
        }
    }


    /**
     * Unregister host.
     */
    private void unregisterHost(Host host) {

//        String hostname = host.getName();
//
//        mapper.removeHost(hostname);
//
//        // Default host may have changed
//        findDefaultHost();
//
//        if (log.isDebugEnabled()) {
//            log.debug(sm.getString("mapperListener.unregisterHost", hostname, domain, service));
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Unregister wrapper.
     */
    private void unregisterWrapper(Wrapper wrapper) {

//        Context context = ((Context) wrapper.getParent());
//        String contextPath = context.getPath();
//        String wrapperName = wrapper.getName();
//
//        if ("/".equals(contextPath)) {
//            contextPath = "";
//        }
//        String version = context.getWebappVersion();
//        String hostName = context.getParent().getName();
//
//        String[] mappings = wrapper.findMappings();
//
//        for (String mapping : mappings) {
//            mapper.removeWrapper(hostName, contextPath, version, mapping);
//        }
//
//        if (log.isDebugEnabled()) {
//            log.debug(sm.getString("mapperListener.unregisterWrapper", wrapperName, contextPath, service));
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Register context.
     */
    private void registerContext(Context context) {

        String contextPath = context.getPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        Host host = (Host) context.getParent();

        WebResourceRoot resources = context.getResources();
        String[] welcomeFiles = context.findWelcomeFiles();
        List<WrapperMappingInfo> wrappers = new ArrayList<>();

        for (Container container : context.findChildren()) {
            prepareWrapperMappingInfo(context, (Wrapper) container, wrappers);

            if (log.isDebugEnabled()) {
                log.debug(sm.getString("mapperListener.registerWrapper", container.getName(), contextPath, service));
            }
        }

        mapper.addContextVersion(host.getName(), host, contextPath, context.getWebappVersion(), context, welcomeFiles,
                resources, wrappers);

        if (log.isDebugEnabled()) {
            log.debug(sm.getString("mapperListener.registerContext", contextPath, service));
        }
    }


    /**
     * Unregister context.
     */
    private void unregisterContext(Context context) {

//        String contextPath = context.getPath();
//        if ("/".equals(contextPath)) {
//            contextPath = "";
//        }
//        String hostName = context.getParent().getName();
//
//        if (context.getPaused()) {
//            if (log.isDebugEnabled()) {
//                log.debug(sm.getString("mapperListener.pauseContext", contextPath, service));
//            }
//
//            mapper.pauseContextVersion(context, hostName, contextPath, context.getWebappVersion());
//        } else {
//            if (log.isDebugEnabled()) {
//                log.debug(sm.getString("mapperListener.unregisterContext", contextPath, service));
//            }
//
//            mapper.removeContextVersion(context, hostName, contextPath, context.getWebappVersion());
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Register wrapper.
     */
    private void registerWrapper(Wrapper wrapper) {

//        Context context = (Context) wrapper.getParent();
//        String contextPath = context.getPath();
//        if ("/".equals(contextPath)) {
//            contextPath = "";
//        }
//        String version = context.getWebappVersion();
//        String hostName = context.getParent().getName();
//
//        List<WrapperMappingInfo> wrappers = new ArrayList<>();
//        prepareWrapperMappingInfo(context, wrapper, wrappers);
//        mapper.addWrappers(hostName, contextPath, version, wrappers);
//
//        if (log.isDebugEnabled()) {
//            log.debug(sm.getString("mapperListener.registerWrapper", wrapper.getName(), contextPath, service));
//        }
        throw new UnsupportedOperationException();
    }

    /*
     * Populate <code>wrappers</code> list with information for registration of mappings for this wrapper in this
     * context.
     */
    private void prepareWrapperMappingInfo(Context context, Wrapper wrapper, List<WrapperMappingInfo> wrappers) {
//        String wrapperName = wrapper.getName();
//        boolean resourceOnly = context.isResourceOnlyServlet(wrapperName);
//        String[] mappings = wrapper.findMappings();
//        for (String mapping : mappings) {
//            boolean jspWildCard = (wrapperName.equals("jsp") && mapping.endsWith("/*"));
//            wrappers.add(new WrapperMappingInfo(mapping, wrapper, jspWildCard, resourceOnly));
//        }
         throw new UnsupportedOperationException();
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
//        if (event.getType().equals(AFTER_START_EVENT)) {
//            Object obj = event.getSource();
//            if (obj instanceof Wrapper) {
//                Wrapper w = (Wrapper) obj;
//                // Only if the Context has started. If it has not, then it will
//                // have its own "after_start" event later.
//                if (w.getParent().getState().isAvailable()) {
//                    registerWrapper(w);
//                }
//            } else if (obj instanceof Context) {
//                Context c = (Context) obj;
//                // Only if the Host has started. If it has not, then it will
//                // have its own "after_start" event later.
//                if (c.getParent().getState().isAvailable()) {
//                    registerContext(c);
//                }
//            } else if (obj instanceof Host) {
//                registerHost((Host) obj);
//            }
//        } else if (event.getType().equals(BEFORE_STOP_EVENT)) {
//            Object obj = event.getSource();
//            if (obj instanceof Wrapper) {
//                unregisterWrapper((Wrapper) obj);
//            } else if (obj instanceof Context) {
//                unregisterContext((Context) obj);
//            } else if (obj instanceof Host) {
//                unregisterHost((Host) obj);
//            }
//        }
        throw new UnsupportedOperationException();
    }


    /**
     * Add this mapper to the container and all child containers
     * <p>
     * 其目的是向指定的容器及其所有子容器添加监听器。
     * 这在容器层次结构中是一种常见的模式，用于确保在容器及其所有子容器上都能监听和响应特定事件。
     *
     * @param container the container (and any associated children) to which the mapper is to be added
     */
    private void addListeners(Container container) {
        container.addContainerListener(this);
        container.addLifecycleListener(this);
        for (Container child : container.findChildren()) {
            addListeners(child);
        }
    }


    /**
     * Remove this mapper from the container and all child containers
     *
     * @param container the container (and any associated children) from which the mapper is to be removed
     */
    private void removeListeners(Container container) {
//        container.removeContainerListener(this);
//        container.removeLifecycleListener(this);
//        for (Container child : container.findChildren()) {
//            removeListeners(child);
//        }
        throw new UnsupportedOperationException();
    }
}
