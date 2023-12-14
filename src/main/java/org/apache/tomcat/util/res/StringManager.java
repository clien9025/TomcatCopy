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

    public String getString(final String key, final Object... args) {
//        String value = getString(key);
//        if (value == null) {
//            value = key;
//        }
//
//        MessageFormat mf = new MessageFormat(value);
//        mf.setLocale(locale);
//        return mf.format(args, new StringBuffer(), null).toString();
        throw new UnsupportedOperationException();
    }
}
