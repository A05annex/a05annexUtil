package org.a05annex.util.geo2d;

import org.a05annex.util.Utl;
import org.jetbrains.annotations.NotNull;

/**
 * The description of a 2 dimensional vector represented by double values.
 */
public class Vector2d {
    // A really small tolerance to prevent divide by zero issues
    public static final double ZERO_TOLERANCE = 0.00001;

    static public final int VECTOR_ADD = 0;
    static public final int VECTOR_SUBTRACT = 1;

    private double m_i;
    private double m_j;

    /**
     * Instantiate a vector from a start location to an end location.
     *
     * @param xStart The X start location.
     * @param yStart The Y start location.
     * @param xEnd   The X end location.
     * @param yEnd   The Y end location.
     */
    public Vector2d(double xStart, double yStart, double xEnd, double yEnd) {
        m_i = xEnd - xStart;
        m_j = yEnd - yStart;
    }

    /**
     * Instantiate a vector given the X and Y lengths of the vector.
     *
     * @param dX The X length of the vector.
     * @param dY The Y length of the vector.
     */
    public Vector2d(double dX, double dY) {
        m_i = dX;
        m_j = dY;
    }

    /**
     * Instantiate a vector that is the sum or difference of two vectors.
     *
     * @param v1       The initial vector.
     * @param v2       The vector to be added or subtracted.
     * @param vectorOp Either {@link #VECTOR_ADD}, or {@link #VECTOR_SUBTRACT} to specify vector addition or
     *                 vector subtraction respectively.
     */
    public Vector2d(@NotNull Vector2d v1, @NotNull Vector2d v2, int vectorOp) {
        switch (vectorOp) {
            case VECTOR_ADD:
                m_i = v1.m_i + v2.m_i;
                m_j = v1.m_j + v2.m_j;
                break;
            case VECTOR_SUBTRACT:
                m_i = v2.m_i - v1.m_i;
                m_j = v2.m_j - v1.m_j;
                break;
        }
    }

    /**
     * Get the i component of the vector.
     *
     * @return The i component of the vector.
     */
    public double getI() {
        return m_i;
    }

    /**
     * Get the j component of the vector.
     *
     * @return The j component of the vector.
     */
    public double getJ() {
        return m_j;
    }

    /**
     * Normalize this vector - which means scale the vector so int is a unit vector (its length is {@code 1.0}).
     *
     * @return This vector after normalization.
     */
    public Vector2d normalize() {
        double length = length();
        return scale( length > ZERO_TOLERANCE ? 1.0 / length() : 0.0);
    }

    /**
     * Get the length of this vector.
     *
     * @return The length of this vector.
     */
    public double length() {
        return Utl.length(m_i, m_j);
    }

    /**
     * Get the dot product between this vector and some other vector. If both vectors are unit vectors
     * (normalized vectors) then the dot product is the cosine of the angle between the vectors.
     *
     * @param v The other vector.
     * @return The dot product between this vector and the other vector.
     */
    public double dot(@NotNull Vector2d v) {
        return (m_i * v.m_i) + (m_j * v.m_j);
    }

    /**
     * Scale this vector by some multiplier. This multiplies the i and j components of the vector.
     *
     * @param scale The scale multiplier
     * @return This vector after it has been scaled.
     */
    public Vector2d scale(double scale) {
        m_i *= scale;
        m_j *= scale;
        return this;
    }

    /**
     * Negate, or reverse the direction of, the vector.
     *
     * @return The negated (reversed) vector.
     */
    @NotNull
    public Vector2d negate() {
        return scale(-1.0);
    }
}
