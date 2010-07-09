package com.vendavo.pdi;

import java.text.MessageFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateValidator implements Validator {
    private String datePattern = null;
    private static SimpleDateFormat format = null;

    public DateValidator(final String datePattern) {
        this.datePattern = datePattern;
        if (format == null) {
            format = new SimpleDateFormat(datePattern);
        }
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
                    result = ResourceUtil.getString("DATE-CONTAINS-WHITESPACE");
                }
                else {
                    value = value.trim();
                    if (value.length() != datePattern.length()) {
                        result = MessageFormat.format(ResourceUtil.getString("DATE-NOT-VALID-FORMAT"), datePattern);
                    }
                    else {
                        ParsePosition pos = new ParsePosition(0);
                        format.setLenient(false);
                        format.parse(value, pos);
                        if (pos.getIndex() != trimLength) {
                            result = ResourceUtil.getString("DATE-NOT-VALID-DATE");
                        }
                    }
                }
            }
            else {
                result = ResourceUtil.getString("DATE-NOT-STRING");
            }
        }
        else {
            result = ResourceUtil.getString("DATE-IS-NULL");
        }
        return result;
    }
}
