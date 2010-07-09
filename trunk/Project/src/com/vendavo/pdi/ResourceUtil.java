package com.vendavo.pdi;

import java.util.ResourceBundle;

public final class ResourceUtil {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.vendavo.pdi.Resource");

    public static String getString(final String key) {
        return bundle.getString(key);
    }
}