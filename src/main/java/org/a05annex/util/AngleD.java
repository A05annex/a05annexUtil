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
     * Instantiate an angle with the specified value.
     * @param type The unit in which the angle is specified.
     * @param angle The value of the angle in the specified units.
     */
    public AngleD(final AngleUnit type, final double angle) {
        super(type, angle);
    }

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
    public AngleD mult(final double multiplier) {
        radians *= multiplier;
        return this;
    }

    public AngleD divide(final double divisor) {
        radians /= divisor;
        return this;
    }

    public AngleD add(final AngleConstantD addAngle) {
        radians += addAngle.radians;
        return this;
    }

    public AngleD subtract(final AngleConstantD subtractAngle) {
        radians -= subtractAngle.radians;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    public AngleD atan(final double dTan) {
        radians = Math.atan(dTan);
        return this;
    }

    public AngleD atan2(final double dSin, final double dCos) {
        radians = Math.atan2(dSin, dCos);
        return this;
    }

    public AngleD asin(final double dSin) {
        radians = Math.asin(dSin);
        return this;
    }

    public AngleD acos(final double dCos) {
        radians = Math.acos(dCos);
        return this;
    }

//    //----------------------------------------------------------------------------------------------------------------
    public void setDegrees(final double fDegrees) {
        radians = fDegrees * DEGREES_TO_RADIANS;
    }

    public void setRadians(final double fRadians) {
        radians = fRadians;
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return cloneAngleD();
    }

    //------------------------------------------------------------------------------------------------------------------
    public AngleD cloneAngleD() {
        return new AngleD(AngleUnit.RADIANS, radians);
    }
}
