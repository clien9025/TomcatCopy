package org.apache.catalina.util;

import org.apache.catalina.JmxEnabled;

import javax.management.ObjectName;

public abstract class LifecycleMBeanBase extends LifecycleBase
        implements JmxEnabled {


    /**
     * 源码这里是实现了，并且类型是 final
     * @return
     */
    @Override
    public final ObjectName getObjectName() {
//        return oname;
        throw new UnsupportedOperationException();
    }


}
