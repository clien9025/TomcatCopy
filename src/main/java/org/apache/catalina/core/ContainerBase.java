package org.apache.catalina.core;

import org.apache.catalina.Container;
import org.apache.catalina.Pipeline;
import org.apache.catalina.util.LifecycleMBeanBase;

// todo 问一下，是保持 JmxEnabled 和 Container 都是 default 状态，LifecycleMBeanBase 也 final 实现的情况；
//  还是保持 JmxEnabled 和 Container 都不是 default 状态，只有 LifecycleMBeanBase 是 final 实现的情况；
//  还是保持 JmxEnabled 和 Container 都不是 default 状态，LifecycleMBeanBase 也不实现这个 getObjectName() 方法；
//  上述经过我的验证都是不报错的
public abstract class ContainerBase extends LifecycleMBeanBase implements Container {

}
