package org.a05annex.util.geo2d;

import org.a05annex.util.Utl;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;

/**
 * The description of a plane parallel to the Z axis intersecting the XY plane, which is a line in XY. This
 * is the implicit representation of a line in 2D which is the degenerate form of the 3D implicit plane
 * equation: {@code Ax * By + Bz + D = 0}; to {@code Ax + By + D = 0}. This is a normalized description, so
 * {@code sqrt((A*A) + (B*B)) = 1.0} and the perpendicular distance of a point from the plane
 * is {@code Ax + By + D} in the units {@code x} and {@code x} are expressed in.
 */
public class Plane2d {
    private double m_A;
    private double m_B;
    private double m_D;

    /**
     * Instantiate a 2D plane in the form {@code Ax + By + D = 0}. The A and B coefficients will be normalized.
     *
     * @param A The A coefficient of the plane.
     * @param B The B coefficient of the plane.
     * @param D The D coefficient of the plane.
     */
    public Plane2d(double A, double B, double D) {
        setValue(A, B, D);
    }

    public Plane2d setValue(double A, double B, double D) {
        double len = Utl.length(A, B);
        m_A = A / len;
        m_B = B / len;
        m_D = D / len;
        return this;
    }
    /**
     * Test whether a point is 'inside' the plane with a tolerance on what is considered inside. Note that (B,A)
     *
     * @param pt The point to be tested.
     * @param tolerance The tolerance for being considered 'inside'. A positive tolerance is the extra distance from
     *                  the plane towards the inside. This is useful, for example, when testing for all of the points
     *                  of the robot being inside the field and wanting a margin of error of several cm because of
     *                  the uncertainty in the robot position. A positive tolerance gives you an area outside the
     *                  plane which is 'close enough' to be considered in.
     * @return {@code true} if the point is 'inside' the plane and {@code false} if the point is 'outside' the plane.
     */
    public boolean isIn(@NotNull Point2D pt, double tolerance) {
        return ((m_A * pt.getX()) + (m_B * pt.getY()) + m_D) < -tolerance;
    }

    /**
     * Test whether a point is 'on' the plane, within a specified tolerance.
     *
     * @param pt The point to be tested.
     * @param tolerance The distance to either side of the plane that is considered 'on' the plane,
     * @return {@code true} if the point is on the plane and {@code false} otherwise.
     */
    public boolean isOn(@NotNull Point2D pt, double tolerance) {
        return Math.abs((m_A * pt.getX()) + (m_B * pt.getY()) + m_D) <= tolerance;

    }

    /**
     * Compute the distance between a 2d plane and a 2d point.
     *
     * @param pt The point for which the distance will be computed.
     * @return The distance from the plane to the point. If positive the point is to the 'outside' of the plane.
     */
    public double distance(@NotNull Point2D pt) {
        return (m_A * pt.getX()) + (m_B * pt.getY()) + m_D;
    }
}
