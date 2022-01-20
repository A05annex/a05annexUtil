package org.a05annex.util;

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
     * @param type The unit in which the angle is specified.
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
    public AngleD setValue(final AngleUnit type, final double dAngle) {
        radians = AngleUnit.DEGREES == type ? dAngle * DEGREES_TO_RADIANS : dAngle;
        return this;
    }

    public AngleD setValue(final AngleConstantD aInit) {
        radians = aInit.radians;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Multiply this angle by the specified multiplier.
     * @param multiplier The multiplier.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD mult(final double multiplier) {
        radians *= multiplier;
        return this;
    }

    /**
     * Divide this angle by the specified divisor.
     * @param divisor The divisor.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD divide(final double divisor) {
        radians /= divisor;
        return this;
    }

    /**
     * Add the specified <code>addAngle</code> to this angle.
     * @param addAngle The angle to be added.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD add(final AngleConstantD addAngle) {
        radians += addAngle.radians;
        return this;
    }

    /**
     * Subtract the specified <code>subtractAngle</code> to this angle.
     * @param subtractAngle The angle to be subtracted.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD subtract(final AngleConstantD subtractAngle) {
        radians -= subtractAngle.radians;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Set the value of this angle to the arctangent of specified <code>tan</code>.
     * @param tan The tangent of the angle.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD atan(final double tan) {
        radians = Math.atan(tan);
        return this;
    }

    /**
     * Set the value of this angle to the arctangent of specified <code>sin</code> and
     * <code>cos</code>. Note that <code>sin</code> and <code>cos</code> do not need to be
     * normalized
     * @param sin The sine of the angle.
     * @param cos The codine of the angle.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD atan2(final double sin, final double cos) {
        radians = Math.atan2(sin, cos);
        return this;
    }

    /**
     * Set the value of this angle to the arcsine of specified <code>sin</code>.
     * @param sin The sine of the angle.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD asin(final double sin) {
        radians = Math.asin(sin);
        return this;
    }

    /**
     * Set the value of this angle to the arccosine of specified <code>cos</code>.
     * @param cos The cosine of the angle.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD acos(final double cos) {
        radians = Math.acos(cos);
        return this;
    }

    //----------------------------------------------------------------------------------------------------------------

    /**
     * Set this angle value to the specified <code>degrees</code>
     * @param degrees The angle in degrees.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD setDegrees(final double degrees) {
        radians = degrees * DEGREES_TO_RADIANS;
        return this;
    }

    /**
     * Set this angle value to the specified <code>degrees</code>
     * @param radians The angle in radians.
     * @return Returns this angle to support operation chaining.
     */
    public AngleD setRadians(final double radians) {
        this.radians = radians;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return cloneAngleD();
    }

    /**
     *
     * @return
     */
    public AngleD cloneAngleD() {
        return new AngleD(AngleUnit.RADIANS, radians);
    }
}
