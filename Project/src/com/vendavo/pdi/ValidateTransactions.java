package com.vendavo.pdi;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;

public class ValidateTransactions {
    public static final String DEFAULT_DATE_PATTERN = "dd-Mon-yy";
    public static final int DEFAULT_PRECISION = 10;
    public static final int DEFAULT_SCALE = 2;
    public static final String DELIMITER = " - ";
    public static final String SEPARATOR = ": ";

    public static final String ADDER = "-Adder";
    public static final String DATE = "-Date";
    public static final String PERCENT = "-Percent";
    public static final String PRICE = "-Price";
    public static final String QUANTITY = "-Quantity";
    public static final String SUBTRACTOR = "-Subtractor";

    /**
     * Validates the data in the incoming Object[] based on the names of the fields
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @return String that contains the error, if any is observed, or null
     */
    public static String checkTypesByName(final RowMetaInterface meta,
                                          final Object[] fields) {
        return checkTypesByName(meta, fields, DEFAULT_DATE_PATTERN);
    }

    /**
     * Validates the data in the incoming Object[] based on the names of the fields
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @param datePattern - the pattern to be matched against when validating -Date fields
     * @return String that contains the error, if any is observed, or null
     */
    public static String checkTypesByName(final RowMetaInterface meta,
                                          final Object[] fields,
                                          final String datePattern) {
        return checkTypesByName(meta, fields, datePattern, DEFAULT_PRECISION, DEFAULT_SCALE);
    }

    /**
     * Validates the data in the incoming Object[] based on the names of the fields
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @param datePattern - the pattern to be matched against when validating -Date fields
     * @param precision - the allowed precision when validating -Price fields
     * @param scale - the allowed scale when validating -Price fields
     * @return String that contains the error, if any is observed, or null
     */
    public static String checkTypesByName(final RowMetaInterface meta,
                                          final Object[] fields,
                                          final String datePattern,
                                          final int precision,
                                          final int scale) {
        return checkTypesByName(meta, fields, datePattern, precision, scale, false);
    }

    /**
     * Validates the data in the incoming Object[] based on the names of the fields
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @param datePattern - the pattern to be matched against when validating -Date fields
     * @param precision - the allowed precision when validating -Price fields
     * @param scale - the allowed scale when validating -Price fields
     * @param allowNegativeQuantity - specifies whether negative quantities are permitted
     * @return String that contains the error, if any is observed, or null
     */
    public static String checkTypesByName(final RowMetaInterface meta,
                                          final Object[] fields,
                                          final String datePattern,
                                          final int precision,
                                          final int scale,
                                          final boolean allowNegativeQuantity) {
        String result = null;
        StringBuilder errors = new StringBuilder();
        int metaSize = meta.size();
        int arraySize = fields.length;
        if (metaSize != arraySize) {
            errors.append(ResourceUtil.getString("METADATA-DATA-MISMATCH"));
        }
        else {
            for (int i = 0; i < metaSize; i++) {
                ValueMetaInterface valueMeta = meta.getValueMeta(i);
                String fieldName = valueMeta.getName();
                if (fieldName != null) {
                    Validator validator = null;
                    if (fieldName.endsWith(ADDER)) {
                        validator = new AdderValidator();
                    }
                    else if (fieldName.endsWith(DATE)) {
                        validator = new DateValidator(datePattern);
                    }
                    else if (fieldName.endsWith(PERCENT)) {
                        validator = new PercentValidator();
                    }
                    else if (fieldName.endsWith(PRICE)) {
                        validator = new PriceValidator(precision, scale);
                    }
                    else if (fieldName.endsWith(QUANTITY)) {
                        validator = new QuantityValidator(allowNegativeQuantity);
                    }
                    else if (fieldName.endsWith(SUBTRACTOR)) {
                        validator = new SubtractorValidator();
                    }
                    else {
                        validator = new StringValidator();
                    }
                    if (validator != null) {
                        String error = validator.execute(fields[i]);
                        if (error != null) {
                            appendError(errors, fieldName, error);
                        }
                    }
                }
            }
        }
        if (errors.length() > 0) {
            result = errors.toString();
        }
        return result;
    }

