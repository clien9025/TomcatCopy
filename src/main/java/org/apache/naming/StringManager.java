package org.apache.naming;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class StringManager {

//    /**
//     * The ResourceBundle for this StringManager.
//     */
//    private final ResourceBundle bundle;
//    private final Locale locale;

    // 暂时添加
    public static final StringManager getManager(Class<?> clazz) {
//        return getManager(clazz.getPackage().getName());
        throw new UnsupportedOperationException();
    }


    /**
     * Get a string from the underlying resource bundle and format
     * it with the given set of arguments.
     *
     * @param key  The key for the required message
     * @param args The values to insert into the message
     *
     * @return The request string formatted with the provided arguments or the
     *         key if the key was not found.
     */
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
