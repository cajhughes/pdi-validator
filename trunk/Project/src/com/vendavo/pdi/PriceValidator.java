package com.vendavo.pdi;

import java.math.BigDecimal;

public class PriceValidator implements Validator {
    private int precision;
    private int scale;

    public PriceValidator(final int precision, final int scale) {
        this.precision = precision;
        this.scale = scale;
    }
    @Override
    public String execute(final Object field) {
        String result = null;
        if (field != null) {
            if (field instanceof String) {
                String value = (String)field;
                int valueLength = value.length();
                int trimLength = value.trim().length();
                if (trimLength != valueLength) {
                    result = ResourceUtil.getString("PRICE-CONTAINS-WHITESPACE");
                }
                else {
                    try {
                        BigDecimal price = new BigDecimal(value);
                        if (price.compareTo(BigDecimal.ZERO) < 0) {
                            result = ResourceUtil.getString("PRICE-NEGATIVE-VALUE");
                        }
                        else {
                            if (price.scale() > scale) {
                                result = ResourceUtil.getString("PRICE-PRECISION-SCALE-ERROR");
                            }
                            if (price.precision() > precision) {
                                result = ResourceUtil.getString("PRICE-PRECISION-SCALE-ERROR");
                            }
                        }
                    }
                    catch(NumberFormatException nfe) {
                        result = ResourceUtil.getString("PRICE-NOT-VALID-NUMBER");
                    }
                }
            }
            else {
                result = ResourceUtil.getString("PRICE-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("PRICE-IS-NULL");
        }
        return result;
    }
}
