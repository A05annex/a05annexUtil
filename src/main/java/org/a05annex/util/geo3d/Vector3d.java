package org.a05annex.util.geo3d;

public class Vector3d {
    /**
     * The I component of the vector.
     */
    double i;
    /**
     * The J component of the vector.
     */
    double j;
    /**
     * The K component of the vector.
     */
    double k;

    /**
     * Creates a new instance of <tt>Vector3f</tt>.  This uses the default
     * initialization of the fields setting the vector to 0,0,0.
     */
    public Vector3d() {
    }

    /**
     * Creates a new instance of <tt>Vector3f</tt> with the I, J, and K components
     * set as specified.  No normalization is performed, the values are set exactly as
     * specified.
     *
     * @param i The I component of the vector.
     * @param j The J component of the vector.
     * @param k The K component of the vector.
     */
    public Vector3d(final double i, final double j, final double k) {
        setValue(i, j, k);
    }

    /**
     * Creates a new instance of <tt>Vector3f</tt> set equal to another vector.
     *
     * @param v The vector to make this new vector equal to.
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Vector3d(final Vector3d v) {
        setValue(v);
    }

    /**
     * Creates a new instance of <tt>Vector3f</tt> initialized to be a vector from <tt>ptOrg</tt> to
     * <tt>ptThru</tt>.  This length of this vector is equal to the distance from <tt>ptOrg</tt> to
     * <tt>ptThru</tt>.  The vector should be normalized if you need a direction vector.
     *
     * @param ptOrg  The start of the vector.
     * @param ptThru The end of the vector.
     */
    public Vector3d(final Point3d ptOrg, final Point3d ptThru) {
        setValue(ptThru.x - ptOrg.x, ptThru.y - ptOrg.y, ptThru.z - ptOrg.z);
    }

    /**
     * Get the I component of the vector.
     * @return the I component of the vector.
     */
    public double getI() {
        return i;
    }

    /**
     * Get the J component of the vector.
     * @return the J component of the vector.
     */
    public double getJ() {
        return j;
    }

    /**
     * Get the K component of the vector.
     * @return the K component of the vector.
     */
    public double getK() {
        return k;
    }

    /**
     * Sets the I, J, and K components as specified.  No normalization is performed, the values are set exactly
     * as specified.
     *
     * @param i The I component of the vector.
     * @param j The J component of the vector.
     * @param k The K component of the vector.
     * @return Returns this vector with the value set as specified.
     */
    public Vector3d setValue(final double i, final double j, final double k) {
        this.i = i;
        this.j = j;
        this.k = k;
        return this;
    }

    /**
     * Sets the value of the vector to be a vector from <tt>ptOrg</tt> to <tt>ptThru</tt>.  This
     * length of this vector is equal to the distance from <tt>ptOrg</tt> to <tt>ptThru</tt>.  The
     * vector should be normalized if you need a direction vector.
     *
     * @param ptOrg  The start of the vector.
     * @param ptThru The end of the vector.
     * @return Returns this vector with the value set as specified.
     */
    public Vector3d setValue(final Point3d ptOrg, final Point3d ptThru) {
        return setValue(ptThru.x - ptOrg.x, ptThru.y - ptOrg.y, ptThru.z - ptOrg.z);
    }

    /**
     * Sets the value of this vector (each of the components) to be equal to some other vector, <tt>v</tt>.
     *
     * @param v The vector to make this vector equal to.  The value of this vector is unchanged.
     * @return Returns this vector with its components set equal to vector <tt>v</tt>.
     */
    public Vector3d setValue(final Vector3d v) {
        i = v.i;
        j = v.j;
        k = v.k;
        return this;
    }

    /**
     * Reverse the direction of this vector.  This is the same as scaling the vector by -1.
     *
     * @return Returns this vector after it has been reversed.
     */
    public Vector3d reverse() {
        i = -i;
        j = -j;
        k = -k;
        return this;
    }

    /**
     * Gets the dot product between this vector and another vector, <tt>v</tt>.
     *
     * @param v The vector against which we take the dot product.  The value of this vector is unchanged.
     * @return Returns the dot product between this vector and vector <tt>v</tt>.
     */
    public double dot(final Vector3d v) {
        return (i * v.i) + (j * v.j) + (k * v.k);
    }

