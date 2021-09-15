package org.a05annex.util.geo3d;

public class Point3d {
    /**
     * The X coordinate of the point.
     */
    double x;
    /**
     * The Y coordinate of the point.
     */
    double y;
    /**
     * The Z coordinate of the point.
     */
    double z;

    /**
     * Creates a new instance of <tt>Point3f</tt> initialized to the origin.
     */
    public Point3d() {
    }

    /**
     * Creates a new instance of <tt>Point3f</tt> initialized to a specified location.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @param z The Z coordinate of the point.
     */
    public Point3d(final double x, final double y, final double z) {
        setValue(x, y, z);
    }

    /**
     * Creates a new instance of <tt>Point3f</tt> initialized to a specified location.
     *
     * @param ptInit The location of the instantiated point.
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Point3d(final Point3d ptInit) {
        setValue(ptInit);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
    /**
     * Sets the point to a specified location.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @param z The Z coordinate of the point.
     * @return Returns the point after setting the specified location.
     */
    public Point3d setValue(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Sets the point to a specified location.
     *
     * @param ptInit The new location of the point.
     * @return Returns the point after setting the specified location.
     */
    public Point3d setValue(final Point3d ptInit) {
        x = ptInit.x;
        y = ptInit.y;
        z = ptInit.z;
        return this;
    }

    /**
     * Adds a vector to this point.
     *
     * @param v The vector to be added
     * @return Returns this point after the vector is added
     */
    public Point3d addVector(final Vector3d v) {
        x += v.i;
        y += v.j;
        z += v.k;
        return this;
    }

    public Point3d addPoint(final Point3d pt) {
        x += pt.x;
        y += pt.y;
        z += pt.z;
        return this;
    }

    public Point3d scale(final float scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        return this;
    }

//    /**
//     * Get the perpendicular distance from this point to a plane.
//     *
//     * @param pln The plane
//     * @return Returns the distance to the plane,<tt>pln</tt>.
//     */
//    public float getDistanceTo(final Plane3f pln) {
//        return (pln.A * x) + (pln.B * y) + (pln.C * z) + pln.D;
//    }

    /**
     * Get the geometric (straight line) distance from this point to another point.
     *
     * @param pt The other point.
     * @return Returns the distance between this point and another point, <tt>pt</tt>
     */
    public double getDistanceTo(final Point3d pt) {
        final double dx = x - pt.x;
        final double dy = y - pt.y;
        final double dz = z - pt.z;
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    /**
     * Get the perpendicular distance between this point and a line.  This is done geometrically by first defining a plane
     * with the line (the plane perpendicular to the line that goes through the origin of the line), finding the distance
     * from this point to that plane, and then projecting the origin of the line by that distance along the line so that
     * we have a point on the line that is on a plane perpendicular to the line that also contains this point.  After all
     * that, we simply get the distance between the two points.
     *
     * @param ln The line we will get the perpendicular distance from.  For best results, the line should be normalized.
     * @return Returns the perpendicular distance from this point to the line <tt>ln</tt>.
     */
//    public float getDistanceTo(final Line3f ln) {
//        // get the D of the plane
//        final float fD = -((ln.m_vDir.i * ln.m_ptOrg.x) + (ln.m_vDir.j * ln.m_ptOrg.y) + (ln.m_vDir.k * ln.m_ptOrg.z));
//        // get the distance from this point to the plane
//        final float fPlaneDist = (ln.m_vDir.i * x) + (ln.m_vDir.j * y) + (ln.m_vDir.k * z) + fD;
//        // get the point on the line that is in the same perpendicular plane as this point, and then get the distance
//        final float dx = x - (ln.m_ptOrg.x + (fPlaneDist * ln.m_vDir.i));
//        final float dy = y - (ln.m_ptOrg.y + (fPlaneDist * ln.m_vDir.j));
//        final float dz = z - (ln.m_ptOrg.z + (fPlaneDist * ln.m_vDir.k));
//        return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
//    }

    /**
     * Tests another point, <tt>pt</tt>, for equality with this vector.
     *
     * @param pt The point to be tested.  This point is unchanged.
     * @return Returns <tt>true</tt> if <tt>pt</tt> is equal to this point (identical
     * in all coordinates), and <tt>false</tt> otherwise.
     */
    public boolean equals(final Point3d pt) {
        return this == pt || ((null != pt) && (!(x != pt.x)) && (!(y != pt.y)) && (!(z != pt.z)));
    }

    /**
     * Tests another object, <tt>obj</tt>, for equality with this point.
     *
     * @param obj The object to be tested.  This object is unchanged.
     * @return Returns <tt>true</tt> if <tt>obj</tt> is equal to this point (also a <tt>Point3d</tt> and
     * identical in all coordinates), and <tt>false</tt> otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        return (null != obj) && (getClass() == obj.getClass()) && equals((Point3d) obj);
    }

    /**
     * Clone this point.
     *
     * @return Returns a clone of the point.
     */
    @Override
    public Object clone() {
        return clonePoint3f();
    }

    /**
     * Clone this point.
     *
     * @return Returns a clone of the point.
     */
    public Point3d clonePoint3f() {
        return new Point3d(x, y, z);
    }

}
