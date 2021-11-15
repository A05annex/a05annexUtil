package org.a05annex.util;

/**
 * This is a wrapper class for an angle. We are using radians as the default unit for angles, so the internal
 * representation is in radians at double precision.
 */
public class AngleD implements Cloneable {
    /**
     * The degrees type.  Used to indicate the angle is specified in degrees when a type is specified.
     */
    public static final int DEGREES = 0;

    /**
     * The radians type.  Used to indicate the angle is specified in radians when a type is specified.
     */
    public static final int RADIANS = 1;

    /**
     * The conversion factor for radians to degrees. Normally <code>AngledD</code> handles any conversions you need
     * so there is probably no reason to ever use this.
     */
    private static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;

    /**
     * The conversion factor for degrees to radians. Normally <code>AngleD</code> handles any conversions you need
     * so there is probably no reason to ever use this.
     */
    public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

    private double radians;   // the internal representation is always radians


    /**
     * Creates a new instance of <code>AngleD</code>
     */
    public AngleD() {
    }

    public AngleD(final int type, final double dAngle) {
        setValue(type, dAngle);
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public AngleD(final AngleD aInit) {
        setValue(aInit);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public AngleD setValue(final int type, final double dAngle) {
        if (DEGREES == type) {
            radians = dAngle * DEGREES_TO_RADIANS;
        } else if (RADIANS == type) {
            radians = dAngle;
        } else {
            throw new IllegalArgumentException("Unrecognized angle type");
        }
        return this;
    }

    public AngleD setValue(final AngleD aInit) {
        radians = aInit.radians;
        return this;
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public AngleD mult(final double f) {
        radians *= f;
        return this;
    }

    public AngleD add(final AngleD aAng) {
        radians += aAng.radians;
        return this;
    }

    public AngleD subtract(final AngleD aAng) {
        radians -= aAng.radians;
        return this;
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public AngleD atan(final double fTan) {
        radians = Math.atan(fTan);
        return this;
    }

    public AngleD atan2(final double fSin, final double fCos) {
        radians = Math.atan2(fSin, fCos);
        return this;
    }

    public AngleD asin(final double fSin) {
        radians = Math.asin(fSin);
        return this;
    }

    public AngleD acos(final double fCos) {
        radians = Math.acos(fCos);
        return this;
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public double getDegrees() {
        return radians * RADIANS_TO_DEGREES;
    }

    public void setDegrees(final double fDegrees) {
        radians = fDegrees * DEGREES_TO_RADIANS;
    }

    public double getRadians() {
        return radians;
    }

    public void setRadians(final double fRadians) {
        radians = fRadians;
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public double cos() {
        return Math.cos(radians );
    }

    public double sin() {
        return Math.sin(radians);
    }

    public double tan() {
        return Math.tan(radians);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public boolean equals(final AngleD angle) {
        return (this == angle) || ((null != angle) && (radians == angle.radians));
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj) {
        if ((null == obj) || (getClass() != obj.getClass())) {
            return false;
        }
        return equals((AngleD) obj);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return cloneAngleD();
    }

    //-------------------------------------------------------------------------------------------------------------------------
    public AngleD cloneAngleD() {
        return new AngleD(RADIANS, radians);
    }
}
