package org.apache.juli.logging;

import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.util.ServiceLoader;

/**
 * @author zhanyang
 */
//@ServiceConsumer(value=Log.class)
public class LogFactory {
    private static final LogFactory singleton = new LogFactory();

    private final Constructor<? extends Log> discoveredLogConstructor;


    /**
     * Private constructor that is not available for public use.
     */
    private LogFactory() {
        /*
         * Work-around known a JRE bug.
         * https://bugs.openjdk.java.net/browse/JDK-8194653
         *
         * Pre-load the default file system. No performance impact as we need to
         * load the default file system anyway. Just do it earlier to avoid the
         * potential deadlock.
         *
         * This can be removed once the oldest JRE supported by Tomcat includes
         * a fix.
         */
        FileSystems.getDefault();

        // Look via a ServiceLoader for a Log implementation that has a
        // constructor taking the String name.
        ServiceLoader<Log> logLoader = ServiceLoader.load(Log.class);
        Constructor<? extends Log> m=null;
        for (Log log: logLoader) {
            Class<? extends Log> c=log.getClass();
            try {
                m=c.getConstructor(String.class);
                break;
            }
            catch (NoSuchMethodException | SecurityException e) {
                throw new Error(e);
            }
        }
        // Constructor<? extends Log> discoveredLogConstructor
        discoveredLogConstructor=m;
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param clazz Class from which a log name will be derived
     *
     * @return A log instance with a name of clazz.getName()
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static Log getLog(Class<?> clazz)
            throws LogConfigurationException {
        // 12/28 第二步
        // 返回 LogFactory 类的一个单例对象.getInstance(clazz) 的对象
        return getFactory().getInstance(clazz);// 调取其他方法
//        throw new UnsupportedOperationException();
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     *
     * @return A log instance with the requested name
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public static Log getLog(String name)
            throws LogConfigurationException {
        return getFactory().getInstance(name);
    }


    /**
     * <p>Construct (if necessary) and return a <code>LogFactory</code>
     * instance, using the following ordered lookup procedure to determine
     * the name of the implementation class to be loaded.</p>
     * <ul>
     * <li>The <code>org.apache.commons.logging.LogFactory</code> system
     *     property.</li>
     * <li>The JDK 1.3 Service Discovery mechanism</li>
     * <li>Use the properties file <code>commons-logging.properties</code>
     *     file, if found in the class path of this class.  The configuration
     *     file is in standard <code>java.util.Properties</code> format and
     *     contains the fully qualified name of the implementation class
     *     with the key being the system property defined above.</li>
     * <li>Fall back to a default implementation class
     *     (<code>org.apache.commons.logging.impl.LogFactoryImpl</code>).</li>
     * </ul>
     *
     * <p><em>NOTE</em> - If the properties file method of identifying the
     * <code>LogFactory</code> implementation class is utilized, all of the
     * properties defined in this file will be set as configuration attributes
     * on the corresponding <code>LogFactory</code> instance.</p>
     *
     * @return The singleton LogFactory instance
     *
     * @exception LogConfigurationException if the implementation class is not
     *  available or cannot be instantiated.
     */
    public static LogFactory getFactory() throws LogConfigurationException {
        // private static final LogFactory singleton = new LogFactory(); 返回一个 LogFactory 类的一个单利对象
        return singleton;
//        throw new UnsupportedOperationException();
    }

    /**
     * Convenience method to derive a name from the specified class and
     * call <code>getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     *
     * @return A log instance with a name of clazz.getName()
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public Log getInstance(Class<?> clazz) throws LogConfigurationException {
        // 12/28 第三步
        // clazz.getName() 的结果 ："org.apache.tomcat.util.modeler.Registry"
        return getInstance( clazz.getName());// 将类名（String）当做参数
//        throw new UnsupportedOperationException();
    }


    // --------------------------------------------------------- Public Methods

    // only those 2 methods need to change to use a different direct logger.

    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     *
     * @return A log instance with the requested name
     *
     * @exception LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    public Log getInstance(String name) throws LogConfigurationException {
        // 12/28 第四步
        // Constructor<? extends Log> discoveredLogConstructor;(创建一个 发现日志的 构造器)
        if (discoveredLogConstructor == null) {
            return DirectJDKLog.getInstance(name);
        }

        try {
            // 动态的使用 函数构造器访问器 创建了一个实例对象，constructorAccessor.newInstance(args)
            return discoveredLogConstructor.newInstance(name);
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new LogConfigurationException(e);
        }
//        throw new UnsupportedOperationException();
    }
}
