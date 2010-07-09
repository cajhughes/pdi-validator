package com.vendavo.pdi;

public class AdderValidator implements Validator {
    @Override
    public String execute(final Object field) {
        String result = null;
        if (field != null) {
            if (field instanceof String) {
                String value = (String)field;
                int valueLength = value.length();
                int trimLength = value.trim().length();
                if (trimLength != valueLength) {
                    result = ResourceUtil.getString("ADDER-CONTAINS-WHITESPACE");
                }
                else {
                    value = value.trim();
                    try {
                        double d = Double.parseDouble(value);
                        if (d < ZERO_DOUBLE) {
                            result = ResourceUtil.getString("ADDER-NEGATIVE-VALUE");
                        }
                    }
                    catch(NumberFormatException nfe) {
                        result = ResourceUtil.getString("ADDER-NOT-VALID-NUMBER");
                    }
                }
            }
            else {
                result = ResourceUtil.getString("ADDER-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("ADDER-IS-NULL");
        }
        return result;
    }
}
