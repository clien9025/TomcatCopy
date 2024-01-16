/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.modeler.modules;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.ObjectName;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.*;
import org.apache.tomcat.util.res.StringManager;

public class MbeansDescriptorsIntrospectionSource extends ModelerSource{
    private static final Log log = LogFactory.getLog(MbeansDescriptorsIntrospectionSource.class);
    private static final StringManager sm = StringManager.getManager(MbeansDescriptorsIntrospectionSource.class);

    private Registry registry;
    private String type;
    private final List<ObjectName> mbeans = new ArrayList<>();

    public void setRegistry(Registry reg) {
        this.registry=reg;
    }

    /**
     * Used if a single component is loaded
     *
     * @param type The type
     */
    public void setType( String type ) {
        this.type=type;
    }

    public void setSource( Object source ) {
        this.source=source;
    }

    @Override
    public List<ObjectName> loadDescriptors(Registry registry, String type,
                                            Object source) throws Exception {
        setRegistry(registry);
        setType(type);
        setSource(source);
        execute();
        return mbeans;
    }

    public void execute() throws Exception {
        /* 1. 初始化或获取注册表 */
        // 检查 registry（可能是一个用于注册和管理 MBeans 的注册表对象）是否为 null。
        // 如果是，通过调用 Registry.getRegistry(null, null) 获取一个注册表实例
        if( registry==null ) {
            registry=Registry.getRegistry(null, null);
        }

        try {
            /* 2. 创建 ManagedBean */
            ManagedBean managed = createManagedBean(registry, null,
                    (Class<?>)source, type);
            if( managed==null ) {
                return;
            }
            /* 3. 设置 ManagedBean 的名称并注册 */
            // 如果成功创建了 ManagedBean 实例（managed 非 null），设置它的名称为 type
            managed.setName( type );
            // 将这个 ManagedBean 添加到注册表中
            registry.addManagedBean(managed);

        } catch( Exception ex ) {
            log.error(sm.getString("modules.readDescriptorsError"), ex);
        }
    }

    // ------------ Implementation for non-declared introspection classes

    private static final Map<String,String> specialMethods = new HashMap<>();
    static {
        specialMethods.put( "preDeregister", "");
        specialMethods.put( "postDeregister", "");
    }

    private static final Class<?>[] supportedTypes  = new Class[] {
            Boolean.class,
            Boolean.TYPE,
            Byte.class,
            Byte.TYPE,
            Character.class,
            Character.TYPE,
            Short.class,
            Short.TYPE,
            Integer.class,
            Integer.TYPE,
            Long.class,
            Long.TYPE,
            Float.class,
            Float.TYPE,
            Double.class,
            Double.TYPE,
            String.class,
            String[].class,
            BigDecimal.class,
            BigInteger.class,
            ObjectName.class,
            Object[].class,
            java.io.File.class,
    };


    /**
     * Check if this class is one of the supported types.
     * If the class is supported, returns true.  Otherwise,
     * returns false.
     * @param ret The class to check
     * @return boolean True if class is supported
     */
    private boolean supportedType(Class<?> ret) {
        for (Class<?> supportedType : supportedTypes) {
            if (ret == supportedType) {
                return true;
            }
        }
        if (isBeanCompatible(ret)) {
            return true;
        }
        return false;
    }

