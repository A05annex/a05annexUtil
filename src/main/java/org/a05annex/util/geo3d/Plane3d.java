package org.a05annex.util.geo3d;

public class Plane3d {
    /**
     * The <b>A</b> coefficient of the plane equation.
     */
    public double A;

    /**
     * The <b>B</b> coefficient of the plane equation.
     */
    public double B;

    /**
     * The <b>C</b> coefficient of the plane equation.
     */
    public double C;

    /**
     * The <b>D</b> coefficient of the plane equation.
     */
    public double D;

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new uninitialized instance of <tt>Plane3d</tt>.
     */
    public Plane3d() {
    }

    /**
     * Instantiates an initialized instance of <tt>Plane3d</tt>.
     *
     * @param A The <b>A</b> coefficient of the plane equation.
     * @param B The <b>B</b> coefficient of the plane equation.
     * @param C The <b>C</b> coefficient of the plane equation.
     * @param D The <b>D</b> coefficient of the plane equation.
     */
    public Plane3d(final double A, final double B, final double C, final double D) {
        setValue(A, B, C, D);
    }

    /**
     * Instantiates an initialized instance of <tt>Plane3d</tt>.
     *
     * @param normal The normal of the plane. NOTE: the normal is not re-normalized as part of the
     *               initialization.  If there is any question about whether the normal is a unit vector,
     *               then the plane should be normalized after instantiation.
     * @param pt     A point on the plane
     */
    public Plane3d(final Vector3d normal, final Point3d pt) {
        setValue(normal, pt);
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Sets the coefficients of the plane equation..
     *
     * @param A The <b>A</b> coefficient of the plane equation.
     * @param B The <b>B</b> coefficient of the plane equation.
     * @param C The <b>C</b> coefficient of the plane equation.
     * @param D The <b>D</b> coefficient of the plane equation.
     * @return Returns this plane after the coefficients of the plane equation have been set.
     */
    public Plane3d setValue(final double A, final double B, final double C, final double D) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        return this;
    }

    /**
     * Sets the coefficients of the plane equation for a plane that has the specified normal and passes
     * through the specified point.
     *
     * @param normal The normal of the plane. NOTE: the normal is not re-normalized as part of the
     *               initialization.  If there is any question about whether the normal is a unit vector,
     *               then the plane should be normalized after instantiation.
     * @param pt     A point on the plane
     * @return Returns this plane after the coefficients of the plane equation have been set.
     */
    public Plane3d setValue(final Vector3d normal, final Point3d pt) {
        A = normal.i;
        B = normal.j;
        C = normal.k;
        D = -((A * pt.x) + (B * pt.y) + (C * pt.z));
        return this;
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Normalizes the plane equation coefficients so that the normal, <b>A</b>,<b>B</b>,<b>C</b>, is a unit vector.
     *
     * @return Returns this plane after normalization.
     * @throws ZeroLengthVectorException If the current length of the normal is so close to zero that a meaningful
     *                                   normal cannot be generated.
     */
    public Plane3d normalize() throws ZeroLengthVectorException {
        final double dLength = Math.sqrt((A * A) + (B * B) + (C * C));
        if (PackageConstants.isZero(dLength)) {
            throw new ZeroLengthVectorException();
        }
        final double dScale = 1.0f / dLength;
        A *= dScale;
        B *= dScale;
        C *= dScale;
        D *= dScale;
        return this;
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Gets the normal of the vector,<b>A</b>,<b>B</b>,<b>C</b>.  The normal will be a unit vector only if the plane
     * is normalized.
     *
     * @param normal The vector to be set to the normal.
     * @return The vector <tt>normal</tt> is returned.
     */
    public Vector3d getNormal(final Vector3d normal) {
        normal.i = A;
        normal.j = B;
        normal.k = C;
        return normal;
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Tests another plane, <tt>pln</tt>, for equality with this plane.
     *
     * @param pln The plane to be tested.  This plane is unchanged.
     * @return Returns <tt>true</tt> if <tt>pln</tt> is equal to this plane (identical
     * in all coefficients), and <tt>false</tt> otherwise. NOTE: it is possible that the planes represent the same
     * physical plane but have unequal coefficients if one or both are not normalized.
     */
    public boolean equals(final Plane3d pln) {
        if (this == pln) {
            return true;
        }
        return (null != pln) && (A == pln.A) && (B == pln.B) && (C == pln.C) && (D == pln.D);
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Tests another object, <tt>obj</tt>, for equality with this plane.
     *
     * @param obj The object to be tested.  This object is unchanged.
     * @return Returns <tt>true</tt> if <tt>obj</tt> is both a <tt>Plane3d</tt> and is equal to this plane (identical
     * in all coefficients), and <tt>false</tt> otherwise. NOTE: it is possible that the planes represent the same
     * physical plane but have unequal coefficients if one or both are not normalized.
     */
    public boolean equals(final Object obj) {
        if ((null == obj) ||
                (getClass() != obj.getClass())) {
            return false;
        }
        return equals((Plane3d) obj);
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Clone this plane.
     *
     * @return Returns a clone of the plane.  The clone is NOT obtained from the object cache.
     */
    public Object clone() {
        return clonePlane3d();
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Clone this plane.
     *
     * @return Returns a clone of the plane.  The clone is NOT obtained from the object cache.
     */
    public Plane3d clonePlane3d() {
        return new Plane3d(A, B, C, D);
    }

}
