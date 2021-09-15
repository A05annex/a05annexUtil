package org.a05annex.util.geo3d;

public class PackageConstants {
    /**
     * The maximum positive <tt>double</tt> value that is considered to be 0.0 for graphics.
     */
    public static final double ZERO_TOLERANCE_MAX_DOUBLE = 1.0e-35;
    /**
     * The minimum negative <tt>double</tt> value that is considered to be 0.0 for graphics.
     */
    public static final double ZERO_TOLERANCE_MIN_DOUBLE = -ZERO_TOLERANCE_MAX_DOUBLE;

    /**
     * Test whether a <tt>double</tt> value should be considered to be 0.0 for graphics.
     *
     * @param dVal (double) The value to be tested.
     * @return <tt>true</tt> if the value should be considered to be 0.0, <tt>false</tt> otherwise.
     */
    public static boolean isZero(final double dVal) {
        return (dVal < ZERO_TOLERANCE_MAX_DOUBLE) && (dVal > ZERO_TOLERANCE_MIN_DOUBLE);
    }

}
