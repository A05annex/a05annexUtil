package org.a05annex.util;

/**
 * This is a utility class with some commonly used math constants and utility methods
 */
public final class Utl {

    /**
     * Set to the value of {@link java.lang.Math#PI}, or {@code 180}&deg;, for convenience only - we are
     * already including all of the constants from this class.
     */
    public static final double PI = Math.PI;
    /**
     * Set to the value of {@code -}{@link java.lang.Math#PI}, or {@code 180}&deg;, because we use this a
     * lot in swerve drive code.
     */
    public static final double NEG_PI = -Math.PI;
    /**
     * Set to the value of {@code (}{@link java.lang.Math#PI}{@code  * 2,0)}, or {@code 360}&deg;, because we use
     * this a lot in swerve drive code.
     */
    public static final double TWO_PI = Math.PI * 2.0;
    /**
     * Set to the value of {@code (}{@link java.lang.Math#PI}{@code  * 0.5)}, or {@code 90}&deg;, because we use
     * this a lot in swerve drive code.
     */
    public static final double PI_OVER_2 = Math.PI * 0.5;
    /**
     * Set to the value of {@code -(}{@link java.lang.Math#PI}{@code  * 0.5)}, or {@code -90}&deg;, because
     * we use this a lot in swerve drive code.
     */
    public static final double NEG_PI_OVER_2 = -(Math.PI * 0.5);

    /**
     * This class is all static constants and methods, it cannot be instantiated.
     */
    private Utl() {
    }

    /**
     * Get the length of an n-dimensional set of lengths.
     *
     * @param values The dimensional lengths. The number of dimensional lengths is variable and may be zero.
     * @return The n-dimensional length, 0.0 of no dimensional lengths are specified.
     */
    public static double length(double... values) {
        double lengthSquared = 0.0;
        for (double v : values) {
            lengthSquared += v * v;
        }
        return Math.sqrt(lengthSquared);
    }

    /**
     * Get the maximum value of an arbitrary number of values.
     *
     * @param values The values. The number of values is variable and may be zero.
     * @return The maximum value, {@link Double#NEGATIVE_INFINITY} if no values are specified.
     */
    public static double max(double... values) {
        double max = Double.NEGATIVE_INFINITY;
        for (double v : values) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    /**
     * Get the minimum value of an arbitrary number of values.
     *
     * @param values The values. The number of values is variable and may be zero.
     * @return The minimum value, {@link Double#POSITIVE_INFINITY} if no values are specified.
     */
    public static double min(double... values) {
        double min = Double.POSITIVE_INFINITY;
        for (double v : values) {
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

}
