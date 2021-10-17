package org.a05annex.util.geo3d;

public class PackageConstants {
    /**
     * The maximum positive <code>double</code> value that is considered to be 0.0 for graphics.
     */
    public static final double ZERO_TOLERANCE_MAX_DOUBLE = 1.0e-35;
    /**
     * The minimum negative <code>double</code> value that is considered to be 0.0 for graphics.
     */
    public static final double ZERO_TOLERANCE_MIN_DOUBLE = -ZERO_TOLERANCE_MAX_DOUBLE;

    /**
     * Test whether a <code>double</code> value should be considered to be 0.0 for graphics.
     *
     * @param dVal (double) The value to be tested.
     * @return <code>true</code> if the value should be considered to be 0.0, <code>false</code> otherwise.
     */
    public static boolean isZero(final double dVal) {
        return (dVal < ZERO_TOLERANCE_MAX_DOUBLE) && (dVal > ZERO_TOLERANCE_MIN_DOUBLE);
    }

}
