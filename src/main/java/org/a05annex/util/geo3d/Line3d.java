package org.a05annex.util.geo3d;

public class Line3d {
    //------------------------------------------------------------------------------------------------------------------
    /**
     * The starting point of the line.
     */
    final Point3d origin = new Point3d();

    /**
     * The direction of the line.  NOTE: in typical use, the direction vector is
     * normalized so that points a given distance from the starting point can be simply
     * generated by adding the direction vector scaled by the distance to the starting
     * point.  However, normalization of the direction vector is not enforced by the
     * <code>Line3d</code> class, and must be controlled by the user of the class.
     */
    final Vector3d direction = new Vector3d();

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of <code>Line3d</code>.
     */
    public Line3d() {
    }

    /**
     * Creates a new instance of <code>Line3d</code>.
     *
     * @param ptOrg The origin of the line.
     * @param vDir  The direction of the line.  NOTE: the direction is used as given and is not normalized
     *              during initialization.
     */
    public Line3d(final Point3d ptOrg, final Vector3d vDir) {
        setValue(ptOrg, vDir);
    }

    /**
     * Creates a new instance of <code>Line3d</code>.
     *
     * @param ptOrg  The origin of the line.
     * @param ptThru A point the line passes through.  The direction of the line is from the origin to this
     *               point, and is normalized during initialization.
     * @throws ZeroLengthVectorException This exception is thrown if the <code>ptOrg</code> and <code>ptThru</code>
     *                                   points are essentially identical (so close together that a meaningful
     *                                   direction cannot be determined).
     */
    public Line3d(final Point3d ptOrg, final Point3d ptThru) throws ZeroLengthVectorException {
        setValue(ptOrg, ptThru);
    }

    /**
     * Creates a new instance of <code>Line3d</code>.
     *
     * @param lnInit The line this line will be set equal to.
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Line3d(final Line3d lnInit) {
        setValue(lnInit);
    }

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Sets the value of the line to be a line starting at <code>ptOrg</code> and in the direction <code>vDir</code>.
     *
     * @param ptOrg The origin of the line.
     * @param vDir  The direction of the line.  NOTE: the direction is used as given and is not normalized
     *              during initialization.
     * @return Returns this line initialized with the value set as specified.
     */
    public Line3d setValue(final Point3d ptOrg, final Vector3d vDir) {
        origin.setValue(ptOrg);
        direction.setValue(vDir);
        return this;
    }

    /**
     * Sets the value of the line to be a normalized line starting at <code>ptOrg</code> and passing
     * through <code>ptThru</code>.
     *
     * @param ptOrg  The origin of the line.
     * @param ptThru A point the line passes through.  The direction of the line is from the origin to this
     *               point, and is normalized during initialization.
     * @return Returns this line initialized with the value set as specified.
     * @throws ZeroLengthVectorException This exception is thrown if the <code>ptOrg</code> and <code>ptThru</code>
     *                                   points are essentially identical (so close together that a meaningful
     *                                   direction cannot be determined).
     */
    public Line3d setValue(final Point3d ptOrg, final Point3d ptThru) throws ZeroLengthVectorException {
        origin.setValue(ptOrg);
        direction.setValue(ptOrg, ptThru).normalize();
        return this;
    }

    /**
     * Sets the value of the line to be equal to another line.
     *
     * @param lnInit The line this line will be set equal to.
     * @return Returns this line initialized with the value set as specified.
     */
    public Line3d setValue(final Line3d lnInit) {
        origin.setValue(lnInit.origin);
        direction.setValue(lnInit.direction);
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Normalizes the line - specifically, this means that the direction vector of the line is normalized.
     *
     * @return Returns the line after normalization.
     * @throws ZeroLengthVectorException Thrown if the length of the direction vector is so close to zero that the
     *                                   normalization operation is meaningless - i.e. the direction of the line
     *                                   to totally ambiguous.
     */
    public Line3d normalize() throws ZeroLengthVectorException {
        direction.normalize();
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the point at some parametric distance along the line.  If the line is normalized (i.e. the direction vector
     * is normalized) the parametric distance is equal to the geometric distance from the origin of the line.
     *
     * @param pt    The point at the parametric distance.  This point is passed in so that the caller can decide
     *              whether this point is instantiated, or part of the description of some other object.
     * @param fDist The parametric distance along the line.
     * @return Returns <code>pt</code>.
     */
    public Point3d pointAtDistance(final Point3d pt, final float fDist) {
        pt.x = origin.x + (fDist * direction.i);
        pt.y = origin.y + (fDist * direction.j);
        pt.z = origin.z + (fDist * direction.k);
        return pt;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Tests another line, <code>ln</code>, for equality with this line.
     *
     * @param ln The line to be tested.  This line is unchanged.
     * @return Returns <code>true</code> if <code>ln</code> is equal to this line (identical
     * in all components), and <code>false</code> otherwise.
     */
    public boolean equals(final Line3d ln) {
        if (this == ln) {
            return true;
        }
        return (null != ln) && (origin.equals(ln.origin)) && (direction.equals(ln.direction));
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Tests another object, <code>obj</code>, for equality with this line.
     *
     * @param obj The object to be tested.  This object is unchanged.
     * @return Returns <code>true</code> if <code>obj</code> is equal to this line (also a <code>Line3d</code> and
     * identical in all components), and <code>false</code> otherwise.
     */
    public boolean equals(final Object obj) {
        if ((null == obj) || (getClass() != obj.getClass())) {
            return false;
        }
        return equals((Line3d) obj);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Clone this line.
     *
     * @return (not null) Returns a clone of this line.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Clone this line.
     *
     * @return (not null)Returns a clone of this line.
     */
    public Line3d cloneLine3d() {
        try {
            return (Line3d) this.clone();
        } catch (CloneNotSupportedException e) {
            // This will not happen because clone is supported for this object.
        }
        return null;
    }

}
