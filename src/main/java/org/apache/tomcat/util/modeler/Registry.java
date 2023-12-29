package org.apache.tomcat.util.modeler;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/**
 * @author zhanyang
 */
public class Registry {
    /**
     * The registry instance created by our factory method the first time it is
     * called.
     */
    private static Registry registry = null;
    /**
     * The Log instance to which we will write our log messages.
     */
    // 12/28 第一步
    private static final Log log = LogFactory.getLog(Registry.class);// 将 Registry 类传给 getLog 方法
    private static final StringManager sm = StringManager.getManager(Registry.class);


    // 这是个 synchronized 方法
    public static synchronized void disableRegistry() {
        // 注册表为 null 时，创建一个无描述的注册表
        if (registry == null) {
            registry = new NoDescriptorRegistry();
        } else if (!(registry instanceof NoDescriptorRegistry)) {// 如果 registry 不属于 NoDescriptorRegistry 类的话
            log.warn(sm.getString("registry.noDisable"));// 警告 log
        }
//        throw new UnsupportedOperationException();
    }
}

