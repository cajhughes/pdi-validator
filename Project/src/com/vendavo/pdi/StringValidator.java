package com.vendavo.pdi;

public class StringValidator implements Validator {
    private static final String POUND = "#";

    @Override
    public String execute(Object field) {
        String result = null;
        if (field != null) {
            if (field instanceof String) {
                String value = (String)field;
                int valueLength = value.length();
                int trimLength = value.trim().length();
                if (trimLength != valueLength) {
                    result = ResourceUtil.getString("STRING-CONTAINS-WHITESPACE");
                }
                int pound = value.indexOf(POUND);
                if (pound >= 0) {
                    result = ResourceUtil.getString("STRING-CONTAINS-POUND");
                }
            }
            else {
                result = ResourceUtil.getString("STRING-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("STRING-IS-NULL");
        }
        return result;
    }
}
