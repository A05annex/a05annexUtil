package org.a05annex.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a set of tests for the methods in {@link Utl}.
 */
@RunWith(JUnitPlatform.class)
public class TestUtil {

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for the length(...) method, test different numbers of arguments and reporting length per documentation.
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Test length 0")
    void test_length_0() {
        assertEquals(0.0,Utl.length());
    }

    @Test
    @DisplayName("Test length 1")
    void test_length_1() {
        assertEquals(Math.sqrt(1.0),Utl.length(1.0));
    }

    @Test
    @DisplayName("Test length 2")
    void test_length_2() {
        assertEquals(Math.sqrt(2.0),Utl.length(1.0, 1.0));
    }

    @Test
    @DisplayName("Test length 3")
    void test_length_3() {
        assertEquals(Math.sqrt(3.0),Utl.length(1.0, 1.0, 1.0));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for the max(...) method, test different numbers of arguments and reporting max per documentation.
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Test max 0")
    void test_max_0() {
        assertEquals(Double.NEGATIVE_INFINITY,Utl.max());
    }

    @Test
    @DisplayName("Test max 1")
    void test_max_1() {
        assertEquals(4.0,Utl.max(4.0));
    }

    @Test
    @DisplayName("Test max 2")
    void test_max_2() {
        assertEquals(12.0,Utl.max(4.0, 12.0));
        assertEquals(12.0,Utl.max(12.0, 4.0));
    }

    @Test
    @DisplayName("Test max 3")
    void test_max_3() {
        assertEquals(12.0,Utl.max(-1.0, 4.0, 12.0));
        assertEquals(12.0,Utl.max(12.0, -1.0, 4.0));
        assertEquals(12.0,Utl.max(4.0, 12.0, -1.0));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for the min(...) method, test different numbers of arguments and reporting min per documentation.
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Test min 0")
    void test_min_0() {
        assertEquals(Double.POSITIVE_INFINITY,Utl.min());
    }

    @Test
    @DisplayName("Test min 1")
    void test_min_1() {
        assertEquals(4.0,Utl.min(4.0));
    }

    @Test
    @DisplayName("Test min 2")
    void test_min_2() {
        assertEquals(4.0,Utl.min(4.0, 12.0));
        assertEquals(4.0,Utl.min(12.0, 4.0));
    }

    @Test
    @DisplayName("Test min 3")
    void test_min_3() {
        assertEquals(-1.0,Utl.min(-1.0, 4.0, 12.0));
        assertEquals(-1.0,Utl.min(12.0, -1.0, 4.0));
        assertEquals(-1.0,Utl.min(12.0, 4.0, -1.0));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for the clip(...) method, test it performs as documented
    // -----------------------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("Test clip invalid args")
    void test_clip_invalid_args() {
        assertThrows(IllegalArgumentException.class,
                () -> Utl.clip(5.0, 10.0, 9.0));
    }

    @Test
    @DisplayName("Test clip in range")
    void test_clip_in_range() {
        assertEquals(-9.99999, Utl.clip(-9.99999, -10.0, 10.0));
        assertEquals(0.0, Utl.clip(0.0, -10.0, 10.0));
        assertEquals(9.99999, Utl.clip(9.99999, -10.0, 10.0));
    }

    @Test
    @DisplayName("Test clip min")
    void test_clip_min() {
        assertEquals(-10.0, Utl.clip(-10.00001, -10.0, 10.0));
    }

    @Test
    @DisplayName("Test clip max")
    void test_clip_max() {
        assertEquals(10.0, Utl.clip(10.00001, -10.0, 10.0));
    }

    @Test
    @DisplayName("Test clip no min")
    void test_clip_no_min() {
        assertEquals(-10.0, Utl.clip(-Double.MAX_VALUE, -10.0, 10.0));
        assertEquals(-Double.MAX_VALUE, Utl.clip(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 10.0));
    }

    @Test
    @DisplayName("Test clip no max")
    void test_clip_no_max() {
        assertEquals(10.0, Utl.clip(Double.MAX_VALUE, -10.0, 10.0));
        assertEquals(Double.MAX_VALUE, Utl.clip(Double.MAX_VALUE, -10.0, Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Test inTolerance true")
    public void testInToleranceTrue() {
        // Test when the value is within the tolerance
        assertTrue(Utl.inTolerance(5.0, 4.9, 0.1));
        assertTrue(Utl.inTolerance(10.0, 10.05, 0.1));
        assertTrue(Utl.inTolerance(3.0, 2.95, 0.1));
    }

    @Test
    @DisplayName("Test inTolerance false")
    public void testInToleranceFalse() {
        // Test when the value is not within the tolerance
        assertFalse(Utl.inTolerance(5.0, 4.9, 0.05));
        assertFalse(Utl.inTolerance(10.0, 10.05, 0.01));
        assertFalse(Utl.inTolerance(3.0, 2.95, 0.001));
    }

    @Test
    @DisplayName("Test inTolerance with 0 tolerance")
    public void testInToleranceZeroTolerance() {
        // Test when the tolerance is zero
        assertFalse(Utl.inTolerance(5.0, 4.9, 0.0));
        assertFalse(Utl.inTolerance(10.0, 10.05, 0.0));
        assertFalse(Utl.inTolerance(3.0, 2.95, 0.0));
        assertTrue(Utl.inTolerance(10.01, 10.01, 0.0));
    }

    @Test
    @DisplayName("Test inTolerance with negative values")
    public void testInToleranceNegativeValues() {
        // Test when the value and/or target are negative
        assertTrue(Utl.inTolerance(-5.0, -4.9, 0.1));
        assertTrue(Utl.inTolerance(-10.0, -10.05, 0.1));
        assertTrue(Utl.inTolerance(-3.0, -2.95, 0.1));
    }
}