    /**
     * Check if this class conforms to JavaBeans specifications.
     * If the class is conformant, returns true.
     *
     * @param javaType The class to check
     * @return boolean True if the class is compatible.
     */
    private boolean isBeanCompatible(Class<?> javaType) {
        // Must be a non-primitive and non array
        if (javaType.isArray() || javaType.isPrimitive()) {
            return false;
        }

        // Anything in the java or javax package that
        // does not have a defined mapping is excluded.
        if (javaType.getName().startsWith("java.") ||
                javaType.getName().startsWith("javax.")) {
            return false;
        }

        try {
            javaType.getConstructor(new Class[]{});
        } catch (NoSuchMethodException e) {
            return false;
        }

        // Make sure superclass is compatible
        Class<?> superClass = javaType.getSuperclass();
        if (superClass != null &&
                superClass != Object.class &&
                superClass != Exception.class &&
                superClass != Throwable.class) {
            if (!isBeanCompatible(superClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Process the methods and extract 'attributes', methods, etc.
     *
     * 这段代码定义了 initMethods 方法，其作用是对一个给定的 Java 类 (realClass) 进行分析，以提取和分类其方法。
     * 这些方法被分类为“可读属性”（getters）、“可写属性”（setters）和“可调用方法”（invoke）。
     *
     * @param realClass The class to process
     * @param attNames The attribute name (complete)
     * @param getAttMap The readable attributes map
     * @param setAttMap The settable attributes map
     * @param invokeAttMap The invokable attributes map
     */
    private void initMethods(Class<?> realClass, Set<String> attNames,
                             Map<String,Method> getAttMap, Map<String,Method> setAttMap,
                             Map<String,Method> invokeAttMap) {
        /* 1. 获取类的所有方法 */
        // 使用 realClass.getMethods() 获取类的所有公共方法（包括从父类继承的方法）
        Method[] methods = realClass.getMethods();
        /* 2. 遍历每个方法 */
        for (Method method : methods) {
            String name = method.getName();
            // 对于 realClass 中的每个方法，进行以下检查和处理：
            // 忽略静态方法，因为它们不属于实例属性或操作。
            // 忽略非公共方法。
            // 忽略 Object 类中声明的方法。
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (!Modifier.isPublic(method.getModifiers())) {
                if (log.isDebugEnabled()) {
                    log.debug("Not public " + method);
                }
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            /* 3. 处理属性方法 */
            Class<?>[] params = method.getParameterTypes();
            // 使用条件 name.startsWith("get") && params.length == 0 来判断，这意味着方法名应该以 "get" 开头，并且没有参数。
            if (name.startsWith("get") && params.length == 0) {
                // 如果是一个 getter 方法，检查其返回类型 ret 是否被支持（使用 supportedType(ret) 方法）。
                // 如果返回类型不支持，则跳过该方法。
                Class<?> ret = method.getReturnType();
                if (!supportedType(ret)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unsupported type " + method);
                    }
                    continue;
                }
                // 如果返回类型被支持，将方法名转换成属性名（去掉 "get" 并使首字母小写），
                // 然后将这个属性名和对应的方法添加到 getAttMap 和 attNames。
                name = unCapitalize(name.substring(3));

                getAttMap.put(name, method);
                attNames.add(name);
            } else if (name.startsWith("is") && params.length == 0) {
                Class<?> ret = method.getReturnType();
                if (Boolean.TYPE != ret) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unsupported type " + method + " " + ret);
                    }
                    continue;
                }
                name = unCapitalize(name.substring(2));

                getAttMap.put(name, method);
                attNames.add(name);

            } else if (name.startsWith("set") && params.length == 1) {
                if (!supportedType(params[0])) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unsupported type " + method + " " + params[0]);
                    }
                    continue;
                }
                name = unCapitalize(name.substring(3));
                setAttMap.put(name, method);
                attNames.add(name);
            } else {
                /* 对无参数方法的处理 */
                // 将不符合 getter 和 setter 模式的方法视为可调用方法。这些方法可能是类的业务逻辑方法，添加到 invokeAttMap 中。
                // 如果方法没有参数 (params.length == 0)，首先检查该方法名是否存在于 specialMethods 映射中。
                // 如果存在，说明这是一个特殊的方法，应该被忽略，不视为常规的可调用操作。
                if (params.length == 0) {
                    if (specialMethods.get(method.getName()) != null) {
                        continue;
                    }
                    // 如果该方法不是特殊方法，将其添加到 invokeAttMap 中，表示它是一个可调用的操作。
                    invokeAttMap.put(name, method);
                } else {
                    /* 对有参数方法的处理 */
                    boolean supported = true;
                    for (Class<?> param : params) {
                        if (!supportedType(param)) {
                            supported = false;
                            break;
                        }
                    }
                    // 只有当所有参数都是支持的类型时，该方法才被认为是一个可调用的操作，并将其添加到 invokeAttMap。
                    if (supported) {
                        invokeAttMap.put(name, method);
                    }
                }
            }
        }
    }


