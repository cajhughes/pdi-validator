package com.vendavo.pdi;

public class QuantityValidator implements Validator {
    private boolean allowNegativeQuantity;

    public QuantityValidator(final boolean allowNegativeQuantity) {
        this.allowNegativeQuantity = allowNegativeQuantity;
    }
    @Override
    public String execute(Object field) {
        String result = null;
        if (field != null) {
            if (field instanceof String) {
                String value = (String)field;
                int valueLength = value.length();
                int trimLength = value.trim().length();
                if (trimLength != valueLength) {
                    result = ResourceUtil.getString("QUANTITY-CONTAINS-WHITESPACE");
                }
                else {
                    value = value.trim();
                    try {
                        int i = Integer.parseInt(value);
                        if (i < ZERO_INT && !allowNegativeQuantity) {
                            result = ResourceUtil.getString("QUANTITY-NEGATIVE-VALUE");
                        }
                    }
                    catch(NumberFormatException nfe) {
                        result = ResourceUtil.getString("QUANTITY-NOT-VALID-INTEGER");
                    }
                }
            }
            else {
                result = ResourceUtil.getString("QUANTITY-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("QUANTITY-IS-NULL");
        }
        return result;
    }
}
