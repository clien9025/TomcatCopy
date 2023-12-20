package org.apache.catalina.core;

import org.apache.tomcat.util.ExceptionUtils;

/**
 * @author zhanyang
 */
public class AprLifecycleListener {

    protected static final Object lock = new Object();

    //
    public static boolean isAprAvailable() {
        // https://bz.apache.org/bugzilla/show_bug.cgi?id=48613
        // AprStatus.isInstanceCreated() 的值默认是 false
        if (AprStatus.isInstanceCreated()) {
            // 值是 True 的话，就是 AprStatus 的实例被创建了，那就使用多线程去初始化（下面有初始化代码）
            synchronized (lock) {
                init();
            }
        }
        // 返回 isAprAvailable 的值， isAprAvailable 的默认值是 false
        return AprStatus.isAprAvailable();
    }
    private static void init() {
//        int rqver = TCN_REQUIRED_MAJOR * 1000 + TCN_REQUIRED_MINOR * 100 + TCN_REQUIRED_PATCH;
//        int rcver = TCN_RECOMMENDED_MAJOR * 1000 + TCN_RECOMMENDED_MINOR * 100 + TCN_RECOMMENDED_PV;
//
//        if (AprStatus.isAprInitialized()) {
//            return;
//        }
//        AprStatus.setAprInitialized(true);
//
//        try {
//            Library.initialize(null);
//            tcnMajor = Library.TCN_MAJOR_VERSION;
//            tcnMinor = Library.TCN_MINOR_VERSION;
//            tcnPatch = Library.TCN_PATCH_VERSION;
//            tcnVersion = tcnMajor * 1000 + tcnMinor * 100 + tcnPatch;
//        } catch (LibraryNotFoundError lnfe) {
//            // Library not on path
//            if (log.isDebugEnabled()) {
//                log.debug(sm.getString("aprListener.aprInitDebug", lnfe.getLibraryNames(),
//                        System.getProperty("java.library.path"), lnfe.getMessage()), lnfe);
//            }
//            initInfoLogMessages.add(sm.getString("aprListener.aprInit", System.getProperty("java.library.path")));
//            return;
//        } catch (Throwable t) {
//            // Library present but failed to load
//            t = ExceptionUtils.unwrapInvocationTargetException(t);
//            ExceptionUtils.handleThrowable(t);
//            log.warn(sm.getString("aprListener.aprInitError", t.getMessage()), t);
//            return;
//        }
//        if (tcnMajor > 1 && "off".equalsIgnoreCase(SSLEngine)) {
//            log.error(sm.getString("aprListener.sslRequired", SSLEngine, Library.versionString()));
//            try {
//                // Tomcat Native 2.x onwards requires SSL
//                terminateAPR();
//            } catch (Throwable t) {
//                t = ExceptionUtils.unwrapInvocationTargetException(t);
//                ExceptionUtils.handleThrowable(t);
//            }
//            return;
//        }
//        if (tcnVersion < rqver) {
//            log.error(sm.getString("aprListener.tcnInvalid", Library.versionString(),
//                    TCN_REQUIRED_MAJOR + "." + TCN_REQUIRED_MINOR + "." + TCN_REQUIRED_PATCH));
//            try {
//                // Terminate the APR in case the version
//                // is below required.
//                terminateAPR();
//            } catch (Throwable t) {
//                t = ExceptionUtils.unwrapInvocationTargetException(t);
//                ExceptionUtils.handleThrowable(t);
//            }
//            return;
//        }
//        if (tcnVersion < rcver) {
//            initInfoLogMessages.add(sm.getString("aprListener.tcnVersion", Library.versionString(),
//                    TCN_RECOMMENDED_MAJOR + "." + TCN_RECOMMENDED_MINOR + "." + TCN_RECOMMENDED_PV));
//        }
//
//        initInfoLogMessages
//                .add(sm.getString("aprListener.tcnValid", Library.versionString(), Library.aprVersionString()));
//
//        AprStatus.setAprAvailable(true);
        throw new UnsupportedOperationException();
    }
}