    /**
     * XXX Find if the 'className' is the name of the MBean or
     *       the real class ( I suppose first )
     * XXX Read (optional) descriptions from a .properties, generated
     *       from source
     * XXX Deal with constructors
     *
     * @param registry The Bean registry (not used)
     * @param domain The bean domain (not used)
     * @param realClass The class to analyze
     * @param type The bean type
     * @return ManagedBean The create MBean
     */
    public ManagedBean createManagedBean(Registry registry, String domain,
                                         Class<?> realClass, String type)
    {
        ManagedBean mbean = new ManagedBean();


        Set<String> attrNames = new HashSet<>();
        // key: attribute val: getter method
        Map<String, Method> getAttMap = new HashMap<>();
        // key: attribute val: setter method
        Map<String, Method> setAttMap = new HashMap<>();
        // key: operation val: invoke method
        Map<String, Method> invokeAttMap = new HashMap<>();

        /* 分析类的方法 */
        initMethods(realClass, attrNames, getAttMap, setAttMap, invokeAttMap);
        /* 处理属性信息 */
        try {
            for (String name : attrNames) {
                AttributeInfo ai = new AttributeInfo();
                ai.setName(name);
                Method gm = getAttMap.get(name);
                if (gm != null) {
                    ai.setGetMethod(gm.getName());
                    Class<?> t = gm.getReturnType();
                    if (t != null) {
                        ai.setType(t.getName());
                    }
                }
                Method sm = setAttMap.get(name);
                if (sm != null) {
                    Class<?> t = sm.getParameterTypes()[0];
                    if (t != null) {
                        ai.setType(t.getName());
                    }
                    ai.setSetMethod(sm.getName());
                }
                ai.setDescription("Introspected attribute " + name);
                if (log.isDebugEnabled()) {
                    log.debug("Introspected attribute " + name + " " + gm + " " + sm);
                }
                if (gm == null) {
                    ai.setReadable(false);
                }
                if (sm == null) {
                    ai.setWriteable(false);
                }
                if (sm != null || gm != null) {
                    mbean.addAttribute(ai);
                }
            }

            // This map is populated by iterating the methods (which end up as
            // values in the Map) and obtaining the key from the value. It is
            // impossible for a key to be associated with a null value.
            for (Entry<String, Method> entry : invokeAttMap.entrySet()) {
                String name = entry.getKey();
                Method m = entry.getValue();

                OperationInfo op = new OperationInfo();
                op.setName(name);
                op.setReturnType(m.getReturnType().getName());
                op.setDescription("Introspected operation " + name);
                Class<?>[] params = m.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    ParameterInfo pi = new ParameterInfo();
                    pi.setType(params[i].getName());
                    pi.setName(("param" + i).intern());
                    pi.setDescription(("Introspected parameter param" + i).intern());
                    op.addParameter(pi);
                }
                mbean.addOperation(op);
            }

            if (log.isDebugEnabled()) {
                log.debug("Setting name: " + type);
            }
            mbean.setName(type);

            return mbean;
        } catch (Exception ex) {
            log.error(sm.getString("source.introspectionError", realClass.getName()), ex);
            return null;
        }

    }


    // -------------------- Utils --------------------
    /**
     * Converts the first character of the given
     * String into lower-case.
     *
     * @param name The string to convert
     * @return String
     */
    private static String unCapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
