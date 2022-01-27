package org.a05annex.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class TestAngle {

    private final double tolerance = 0.00000001;

    static final AngleConstantD PI_OVER_4 = new AngleConstantD(AngleUnit.RADIANS, Math.PI / 4.0);
    static final AngleConstantD NEG_PI_OVER_4 = new AngleConstantD(AngleUnit.RADIANS, -(Math.PI / 4.0));

    /**
     * This tests that the constant angles are set as expected, when writing this we also tested that
     * we could not call a setValue() method on constant angles
     */
    @Test
    @DisplayName("test the predefined constant angles")
    void testConstantAngles() {
        // in radians
        assertEquals(0.0, AngleD.ZERO.getRadians());
        assertEquals(Math.PI, AngleD.PI.getRadians());
        assertEquals(2.0 * Math.PI, AngleD.TWO_PI.getRadians());
        assertEquals(Math.PI / 2.0, AngleD.PI_OVER_2.getRadians());
        assertEquals(-Math.PI, AngleD.NEG_PI.getRadians());
        assertEquals(-(2.0 * Math.PI), AngleD.NEG_TWO_PI.getRadians());
        assertEquals(-(Math.PI / 2.0), AngleD.NEG_PI_OVER_2.getRadians());
        // in degrees
        assertEquals(Math.PI, AngleD.DEG_180.getRadians());
        assertEquals(2.0 * Math.PI, AngleD.DEG_360.getRadians());
        assertEquals(Math.PI / 2.0, AngleD.DEG_90.getRadians());
        assertEquals(-Math.PI, AngleD.DEG_NEG_180.getRadians());
        assertEquals(-(2.0 * Math.PI), AngleD.DEG_NEG_360.getRadians());
        assertEquals(-(Math.PI / 2.0), AngleD.DEG_NEG_90.getRadians());
        // in degrees as degrees
        assertEquals(0.0, AngleD.ZERO.getDegrees());
        assertEquals(180.0, AngleD.DEG_180.getDegrees());
        assertEquals(360.0, AngleD.DEG_360.getDegrees());
        assertEquals(90.0, AngleD.DEG_90.getDegrees());
        assertEquals(-180.0, AngleD.DEG_NEG_180.getDegrees());
        assertEquals(-360.0, AngleD.DEG_NEG_360.getDegrees());
        assertEquals(-90.0, AngleD.DEG_NEG_90.getDegrees());
    }

    /**
     * Tests the {@link AngleConstantD#cos()} method.
     */
    @Test
    @DisplayName("test AngleConstantD.cos()")
    void testCos() {
        assertEquals(1.0, new AngleD(AngleUnit.RADIANS, 0.0).cos(), tolerance);
        assertEquals(0.0, AngleD.PI_OVER_2.cos(), tolerance);
        assertEquals(-1.0, AngleD.PI.cos(), tolerance);
    }

    /**
     * Tests the {@link AngleConstantD#sin()} method.
     */
    @Test
    @DisplayName("test AngleConstantD.sin()")
    void testSin() {
        assertEquals(0.0, new AngleD(AngleUnit.RADIANS, 0.0).sin(), tolerance);
        assertEquals(1.0, AngleD.PI_OVER_2.sin(), tolerance);
        assertEquals(0.0, AngleD.PI.sin(), tolerance);
    }

    /**
     * Tests the {@link AngleConstantD#sin()} method.
     */
    @Test
    @DisplayName("test AngleConstantD.tan()")
    void testTan() {
        assertEquals(0.0, new AngleD(AngleUnit.RADIANS, 0.0).tan(), tolerance);
        assertEquals(1.0, PI_OVER_4.tan(), tolerance);
        // OK, at 90 deg this is a divide by zeros, so it is really not a number - don't test this
        // assertEquals( 1.0, AngleD.PI_OVER_2.tan(),tolerance);
        assertEquals(-1.0, NEG_PI_OVER_4.tan(), tolerance);
        assertEquals(0.0, AngleD.PI.tan(), tolerance);
    }

    /**
     * Tests the {@link AngleConstantD#isLessThan(AngleConstantD)} method.
     */
    @Test
    @DisplayName("test AngleConstantD.isLessThan()")
    void testIsLessThan() {
        assertTrue(new AngleD(AngleUnit.RADIANS, 0.0).isLessThan(AngleD.PI));
        assertTrue(AngleD.PI.isLessThan(AngleD.TWO_PI));
        assertFalse(AngleD.PI.isLessThan(AngleD.PI));
        assertFalse(AngleD.PI.isLessThan(AngleD.NEG_PI));
    }

    /**
     * Tests the {@link AngleConstantD#isLessThanOrEqual(AngleConstantD)} method.
     */
    @Test
    @DisplayName("test AngleConstantD.isLessThanOrEqual()")
    void testIsLessThanOrEqual() {
        assertTrue(new AngleD(AngleUnit.RADIANS, 0.0).isLessThanOrEqual(AngleD.PI));
        assertTrue(AngleD.PI.isLessThanOrEqual(AngleD.TWO_PI));
        assertTrue(AngleD.PI.isLessThanOrEqual(AngleD.PI));
        assertFalse(AngleD.PI.isLessThanOrEqual(AngleD.NEG_PI));
    }

    /**
     * Tests the {@link AngleConstantD#isGreaterThan(AngleConstantD)} method.
     */
    @Test
    @DisplayName("test AngleConstantD.isGreaterThan()")
    void testIsGreaterThan() {
        assertFalse(new AngleD(AngleUnit.RADIANS, 0.0).isGreaterThan(AngleD.PI));
        assertFalse(AngleD.PI.isGreaterThan(AngleD.TWO_PI));
        assertFalse(AngleD.PI.isGreaterThan(AngleD.PI));
        assertTrue(AngleD.PI.isGreaterThan(AngleD.NEG_PI));
    }

    /**
     * Tests the {@link AngleConstantD#isGreaterThanOrEqual(AngleConstantD)} method.
     */
    @Test
    @DisplayName("test AngleConstantD.isGreaterThanOrEqual()")
    void testIsGreaterThanOrEqual() {
        assertFalse(new AngleD(AngleUnit.RADIANS, 0.0).isGreaterThanOrEqual(AngleD.PI));
        assertFalse(AngleD.PI.isGreaterThanOrEqual(AngleD.TWO_PI));
        assertTrue(AngleD.PI.isGreaterThanOrEqual(AngleD.PI));
        assertTrue(AngleD.PI.isGreaterThanOrEqual(AngleD.NEG_PI));
    }

    /**
     * Tests the {@link AngleConstantD#equals(AngleConstantD)} method.
     */
    @Test
    @DisplayName("test AngleConstantD.equals()")
    void testEquals() {
        assertTrue(new AngleD(AngleUnit.RADIANS, Math.PI).equals(AngleD.PI));
        assertTrue(new AngleD(AngleUnit.RADIANS, Math.PI).equals((Object) AngleD.PI));
        assertTrue(AngleD.PI.equals(AngleD.PI));
        assertTrue(AngleD.PI.equals(new AngleD(AngleUnit.RADIANS, Math.PI)));
        assertTrue(AngleD.PI.equals((Object) (new AngleD(AngleUnit.RADIANS, Math.PI))));

        assertFalse(new AngleD(AngleUnit.RADIANS, Math.PI).equals(AngleD.TWO_PI));
        assertFalse(new AngleD(AngleUnit.RADIANS, Math.PI).equals((Object) AngleD.TWO_PI));
        assertFalse(AngleD.PI.equals(AngleD.TWO_PI));
        assertFalse(AngleD.TWO_PI.equals(new AngleD(AngleUnit.RADIANS, Math.PI)));
        assertFalse(AngleD.TWO_PI.equals((Object) (new AngleD(AngleUnit.RADIANS, Math.PI))));

        assertFalse(AngleD.TWO_PI.equals((Object) ("not an angle")));

    }
    // -----------------------------------------------------------------------------------------------------------------
    // it this point we have tested all the constant angle stuff
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests the {@link AngleD#mult(double)}, {@link AngleD#mult(double)}, {@link AngleD#mult(double)},
     * and {@link AngleD#mult(double)} methods
     */
    @Test
    @DisplayName("test AngleD math")
    void testMath() {
        assertEquals(new AngleD(AngleD.PI).mult(2.0), AngleD.TWO_PI);
        assertEquals(new AngleD(AngleD.PI).divide(2.0), AngleD.PI_OVER_2);
        assertEquals(new AngleD(AngleD.PI).add(AngleD.PI), AngleD.TWO_PI);
        assertEquals(new AngleD(AngleD.PI).add(AngleUnit.RADIANS, Math.PI), AngleD.TWO_PI);
        assertEquals(new AngleD(AngleD.PI).add(AngleUnit.DEGREES, 180.0), AngleD.TWO_PI);
        assertEquals(new AngleD(AngleD.TWO_PI).subtract(AngleD.PI), AngleD.PI);
        assertEquals(new AngleD(AngleD.TWO_PI).subtract(AngleUnit.RADIANS, Math.PI), AngleD.PI);
        assertEquals(new AngleD(AngleD.TWO_PI).subtract(AngleUnit.DEGREES, 180.0), AngleD.PI);
    }

    /**
     * Tests the arc trig functions for initializing the value of the angle. Specifically this
     * tests the {@link AngleD#atan(double)}, {@link AngleD#atan2(double, double)},
     * {@link AngleD#asin(double)}, and {@link AngleD#acos(double)} methods
     */
    @Test
    @DisplayName("test AngleD ATrig")
    void testATrig() {
        assertEquals(PI_OVER_4, new AngleD().atan(1.0));
        assertEquals(new AngleD(AngleUnit.RADIANS, 0.0), new AngleD().atan(0.0));
        assertEquals(PI_OVER_4, new AngleD().atan2(5.0, 5.0));
        assertEquals(new AngleD(AngleUnit.RADIANS, 0.0), new AngleD().atan2(0.0, 12.0));
        assertEquals(AngleD.PI_OVER_2, new AngleD().atan2(3.0, 0.0));
        assertEquals(AngleD.PI, new AngleD().atan2(0.0, -12.0));

        assertEquals(new AngleD(AngleUnit.RADIANS, 0.0), new AngleD().asin(0.0));
        assertEquals(AngleD.PI_OVER_2, new AngleD().asin(1.0));

        assertEquals(new AngleD(AngleUnit.RADIANS, 0.0), new AngleD().acos(1.0));
        assertEquals(AngleD.PI_OVER_2, new AngleD().acos(0.0));
    }

    /**
     * Tests setters for the angle. Specifically this
     * tests the {@link AngleD#setDegrees(double)}, {@link AngleD#setRadians(double)},
     * {@link AngleD#setValue(AngleUnit, double)}, and {@link AngleD#setValue(AngleConstantD)} methods
     */
    @Test
    @DisplayName("test AngleD setters")
    void testSetters() {
        assertEquals(0.0, new AngleD().setValue(AngleUnit.RADIANS, 0.0).getRadians());
        assertEquals(0.0, new AngleD().setValue(AngleUnit.DEGREES, 0.0).getDegrees());
        assertEquals(0.0, new AngleD().setValue(AngleUnit.DEGREES, 0.0).getRadians());
        assertEquals(0.0, new AngleD().setValue(AngleUnit.RADIANS, 0.0).getDegrees());
        assertEquals(0.0, new AngleD().setRadians(0.0).getRadians());
        assertEquals(0.0, new AngleD().setDegrees(0.0).getDegrees());
        assertEquals(0.0, new AngleD().setDegrees(0.0).getRadians());
        assertEquals(0.0, new AngleD().setRadians(0.0).getDegrees());

        assertEquals(Math.PI, new AngleD().setValue(AngleUnit.RADIANS, Math.PI).getRadians());
        assertEquals(Math.PI, new AngleD().setValue(AngleD.PI).getRadians());
        assertEquals(180.0, new AngleD().setValue(AngleUnit.DEGREES, 180.0).getDegrees());
        assertEquals(Math.PI, new AngleD().setValue(AngleUnit.DEGREES, 180.0).getRadians());
        assertEquals(180.0, new AngleD().setValue(AngleUnit.RADIANS, Math.PI).getDegrees());
        assertEquals(Math.PI, new AngleD().setRadians(Math.PI).getRadians());
        assertEquals(180.0, new AngleD().setDegrees(180.0).getDegrees());
        assertEquals(Math.PI, new AngleD().setDegrees(180.0).getRadians());
        assertEquals(180.0, new AngleD().setRadians(Math.PI).getDegrees());

    }

    /**
     * Tests the clone methods {@link AngleD#clone()} and {@link AngleD#cloneAngleD()}.
     *
     * @throws CloneNotSupportedException {@link AngleD#clone()} may, by {@link Cloneable} interface
     *                                    definition, throw this exception, however it is a code failure if it does.
     */
    @Test
    @DisplayName("test AngleD setters")
    void testClone() throws CloneNotSupportedException {
        AngleD pi = new AngleD(AngleUnit.RADIANS, Math.PI);
        AngleD pi_cloneAngle = pi.cloneAngleD();
        AngleD pi_clone = (AngleD) pi.clone();
        assertNotSame(pi, pi_cloneAngle);
        assertNotSame(pi, pi_clone);
        assertNotSame(pi_cloneAngle, pi_clone);
        assertTrue(pi.equals(pi_cloneAngle));
        assertTrue(pi.equals(pi_clone));
        assertTrue(pi_cloneAngle.equals(pi_clone));
    }
}
