package org.apache.tomcat.util.res;

import java.text.MessageFormat;
import java.util.*;

public class StringManager {

    private static final Map<String, Map<Locale, StringManager>> managers = new HashMap<>();
    private static int LOCALE_CACHE_SIZE = 10;
    private final ResourceBundle bundle;
    private final Locale locale;
    private StringManager(String packageName, Locale locale) {
//        String bundleName = packageName + ".LocalStrings";
//        ResourceBundle bnd = null;
//        try {
//            // The ROOT Locale uses English. If English is requested, force the
//            // use of the ROOT Locale else incorrect results may be obtained if
//            // the system default locale is not English and translations are
//            // available for the system default locale.
//            if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
//                locale = Locale.ROOT;
//            }
//            bnd = ResourceBundle.getBundle(bundleName, locale);
//        } catch (MissingResourceException ex) {
//            // Try from the current loader (that's the case for trusted apps)
//            // Should only be required if using a TC5 style classloader structure
//            // where common != shared != server
//            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            if (cl != null) {
//                try {
//                    bnd = ResourceBundle.getBundle(bundleName, locale, cl);
//                } catch (MissingResourceException ex2) {
//                    // Ignore
//                }
//            }
//        }
//        bundle = bnd;
//        // Get the actual locale, which may be different from the requested one
//        if (bundle != null) {
//            Locale bundleLocale = bundle.getLocale();
//            if (bundleLocale.equals(Locale.ROOT)) {
//                this.locale = Locale.ENGLISH;
//            } else {
//                this.locale = bundleLocale;
//            }
//        } else {
//            this.locale = null;
//        }
        throw new UnsupportedOperationException();
    }

    public static final StringManager getManager(Class<?> clazz) {
        return getManager(clazz.getPackage().getName());
    }

    public static final StringManager getManager(String packageName) {
        return getManager(packageName, Locale.getDefault());
    }

    public static final synchronized StringManager getManager(String packageName, Locale locale) {
//        Map<Locale, StringManager> map = managers.get(packageName);
//        if (map == null) {
//            /*
//             * Don't want the HashMap size to exceed LOCALE_CACHE_SIZE. Expansion occurs when size() exceeds capacity.
//             * Therefore keep size at or below capacity. removeEldestEntry() executes after insertion therefore the test
//             * for removal needs to use one less than the maximum desired size. Note this is an LRU cache.
//             */
//            map = new LinkedHashMap<>(LOCALE_CACHE_SIZE, 0.75f, true) {
//                private static final long serialVersionUID = 1L;
//
//                @Override
//                protected boolean removeEldestEntry(Map.Entry<Locale, StringManager> eldest) {
//                    if (size() > (LOCALE_CACHE_SIZE - 1)) {
//                        return true;
//                    }
//                    return false;
//                }
//            };
//            managers.put(packageName, map);
//        }
//
//        StringManager mgr = map.get(locale);
//        if (mgr == null) {
//            mgr = new StringManager(packageName, locale);
//            map.put(locale, mgr);
//        }
//        return mgr;
        throw new UnsupportedOperationException();
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
