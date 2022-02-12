package org.a05annex.util.geo3d;

public class Point3d implements Cloneable {
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
     * Creates a new instance of <code>Point3d</code> initialized to the origin.
     */
    public Point3d() {
    }

    /**
     * Creates a new instance of <code>Point3d</code> initialized to a specified location.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @param z The Z coordinate of the point.
     */
    public Point3d(final double x, final double y, final double z) {
        setValue(x, y, z);
    }

    /**
     * Creates a new instance of <code>Point3d</code> initialized to a specified location.
     *
     * @param ptInit The location of the instantiated point.
     */
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
//     * @return Returns the distance to the plane,<code>pln</code>.
//     */
//    public float getDistanceTo(final Plane3f pln) {
//        return (pln.A * x) + (pln.B * y) + (pln.C * z) + pln.D;
//    }

    /**
     * Get the geometric (straight line) distance from this point to another point.
     *
     * @param pt The other point.
     * @return Returns the distance between this point and another point, <code>pt</code>
     */
    public double getDistanceTo(final Point3d pt) {
        final double dx = x - pt.x;
        final double dy = y - pt.y;
        final double dz = z - pt.z;
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

//    /**
//     * Get the perpendicular distance between this point and a line.  This is done geometrically by first defining a plane
//     * with the line (the plane perpendicular to the line that goes through the origin of the line), finding the distance
//     * from this point to that plane, and then projecting the origin of the line by that distance along the line so that
//     * we have a point on the line that is on a plane perpendicular to the line that also contains this point.  After all
//     * that, we simply get the distance between the two points.
//     *
//     * @param ln The line we will get the perpendicular distance from.  For best results, the line should be normalized.
//     * @return Returns the perpendicular distance from this point to the line <code>ln</code>.
//     */
//    public float getDistanceTo(final Line3f ln) {
//        // get the D of the plane
//        final float fD = -((ln.direction.i * ln.origin.x) + (ln.direction.j * ln.origin.y) + (ln.direction.k * ln.origin.z));
//        // get the distance from this point to the plane
//        final float fPlaneDist = (ln.direction.i * x) + (ln.direction.j * y) + (ln.direction.k * z) + fD;
//        // get the point on the line that is in the same perpendicular plane as this point, and then get the distance
//        final float dx = x - (ln.origin.x + (fPlaneDist * ln.direction.i));
//        final float dy = y - (ln.origin.y + (fPlaneDist * ln.direction.j));
//        final float dz = z - (ln.origin.z + (fPlaneDist * ln.direction.k));
//        return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
//    }

    /**
     * Tests another point, <code>pt</code>, for equality with this vector.
     *
     * @param pt The point to be tested.  This point is unchanged.
     * @return Returns <code>true</code> if <code>pt</code> is equal to this point (identical
     * in all coordinates), and <code>false</code> otherwise.
     */
    public boolean equals(final Point3d pt) {
        return this == pt || ((null != pt) && (!(x != pt.x)) && (!(y != pt.y)) && (!(z != pt.z)));
    }

    /**
     * Tests another object, <code>obj</code>, for equality with this point.
     *
     * @param obj The object to be tested.  This object is unchanged.
     * @return Returns <code>true</code> if <code>obj</code> is equal to this point (also a <code>Point3d</code> and
     * identical in all coordinates), and <code>false</code> otherwise.
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
    public Object clone() throws CloneNotSupportedException {
        Object o = super.clone();
        return clonePoint3d();
    }

    /**
     * Clone this point.
     *
     * @return Returns a clone of the point.
     */
    public Point3d clonePoint3d() {
        return new Point3d(x, y, z);
    }

}
