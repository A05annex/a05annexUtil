package org.a05annex.util.geo2d;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnitPlatform.class)
public class TestPlane2d {

    static private double IS_ON_TOLERANCE = 0.0000001;
    static private double IS_IN_TOLERANCE = 0.05;

    // a plane parallel to X passing through y=5 with outside when y > 5
    private final Plane2d m_x5 = new Plane2d(0.0, 1.0, -5.0);
    // a plane parallel to Y passing through x=5 with outside when x > 5
    private final Plane2d m_y5 = new Plane2d(1.0, 0.0, -5.0);
    // a plane at 45deg, using A,B = (1,1) so it requires normalization should pass through x,y = (1,1)
    private final Plane2d m_at45deg = new Plane2d(1.0, 1.0, -Math.sqrt(2.0));

    /**
     * Test that the planes are the expected distance from the origin
     */
    @Test
    @DisplayName("Test distance to origin")
    void test_distance_origin() {
        Point2D testPt = new Point2D.Double(0.0, 0.0);
        assertEquals(-5.0, m_x5.distance(testPt));
        assertEquals(-5.0, m_y5.distance(testPt));
        assertEquals(-1.0, m_at45deg.distance(testPt));
    }

    /**
     * Follow the direction vector from the origin the distance found in the last test
     * to create a point the closest point to the origin on each of the lines. Test that
     * they are on the lines.
     */
    @Test
    @DisplayName("Test plane isOn")
    void test_isOn() {
        assertTrue(m_x5.isOn(new Point2D.Double(0.0, 5.0), IS_ON_TOLERANCE));
        assertTrue(m_y5.isOn(new Point2D.Double(5.0, 0.0), IS_ON_TOLERANCE));
        assertTrue(m_at45deg.isOn(new Point2D.Double(Math.sqrt(2.0)/2.0,
                Math.sqrt(2.0)/2.0), IS_ON_TOLERANCE));
    }

    /**
     * This is a test of points that are on the planes kake sure they do not test as being inside the planes
     * when there is a positive tolerance.
     */
    @Test
    @DisplayName("Test plane isIn 1")
    void test_isIn_1() {
        assertFalse(m_x5.isIn(new Point2D.Double(0.0, 5.0), IS_IN_TOLERANCE));
        assertFalse(m_y5.isIn(new Point2D.Double(5.0, 0.0), IS_IN_TOLERANCE));
        assertFalse(m_at45deg.isIn(new Point2D.Double(Math.sqrt(2.0)/2.0,
                Math.sqrt(2.0)/2.0), IS_IN_TOLERANCE));
    }

    /**
     * Move the point just inside the IS_IN_TOLERANCE
     */
    @Test
    @DisplayName("Test plane isIn 2")
    void test_isIn_2() {
        assertTrue(m_x5.isIn(new Point2D.Double(0.0, 5.0 - (1.01 * IS_IN_TOLERANCE)), IS_IN_TOLERANCE));
        assertTrue(m_y5.isIn(new Point2D.Double(5.0 - (1.01 * IS_IN_TOLERANCE), 0.0), IS_IN_TOLERANCE));
        assertTrue(m_at45deg.isIn(new Point2D.Double(((1.0 - (1.01 * IS_IN_TOLERANCE)) * Math.sqrt(2.0))/2.0,
                ((1.0 - (1.01 * IS_IN_TOLERANCE)) * Math.sqrt(2.0))/2.0), IS_IN_TOLERANCE));
    }

    /**
     * Move the point just outside the IS_IN_TOLERANCE
     */
    @Test
    @DisplayName("Test plane isIn 3")
    void test_isIn_3() {
        assertFalse(m_x5.isIn(new Point2D.Double(0.0, 5.0 - (0.99 * IS_IN_TOLERANCE)), IS_IN_TOLERANCE));
        assertFalse(m_y5.isIn(new Point2D.Double(5.0 - (0.99 * IS_IN_TOLERANCE), 0.0), IS_IN_TOLERANCE));
        assertFalse(m_at45deg.isIn(new Point2D.Double(((1.0 - (0.99 * IS_IN_TOLERANCE)) * Math.sqrt(2.0))/2.0,
                ((1.0 - (0.99 * IS_IN_TOLERANCE)) * Math.sqrt(2.0))/2.0), IS_IN_TOLERANCE));
    }
}