    /**
     * Validates that the data in the field whose name is highPrice contains a numeric value greater than
     * the data in the field whose name is lowPrice.
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @param highPrice - the name of the field which contains a numeric value that should be higher
     * @param lowPrice - the name of the field which contains a numeric value that should be lower
     * @return String that contains the error, if any is observed, or null
     */
    public static String isGreater(final RowMetaInterface meta, final Object[] fields, final String highPrice,
                                   final String lowPrice) {
        String result = null;
        StringBuilder errors = new StringBuilder();
        int metaSize = meta.size();
        int arraySize = fields.length;
        BigDecimal high = BigDecimal.ZERO;
        BigDecimal low = BigDecimal.ZERO;
        if (metaSize != arraySize) {
            errors.append(ResourceUtil.getString("METADATA-DATA-MISMATCH"));
        }
        else {
            String highFieldName = null;
            String lowFieldName = null;
            for (int i = 0; i < metaSize; i++) {
                ValueMetaInterface valueMeta = meta.getValueMeta(i);
                String fieldName = valueMeta.getName();
                String value = (String)fields[i];
                if (fieldName != null) {
                    if (fieldName.equalsIgnoreCase(highPrice)) {
                        highFieldName = fieldName;
                        if (value != null) {
                            if (isBigDecimal(value)) {
                                high = getBigDecimal(value);
                            }
                            else {
                                appendError(errors, fieldName, ResourceUtil.getString("PRICE-NOT-VALID-NUMBER"));
                            }
                        }
                    }
                    if (fieldName.equalsIgnoreCase(lowPrice)) {
                        lowFieldName = fieldName;
                        if (value != null) {
                            if (isBigDecimal(value)) {
                                low = getBigDecimal(value);
                            }
                            else {
                                appendError(errors, fieldName, ResourceUtil.getString("PRICE-NOT-VALID-NUMBER"));
                            }
                        }
                    }
                }
            }
            if (errors.length() == 0) {
                if (high.compareTo(low) < 0) {
                    Object[] lowField = {lowFieldName};
                    appendError(errors, highFieldName,
                                MessageFormat.format(ResourceUtil.getString("PRICE-HIGH-LESS-THAN-LOW"), lowField));
                }
            }
        }
        if (errors.length() > 0) {
            result = errors.toString();
        }
        return result;
    }

    /**
     * Validates that the data in the field whose name is highPrice contains a numeric value greater than
     * the data in the field whose name is lowPrice.
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @param multipliedPrice - the name of the field which contains a numeric value that equals the multipled values
     * @param multiplyFields - the names of the fields which, when multiplied together, equal the multipliedPrice
     * @return String that contains the error, if any is observed, or null
     */
    public static String isMultiplied(final RowMetaInterface meta, final Object[] fields, final String multipliedPrice,
                                      final String[] multiplyFields) {
        String result = null;
        StringBuilder errors = new StringBuilder();
        int metaSize = meta.size();
        int arraySize = fields.length;
        BigDecimal multiplyTarget = BigDecimal.ZERO;
        List<BigDecimal> multipliers = new ArrayList<BigDecimal>();
        if (metaSize != arraySize) {
            errors.append(ResourceUtil.getString("METADATA-DATA-MISMATCH"));
        }
        else {
            String multiplyFieldName = null;
            for (int i = 0; i < metaSize; i++) {
                ValueMetaInterface valueMeta = meta.getValueMeta(i);
                String fieldName = valueMeta.getName();
                String value = (String)fields[i];
                if (fieldName != null) {
                    if (fieldName.equalsIgnoreCase(multipliedPrice)) {
                        multiplyFieldName = fieldName;
                        if (value != null) {
                            if (isBigDecimal(value)) {
                                multiplyTarget = getBigDecimal(value);
                            }
                            else {
                                appendError(errors, fieldName, ResourceUtil.getString("PRICE-NOT-VALID-NUMBER"));
                            }
                        }
                    }
                    if (value != null) {
                        for (String multiplyField : multiplyFields) {
                            if (fieldName.equalsIgnoreCase(multiplyField)) {
                                if (isBigDecimal(value)) {
                                    multipliers.add(getBigDecimal(value));
                                }
                                else {
                                    appendError(errors, fieldName, ResourceUtil.getString("PRICE-NOT-VALID-NUMBER"));
                                }
                            }
                        }
                    }
                }
            }
            if (errors.length() == 0) {
                BigDecimal runningTotal = BigDecimal.ONE;
                for (BigDecimal multiplier : multipliers) {
                    runningTotal.multiply(multiplier);
                }
                if (multiplyTarget.compareTo(runningTotal) != 0) {
                    appendError(errors, multiplyFieldName, ResourceUtil.getString("PRICES-DO-NOT-MULTIPLY"));
                }
            }
        }
        if (errors.length() > 0) {
            result = errors.toString();
        }
        return result;
    }

