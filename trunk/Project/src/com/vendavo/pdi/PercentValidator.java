package com.vendavo.pdi;

public class PercentValidator implements Validator {
    @Override
    public String execute(final Object field) {
        String result = null;
        if (field != null) {
            if (field instanceof String) {
                String value = (String)field;
                int valueLength = value.length();
                int trimLength = value.trim().length();
                if (trimLength != valueLength) {
                    result = ResourceUtil.getString("PERCENT-CONTAINS-WHITESPACE");
                }
                else {
                    value = value.trim();
                    try {
                        double d = Double.parseDouble(value);
                        if (d < ZERO_DOUBLE || d > ONE_DOUBLE) {
                            result = ResourceUtil.getString("PERCENT-OUT-OF-RANGE");
                        }
                    }
                    catch(NumberFormatException nfe) {
                        result = ResourceUtil.getString("PERCENT-NOT-VALID-NUMBER");
                    }
                }
            }
            else {
                result = ResourceUtil.getString("PERCENT-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("PERCENT-IS-NULL");
        }
        return result;
    }
}