    /**
     * Adds a vector, <tt>v</tt>, to this vector.
     *
     * @param v The vector to be added to this vector.  The value of this vector is unchanged.
     * @return Returns this vector after vector <tt>v</tt> has been added.
     */
    public Vector3d add(final Vector3d v) {
        i += v.i;
        j += v.j;
        k += v.k;
        return this;
    }

    /**
     * Subtracts a vector, <tt>v</tt>, from this vector.
     *
     * @param v The vector to be subtracted from this vector.  The value of this vector is unchanged.
     * @return Returns this vector after vector <tt>v</tt> has been subtracted.
     */
    public Vector3d subtract(final Vector3d v) {
        i -= v.i;
        j -= v.j;
        k -= v.k;
        return this;
    }

    /**
     * Cross this vector with another vector, <tt>v</tt>.  The cross product produces a vector perpendicular to the 2 vectors
     * crossed.  Specifically, the X axis crossed with the Y axis will produce the Z axis, while the cross of the Y axis with the
     * X axis will produce the -Z axis.  NOTE: i cross j = k; j cross k = i; and k cross i = j regardless of the handedness of
     * the coordinate system being used.
     *
     * @param v The vector to be crossed with this vector.  The value of this vector is unchanged.
     * @return Returns this vector after vector <tt>v</tt> has been subtracted.
     */
    public Vector3d cross(final Vector3d v) {
        setValue((j * v.k) - (k * v.j),
                (k * v.i) - (i * v.k),
                (i * v.j) - (j * v.i));
        return this;
    }

    /**
     * Get the length of this vector.
     *
     * @return Returns the length of this vector.  The length is always &gt;= 0.
     */
    public double getLength() {
        return ((float) (Math.sqrt((double) ((i * i) + (j * j) + (k * k)))));
    }

    /**
     * Normalize this vector - in other words, scale the vector so its length is equal to 1 and it can
     * be used as a direction vector.
     *
     * @return This vector is returned after normalization.
     * @throws ZeroLengthVectorException The exception is thrown when the length of the vector is eoo close to zero that
     *                                   normalizing the vector returns a meaningless result.
     */
    public Vector3d normalize() throws ZeroLengthVectorException {
        final double length = getLength();
        if (PackageConstants.isZero(length)) {
            throw new ZeroLengthVectorException();
        }
        return scale(1.0 / length);
    }

    /**
     * Scale a vector - multiply each of its components by a scaling factor.
     *
     * @param fScale The scaling factor.
     * @return Returns this vector after it has been scaled.
     */
    public Vector3d scale(final double fScale) {
        i *= fScale;
        j *= fScale;
        k *= fScale;
        return this;
    }

    /**
     * Tests another vector, <tt>v</tt>, for equality with this vector.
     *
     * @param v The vector to be tested.  This vector is unchanged.
     * @return Returns <tt>true</tt> if <tt>v</tt> is equal to this vector (identical
     * in all components), and <tt>false</tt> otherwise.
     */
    public boolean equals(final Vector3d v) {
        if (this == v) {
            return true;
        }
        return (null != v) && (i == v.i) && (j == v.j) && (k == v.k);
    }

    /**
     * Tests another object, <tt>obj</tt>, for equality with this vector.
     *
     * @param obj The object to be tested.  This object is unchanged.
     * @return Returns <tt>true</tt> if <tt>obj</tt> is equal to this vector (also a <tt>Vector3f</tt> and
     * identical in all components), and <tt>false</tt> otherwise.
     */
    public boolean equals(final Object obj) {
        if ((null == obj) ||
                (getClass() != obj.getClass())) {
            return false;
        }
        return equals((Vector3d) obj);
    }

    /**
     * Clone this vector.
     *
     * @return Returns a clone of the vector.
     */
    public Object clone() {
        return cloneVector3f();
    }

    /**
     * Clone this vector.
     *
     * @return Returns a clone of this vector.
     */
    public Vector3d cloneVector3f() {
        return new Vector3d(i, j, k);
    }
}
