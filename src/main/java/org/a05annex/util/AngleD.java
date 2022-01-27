package org.a05annex.util;

import org.jetbrains.annotations.NotNull;

/**
 * This is a wrapper class for a mutable angle represented internally as radians at double precision.
 */
public class AngleD extends AngleConstantD implements Cloneable {

    /**
     * Creates a new instance of <code>AngleD</code>
     */
    public AngleD() {
    }

    /**
     * Instantiate a mutable angle with the specified value.
     *
     * @param type  The unit in which the angle is specified.
     * @param angle The value of the angle in the specified units.
     */
    public AngleD(final AngleUnit type, final double angle) {
        super(type, angle);
    }

    /**
     * Instantiate a mutable angle initialized to the specified angle
     *
     * @param angle The angle to initialize this angle to.
     */
    public AngleD(final AngleConstantD angle) {
        super(angle);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Set the value using the {@link AngleUnit} type and value.
     *
     * @param type  The unit in which the angle is specified.
     * @param angle The value of the angle in the specified units.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD setValue(final AngleUnit type, final double angle) {
        radians = AngleUnit.DEGREES == type ? angle * DEGREES_TO_RADIANS : angle;
        return this;
    }

    /**
     * Set this angle to the value of the specified angle.
     *
     * @param angle The angle hose value this angle should be set to.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD setValue(final @NotNull AngleConstantD angle) {
        radians = angle.radians;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Multiply this angle by the specified multiplier.
     *
     * @param multiplier The multiplier.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD mult(final double multiplier) {
        radians *= multiplier;
        return this;
    }

    /**
     * Divide this angle by the specified divisor.
     *
     * @param divisor The divisor.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD divide(final double divisor) {
        radians /= divisor;
        return this;
    }

    /**
     * Add the specified <code>addAngle</code> to this angle.
     *
     * @param addAngle The angle to be added.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD add(final @NotNull AngleConstantD addAngle) {
        radians += addAngle.radians;
        return this;
    }

    /**
     * Add the specified <code>addAngle</code> to this angle.
     *
     * @param type  The unit in which the angle is specified.
     * @param addAngle The angle to be added.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD add(final AngleUnit type, final double addAngle) {
        radians += (AngleUnit.DEGREES == type) ? addAngle * DEGREES_TO_RADIANS : addAngle;
        return this;
    }

    /**
     * Subtract the specified <code>subtractAngle</code> to this angle.
     *
     * @param subtractAngle The angle to be subtracted.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD subtract(final @NotNull AngleConstantD subtractAngle) {
        radians -= subtractAngle.radians;
        return this;
    }

    /**
     * Subtract the specified <code>subtractAngle</code> to this angle.
     *
     * @param type  The unit in which the angle is specified.
     * @param subtractAngle The angle to be subtracted.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD subtract(final AngleUnit type, final double subtractAngle) {
        radians -= (AngleUnit.DEGREES == type) ? subtractAngle * DEGREES_TO_RADIANS : subtractAngle;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Set the value of this angle to the arctangent of specified <code>tan</code>.
     *
     * @param tan The tangent of the angle.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD atan(final double tan) {
        radians = Math.atan(tan);
        return this;
    }

    /**
     * Set the value of this angle to the arctangent of specified <code>sin</code> and
     * <code>cos</code>. Note that <code>sin</code> and <code>cos</code> do not need to be
     * normalized
     *
     * @param sin The sine of the angle.
     * @param cos The cosine of the angle.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD atan2(final double sin, final double cos) {
        radians = Math.atan2(sin, cos);
        return this;
    }

    /**
     * Set the value of this angle to the arcsine of specified <code>sin</code>.
     *
     * @param sin The sine of the angle.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD asin(final double sin) {
        radians = Math.asin(sin);
        return this;
    }

    /**
     * Set the value of this angle to the arccosine of specified <code>cos</code>.
     *
     * @param cos The cosine of the angle.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD acos(final double cos) {
        radians = Math.acos(cos);
        return this;
    }

    //----------------------------------------------------------------------------------------------------------------

    /**
     * Set this angle value to the specified <code>degrees</code>
     *
     * @param degrees The angle in degrees.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD setDegrees(final double degrees) {
        radians = degrees * DEGREES_TO_RADIANS;
        return this;
    }

    /**
     * Set this angle value to the specified <code>degrees</code>
     *
     * @param radians The angle in radians.
     * @return Returns this angle to support operation chaining.
     */
    @NotNull
    public AngleD setRadians(final double radians) {
        this.radians = radians;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Clone this angle.
     *
     * @return A newly instantiated clone of this angle
     * @throws CloneNotSupportedException Part of the {@link Cloneable} interface, but
     *                                    will not be thrown by this method.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return cloneAngleD();
    }

    /**
     * Clone this angle.
     *
     * @return A newly instantiated clone of this angle
     */
    public AngleD cloneAngleD() {
        return new AngleD(AngleUnit.RADIANS, radians);
    }
}
