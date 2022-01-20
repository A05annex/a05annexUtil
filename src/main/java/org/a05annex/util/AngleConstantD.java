package org.a05annex.util;

/**
 * This is a wrapper class for a constant angle. Radians are the default unit for angles, so the internal
 * representation is in radians at double precision. This class represents angles that are constants, i.e. there
 * are no methods to change the value of the angle.
 */
public class AngleConstantD {

    public static final AngleConstantD PI = new AngleConstantD(AngleUnit.RADIANS, Math.PI);
    public static final AngleConstantD TWO_PI = new AngleConstantD(AngleUnit.RADIANS, 2.0 * Math.PI);
    public static final AngleConstantD PI_OVER_2 = new AngleConstantD(AngleUnit.RADIANS, Math.PI / 2.0);
    public static final AngleConstantD NEG_PI = new AngleConstantD(AngleUnit.RADIANS, -Math.PI);
    public static final AngleConstantD NEG_TWO_PI = new AngleConstantD(AngleUnit.RADIANS, -2.0 * Math.PI);
    public static final AngleConstantD NEG_PI_OVER_2 = new AngleConstantD(AngleUnit.RADIANS, -Math.PI / 2.0);

    public static final AngleConstantD DEG_180 = PI;
    public static final AngleConstantD DEG_360 = TWO_PI;
    public static final AngleConstantD DEG_90 = PI_OVER_2;
    public static final AngleConstantD DEG_NEG_180 = NEG_PI;
    public static final AngleConstantD DEG_NEG_360 = NEG_TWO_PI;
    public static final AngleConstantD DEG_NEG_90 = NEG_PI_OVER_2;
    /**
     * The conversion factor for radians to degrees.
     */
    protected static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;

    /**
     * The conversion factor for degrees to radians.
     */
    protected static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

    /**
     * The value of the angle, always in radians.
     */
    protected double radians;

    /**
     * Creates a new instance of <code>AngleD</code>
     */
    public AngleConstantD() {
    }

    /**
     * Instantiate an angle with the specified value.
     *
     * @param type  The unit in which the angle is specified.
     * @param angle The value of the angle in the specified units.
     */
    public AngleConstantD(final AngleUnit type, final double angle) {
        radians = AngleUnit.DEGREES == type ? angle * DEGREES_TO_RADIANS : angle;
    }

    /**
     * Instantiate an angle initialized to the specified angle
     *
     * @param angle The angle to initialize this angle to.
     */
    public AngleConstantD(final AngleConstantD angle) {
        radians = angle.radians;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Get the value of the angle in a meaningful unit.
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the value of the angle in degrees.
     *
     * @return The angle in degrees.
     */
    public double getDegrees() {
        return radians * RADIANS_TO_DEGREES;
    }

    /**
     * Get the value of the angle in radians
     *
     * @return The angle in radians.
     */
    public double getRadians() {
        return radians;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Get one of the trig values for the angle.
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the cosine of the angle. This function delegates to {@link Math#cos(double)}.
     *
     * @return The cosine of the angle.
     */
    public double cos() {
        return Math.cos(radians);
    }

    /**
     * Get the sine of the angle. This function delegates to {@link Math#sin(double)}.
     *
     * @return The sine of the angle.
     */
    public double sin() {
        return Math.sin(radians);
    }

    /**
     * Get the tangent of the angle. This function delegates to {@link Math#tan(double)}.
     *
     * @return The sine of the angle.
     */
    public double tan() {
        return Math.tan(radians);
    }

    //------------------------------------------------------------------------------------------------------------------
    // boolean greater than and less than testing
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Is this angle less than the angle to test against.
     *
     * @param angle The angle to test against.
     * @return <code>true</code> if this angle is less than the angle to test against,
     * <code>false</code> otherwise.
     */
    public boolean isLessThan(AngleConstantD angle) {
        return radians < angle.radians;
    }

    /**
     * Is this angle less than or equal to the angle test against.
     *
     * @param angle The angle to test against.
     * @return <code>true</code> if this angle is less than or equal to  the angle to test against,
     * <code>false</code> otherwise.
     */
    public boolean isLessThanOrEqual(AngleConstantD angle) {
        return radians <= angle.radians;
    }


    /**
     * Is this angle greater than the angle to test against.
     *
     * @param angle The angle to test against.
     * @return <code>true</code> if this angle is greater than the angle to test against,
     * <code>false</code> otherwise.
     */
    public boolean isGreaterThan(AngleConstantD angle) {
        return radians > angle.radians;
    }

    /**
     * Is this angle greater than or equal to the angle test against.
     *
     * @param angle The angle to test against.
     * @return <code>true</code> if this angle is greater than or equal to the angle to test against,
     * <code>false</code> otherwise.
     */
    public boolean isGreaterThanOrEqual(AngleConstantD angle) {
        return radians >= angle.radians;
    }

    //------------------------------------------------------------------------------------------------------------------
    // testing for equals
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Tests whether an angle is exactly equal to this angle.
     * @param angle The angle to test.
     * @return <code>true</code> if this angle is exactly equal in value to the angle to test against,
     *      <code>false</code> otherwise.
     */

    public boolean equals(final AngleConstantD angle) {
        return (this == angle) || ((null != angle) && (radians == angle.radians));
    }

    /**
     * Tests whether an object is exactly equal to this angle.
     * @param obj The object to test
     * @return <code>true</code> if this angle is exactly equal in value to the object to test against,
     *      <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if ((null == obj) || (!AngleConstantD.class.isAssignableFrom(obj.getClass()))) {
            return false;
        }
        return equals((AngleConstantD) obj);
    }

}
