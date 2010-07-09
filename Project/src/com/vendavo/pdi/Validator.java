package com.vendavo.pdi;

/**
 * Defines the interface that all validators implement for their specific type.
 */
public interface Validator {
    public static final double ZERO_DOUBLE = 0.0d;
    public static final double ONE_DOUBLE = 1.0d;
    public static final int ZERO_INT = 0;

    public String execute(final Object field);
}
