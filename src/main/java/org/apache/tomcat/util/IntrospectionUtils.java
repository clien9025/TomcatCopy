package org.apache.tomcat.util;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utils for introspection and reflection
 */
public final class IntrospectionUtils {

    private static final Log log = LogFactory.getLog(IntrospectionUtils.class);
    private static final StringManager sm = StringManager.getManager(IntrospectionUtils.class);



    /**
     * Find a method with the right name If found, call the method ( if param is
     * int or boolean we'll convert value to the right type before) - that means
     * you can have setDebug(1).
     * @param o The object to set a property on
     * @param name The property name
     * @param value The property value
     * @return <code>true</code> if operation was successful
     */
    public static boolean setProperty(Object o, String name, String value) {
        return setProperty(o, name, value, true, null);
    }


    @SuppressWarnings("null") // setPropertyMethodVoid is not null when used
    public static boolean setProperty(Object o, String name, String value,
                                      boolean invokeSetProperty, StringBuilder actualMethod) {
        // log 的级别不够就会返回 false ，然后就不开启调试
        if (log.isDebugEnabled()) {
            log.debug("IntrospectionUtils: setProperty(" +
                    o.getClass() + " " + name + "=" + value + ")");
        }
        // XReflectionIntrospectionUtils.isEnabled() 这个方法永远返回 false；actualMethod 上面默认传入是 null（2023/1/6暂时理解）
        if (actualMethod == null && XReflectionIntrospectionUtils.isEnabled()) {
            // 这方法里面永远抛出的是 throw new UnsupportedOperationException();
            return XReflectionIntrospectionUtils.setPropertyInternal(o, name, value, invokeSetProperty);
        }

        String setter = "set" + capitalize(name);

        try {
            Method methods[] = findMethods(o.getClass());// 获取类里面的方法们
            Method setPropertyMethodVoid = null;
            Method setPropertyMethodBool = null;

            /* 第一步、找 setFoo( String ) 类型的方法 */
            // First, the ideal case - a setFoo( String ) method
            for (Method item : methods) {
                Class<?> paramT[] = item.getParameterTypes();
                // 判断是否是 setPort 方法以及传入的元素类型的长度是不是1以及（若前面的两个条件都满足的话，才会到达这个判断）获取的元素数组
                // 的第一个元素是不是 String 类型的
                if (setter.equals(item.getName()) && paramT.length == 1
                        && "java.lang.String".equals(paramT[0].getName())) {
                    item.invoke(o, new Object[]{value});// 调用方法，以及将 value（参数） 放入创建的数组（数组可以放入多个参数，如果有需要的话）
                    if (actualMethod != null) {
                        // 如果actualMethod不是null，这段代码会构建一个字符串，形式类似于someMethod("someValue")，
                        // 其中someMethod是item.getName()的结果，而someValue是经过转义的value。
                        // 这可能是在记录、构建动态代码或进行某种形式的处理中使用。
                        actualMethod.append(item.getName()).append("(\"").append(escape(value)).append("\")");
                    }
                    return true;
                }
            }
            /* 第二步、找 setFoo( int ) or ( boolean ) 类型的方法 */
            // Try a setFoo ( int ) or ( boolean )
            for (Method method : methods) {
                boolean ok = true;
                if (setter.equals(method.getName())
                        && method.getParameterTypes().length == 1) {

                    // match - find the type and invoke it
                    Class<?> paramType = method.getParameterTypes()[0];
                    Object params[] = new Object[1];

                    // Try a setFoo ( int )
                    if ("java.lang.Integer".equals(paramType.getName())
                            || "int".equals(paramType.getName())) {
                        try {
                            params[0] = Integer.valueOf(value);
                        } catch (NumberFormatException ex) {
                            ok = false;
                        }
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(Integer.valueOf(\"").append(value).append("\"))");
                        }
                        // Try a setFoo ( long )
                    } else if ("java.lang.Long".equals(paramType.getName())
                            || "long".equals(paramType.getName())) {
                        try {
                            params[0] = Long.valueOf(value);
                        } catch (NumberFormatException ex) {
                            ok = false;
                        }
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(Long.valueOf(\"").append(value).append("\"))");
                        }
                        // Try a setFoo ( boolean )
                    } else if ("java.lang.Boolean".equals(paramType.getName())
                            || "boolean".equals(paramType.getName())) {
                        params[0] = Boolean.valueOf(value);
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(Boolean.valueOf(\"").append(value).append("\"))");
                        }
                        // Try a setFoo ( InetAddress )
                    } else if ("java.net.InetAddress".equals(paramType
                            .getName())) {
                        try {
                            params[0] = InetAddress.getByName(value);
                        } catch (UnknownHostException exc) {
                            if (log.isDebugEnabled()) {
                                log.debug("IntrospectionUtils: Unable to resolve host name:" + value);
                            }
                            ok = false;
                        }
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(InetAddress.getByName(\"").append(value).append("\"))");
                        }
                        // Unknown type
                    } else {
                        // 如果开启调试模式了 ---> logger.isLoggable(Level.FINE);
                        if (log.isDebugEnabled()) {
                            // 就打印日志内容
                            log.debug("IntrospectionUtils: Unknown type " +
                                    paramType.getName());
                        }
                    }

                    if (ok) {
                        method.invoke(o, params);
                        return true;
                    }
                }
                /* 第三步、找 setProperty 方法 */
                // save "setProperty" for later（如果有 setProperty 方法的话）
                if ("setProperty".equals(method.getName())) {
                    if (method.getReturnType() == Boolean.TYPE) {
                        setPropertyMethodBool = method;// 保存到 布尔值类型
                    } else {
                        setPropertyMethodVoid = method;// 保存到 无返回值类型
                    }

                }
            }
            /* 第四步、找 setProperty("name", "value") 类型的方法 */
            // Ok, no setXXX found, try a setProperty("name", "value")
            // invokeSetProperty 的值上面传的是 true
            if (invokeSetProperty && (setPropertyMethodBool != null ||
                    setPropertyMethodVoid != null)) {
                if (actualMethod != null) {
                    actualMethod.append("setProperty(\"").append(name).append("\", \"").append(escape(value)).append("\")");
                }
                Object params[] = new Object[2];
                params[0] = name;
                params[1] = value;
                if (setPropertyMethodBool != null) {
                    try {
                        return ((Boolean) setPropertyMethodBool.invoke(o,
                                params)).booleanValue();
                    }catch (IllegalArgumentException biae) {
                        //the boolean method had the wrong
                        //parameter types. lets try the other
                        if (setPropertyMethodVoid!=null) {
                            setPropertyMethodVoid.invoke(o, params);
                            return true;
                        }else {
                            throw biae;
                        }
                    }
                } else {
                    setPropertyMethodVoid.invoke(o, params);
                    return true;
                }
            }

        } catch (IllegalArgumentException | SecurityException | IllegalAccessException e) {
            log.warn(sm.getString("introspectionUtils.setPropertyError", name, value, o.getClass()), e);
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            log.warn(sm.getString("introspectionUtils.setPropertyError", name, value, o.getClass()), e);
        }
        return false;
    }


    /**
     * Reverse of Introspector.decapitalize.
     * @param name The name
     * @return the capitalized string
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * @param s
     *            the input string
     * @return escaped string, per Java rule
     *
     * 根据 Java 规则转义字符串
     */
    public static String escape(String s) {

        if (s == null) {
            return "";
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
                b.append('\\').append('"');
            } else if (c == '\\') {
                b.append('\\').append('\\');
            } else if (c == '\n') {
                b.append('\\').append('n');
            } else if (c == '\r') {
                b.append('\\').append('r');
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

    // -------------------- other utils --------------------
//    public static void clear() {
//        objectMethods.clear();
//    }

    private static final Map<Class<?>,Method[]> objectMethods = new ConcurrentHashMap<>();

    public static Method[] findMethods(Class<?> c) {
        /* 先从 map 里面获取类，没有找到就使用反射去获取方法们 */
        Method methods[] = objectMethods.get(c);
        if (methods != null) {
            return methods;
        }

        methods = c.getMethods();
        objectMethods.put(c, methods);
        return methods;
    }
}