    /**
     * Validates that the data in the field whose name is highPrice contains a numeric value greater than
     * the data in the field whose name is lowPrice.
     *
     * @param meta - the metadata for the incoming Object[]
     * @param fields - the Object[] to be validated
     * @param sumPrice - the name of the field which contains a numeric value that equals the summed values
     * @param sumFields - the names of the fields which, when summed together, equal the sumPrice
     * @return String that contains the error, if any is observed, or null
     */
    public static String isSum(final RowMetaInterface meta, final Object[] fields, final String sumPrice,
                               final String[] sumFields) {
        String result = null;
        StringBuilder errors = new StringBuilder();
        int metaSize = meta.size();
        int arraySize = fields.length;
        BigDecimal sumTarget = BigDecimal.ZERO;
        BigDecimal runningTotal = BigDecimal.ZERO;
        if (metaSize != arraySize) {
            errors.append(ResourceUtil.getString("METADATA-DATA-MISMATCH"));
        }
        else {
            String sumFieldName = null;
            for (int i = 0; i < metaSize; i++) {
                ValueMetaInterface valueMeta = meta.getValueMeta(i);
                String fieldName = valueMeta.getName();
                String value = (String)fields[i];
                if (fieldName != null) {
                    if (fieldName.equalsIgnoreCase(sumPrice)) {
                        sumFieldName = fieldName;
                        if (value != null) {
                            if (isBigDecimal(value)) {
                                sumTarget = getBigDecimal(value);
                            }
                            else {
                                appendError(errors, fieldName, ResourceUtil.getString("PRICE-NOT-VALID-NUMBER"));
                            }
                        }
                    }
                    if (value != null) {
                        for (String sumField : sumFields) {
                            if (fieldName.equalsIgnoreCase(sumField)) {
                                if (isBigDecimal(value)) {
                                    runningTotal = runningTotal.add(getBigDecimal(value));
                                }
                                else {
                                    appendError(errors, fieldName, ResourceUtil.getString("PRICE-NOT-VALID-NUMBER"));
                                }
                            }
                        }
                    }
                }
            }
            if (errors.length() == 0) {
                if (sumTarget.compareTo(runningTotal) != 0) {
                    appendError(errors, sumFieldName, ResourceUtil.getString("PRICES-DO-NOT-SUM"));
                }
            }
        }
        if (errors.length() > 0) {
            result = errors.toString();
        }
        return result;
    }

    /**
     * Appends the fieldName and error to the StringBuilder
     *
     * @param buffer - the StringBuilder to be appended to
     * @param fieldName - the fieldName of the field in which an error was identified
     * @param error - the details of the error identified
     */
    private static void appendError(final StringBuilder buffer, final String fieldName, final String error) {
        if (buffer != null) {
            if (buffer.length() > 1) {
                buffer.append(DELIMITER);
            }
            buffer.append(fieldName);
            buffer.append(SEPARATOR);
            buffer.append(error);
        }
    }

    /**
     * Converts the String value passed in into a BigDecimal
     *
     * @param value - the String to be converted
     * @return BigDecimal that represents the converted value
     */
    private static BigDecimal getBigDecimal(final String value) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        if (value != null) {
            try {
                bigDecimal = new BigDecimal(value);
            }
            catch (NumberFormatException nfe) {
                bigDecimal = BigDecimal.ZERO;
            }
        }
        return bigDecimal;
    }

    /**
     * Analyses the value passed in, to determine if it can be converted into a BigDecimal
     *
     * @param value - the String to be analysed
     * @return boolean to indicate whether the value can be converted
     */
    private static boolean isBigDecimal(final String value) {
        boolean isBigDecimal = false;
        if (value != null) {
            try {
                new BigDecimal(value);
                isBigDecimal = true;
            }
            catch (NumberFormatException nfe) {
                isBigDecimal = false;
            }
        }
        return isBigDecimal;
    }
}
