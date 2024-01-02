package org.apache.tomcat.util.res;

import java.text.MessageFormat;
import java.util.*;

public class StringManager {

    private static final Map<String, Map<Locale, StringManager>> managers = new HashMap<>();
    private static int LOCALE_CACHE_SIZE = 10;
    private final ResourceBundle bundle;
    private final Locale locale;

    private StringManager(String packageName, Locale locale) {
        String bundleName = packageName + ".LocalStrings";
        ResourceBundle bnd = null;
        try {
            // The ROOT Locale uses English. If English is requested, force the
            // use of the ROOT Locale else incorrect results may be obtained if
            // the system default locale is not English and translations are
            // available for the system default locale.
            /*
            1. 翻译：
            根区域设置使用英语。如果要求使用英语，则强制使用根语言环境，否则如果系统默认语言环境不是英语，
            并且系统默认语言环境的翻译是可用的，可能会得到不正确的结果。
            2. 解释：
            这部分代码检查传入的 Locale 是否为英语。如果是，则将 Locale 设置为 Locale.ROOT。
            这样做的原因是在国际化应用中，通常使用 Locale.ROOT 作为默认或备选的语言环境，通常是英语。
            在此场景下，如果请求的语言是英语，则使用 Locale.ROOT 来确保始终获取英语资源，即使系统默认语言不是英语。
             */
            if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
//                System.out.println(locale.getLanguage());// locale: en_CN，但打印结果是 en
//                System.out.println(Locale.ENGLISH.getLanguage());// 结果是 en
                locale = Locale.ROOT;
            }
            // 这行代码尝试加载与指定 Locale 匹配的资源捆绑包（ResourceBundle）。资源包内包含了特定区域设置的本地化字符串。
            bnd = ResourceBundle.getBundle(bundleName, locale);
        } catch (MissingResourceException ex) {
            // Try from the current loader (that's the case for trusted apps)
            // Should only be required if using a TC5 style classloader structure
            // where common != shared != server
            /*
            1. 翻译：
            只有在使用TC5风格的类加载器结构(common != shared != server)时，才需要从当前加载器(即可信应用程序)中尝试
            2. 解释：
            这里获取当前线程的上下文类加载器。这是因为在某些环境下（如Java EE应用服务器），应用的类可能不是由系统类加载器加载的。
             使用上下文类加载器可以确保能够访问到应用特定的资源
             */

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                try {
                    // 如果上下文类加载器存在，则尝试使用该类加载器来加载资源包。这通常在标准的类加载器无法找到资源束时使用，如在一些复杂的类加载器结构中。
                    bnd = ResourceBundle.getBundle(bundleName, locale, cl);
                } catch (MissingResourceException ex2) {
                    // Ignore
                }
            }
        }
        bundle = bnd;
        // Get the actual locale, which may be different from the requested one
        /*
        1. 翻译：
        获取实际的区域设置，它可能与请求的不同
        2. 解释：
        这部分代码用于确定最终使用的 Locale。如果能够找到资源包，则使用资源包内的 Locale；
        如果资源包不存在，则将 this.locale 设置为 null。这主要是为了处理资源包可能与请求的 Locale（语言环境） 不完全匹配的情况。

         */
        if (bundle != null) {
            Locale bundleLocale = bundle.getLocale();
            if (bundleLocale.equals(Locale.ROOT)) {
                this.locale = Locale.ENGLISH;
            } else {
                this.locale = bundleLocale;
            }
        } else {
            this.locale = null;
        }
//        throw new UnsupportedOperationException();
    }

    public static final StringManager getManager(Class<?> clazz) {
        return getManager(clazz.getPackage().getName());
    }

    public static final StringManager getManager(String packageName) {
        return getManager(packageName, Locale.getDefault());
    }

    // todo packageName 就是 Map<String, Map<Locale, StringManager>> 中的 key 值（即 String）
    public static final synchronized StringManager getManager(String packageName, Locale locale) {
        Map<Locale, StringManager> map = managers.get(packageName);
        if (map == null) {
            /*
             * Don't want the HashMap size to exceed LOCALE_CACHE_SIZE. Expansion occurs when size() exceeds capacity.
             * Therefore keep size at or below capacity. removeEldestEntry() executes after insertion therefore the test
             * for removal needs to use one less than the maximum desired size. Note this is an LRU cache.
             */
            map = new LinkedHashMap<>(LOCALE_CACHE_SIZE, 0.75f, true) {
                private static final long serialVersionUID = 1L;

                // 看上面英文
                @Override
                protected boolean removeEldestEntry(Map.Entry<Locale, StringManager> eldest) {
                    if (size() > (LOCALE_CACHE_SIZE - 1)) {
                        return true;
                    }
                    return false;
                }
            };
            // 将 packageName 包名和 Map<Locale, StringManager> 类型的 map 放入到
            // Map<String, Map<Locale, StringManager>> 类型的 map 中（managers）
            managers.put(packageName, map);
        }
        // 下面的这个 map 的类型是 Map<Locale, StringManager>
        StringManager mgr = map.get(locale);
        if (mgr == null) {
            mgr = new StringManager(packageName, locale);
            map.put(locale, mgr);
        }
        return mgr;
//        throw new UnsupportedOperationException();
    }


    /**
     * Get a string from the underlying resource bundle or return null if the String is not found.
     *
     * @param key to desired resource String
     *
     * @return resource String matching <i>key</i> from underlying bundle or null if not found.
     *
     * @throws IllegalArgumentException if <i>key</i> is null
     */
    public String getString(String key) {
        if (key == null) {
            String msg = "key may not have a null value";
            throw new IllegalArgumentException(msg);
        }

        String str = null;

        try {
            // Avoid NPE if bundle is null and treat it like an MRE
            if (bundle != null) {
                str = bundle.getString(key);
            }
        } catch (MissingResourceException mre) {
            // bad: shouldn't mask an exception the following way:
            // str = "[cannot find message associated with key '" + key +
            // "' due to " + mre + "]";
            // because it hides the fact that the String was missing
            // from the calling code.
            // good: could just throw the exception (or wrap it in another)
            // but that would probably cause much havoc on existing
            // code.
            // better: consistent with container pattern to
            // simply return null. Calling code can then do
            // a null check.
            /*
            这段注释解释了为什么在捕获MissingResourceException后返回null
            而不是抛出异常或返回错误消息的原因
            不好的做法（bad）：作者指出，掩盖异常（通过设置str为一条错误消息）并不是一个好的做法，因为这隐藏了真正缺失字符串的事实。
            可能的做法（good）：一种可能的处理方式是直接抛出异常或将它包装在另一个异常中，但这可能会对现有代码造成很大的影响。
            更好的做法（better）：作者建议的最佳做法是保持与“容器模式”一致，简单地返回null。这样，调用代码可以通过检查返回值是否为null来判断资源是否存在。
            */
            str = null;
        }

        return str;
    }

    /**
     * Get a string from the underlying resource bundle and format it with the given set of arguments.
     *
     * @param key  The key for the required message
     * @param args The values to insert into the message
     *
     * @return The request string formatted with the provided arguments or the key if the key was not found.
     */
    public String getString(final String key, final Object... args) {
        // 这个 key 暂时是：coyoteConnector.protocolHandlerInstantiationFailed
        String value = getString(key);
        if (value == null) {
            value = key;
        }

        MessageFormat mf = new MessageFormat(value);
        mf.setLocale(locale);
        return mf.format(args, new StringBuffer(), null).toString();
//        throw new UnsupportedOperationException();
    }
}
