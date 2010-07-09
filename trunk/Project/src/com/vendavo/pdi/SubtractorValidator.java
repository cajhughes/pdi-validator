package com.vendavo.pdi;

public class SubtractorValidator implements Validator {
    @Override
    public String execute(Object field) {
        String result = null;
        if (field != null) {
            if (field instanceof String) {
                String value = (String)field;
                int valueLength = value.length();
                int trimLength = value.trim().length();
                if (trimLength != valueLength) {
                    result = ResourceUtil.getString("SUBTRACTOR-CONTAINS-WHITESPACE");
                }
                else {
                    value = value.trim();
                    try {
                        double d = Double.parseDouble(value);
                        if (d > ZERO_DOUBLE) {
                            result = ResourceUtil.getString("SUBTRACTOR-POSITIVE-VALUE");
                        }
                    }
                    catch(NumberFormatException nfe) {
                        result = ResourceUtil.getString("SUBTRACTOR-NOT-VALID-NUMBER");
                    }
                }
            }
            else {
                result = ResourceUtil.getString("SUBTRACTOR-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("SUBTRACTOR-IS-NULL");
        }
        return result;
    }
}
