package org.a05annex.util;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
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

}
