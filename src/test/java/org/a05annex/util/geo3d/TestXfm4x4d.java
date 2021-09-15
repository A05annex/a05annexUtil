package org.a05annex.util.geo3d;

import org.a05annex.util.AngleD;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class TestXfm4x4d {
    /**
     * Get a new 3D transform, it should be initialized to an identity matrix.
     */
    @Test
    @DisplayName("test init to identity")
    void testInitIdentity() {
        Xfm4x4d xfm = new Xfm4x4d();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                assertEquals( (row == col) ? 1.0f : 0.0f,xfm.get(row,col));
            }
        }
        _testUnitVectors(xfm);
        _testInverse(xfm);
    }

    /**
     *
     */
    @Test
    @DisplayName("test translate")
    void testTranslate() {
        Xfm4x4d xfm = new Xfm4x4d().translate(1.0f, 2.0f, 3.0f);
        assertEquals( 1.0f, xfm.get(0,3));
        assertEquals( 2.0f, xfm.get(1,3));
        assertEquals( 3.0f, xfm.get(2,3));
        _testUnitVectors(xfm);
        _testInverse(xfm);
    }

    /**
     *
     */
    @Test
    @DisplayName("test translate a translate")
    void testTranslatetranslate() {
        Xfm4x4d xfm = new Xfm4x4d().translate(1.0, 2.0, 3.0).translate(1.0, 2.0, 3.0);
        assertEquals( 2.0, xfm.get(0,3));
        assertEquals( 4.0, xfm.get(1,3));
        assertEquals( 6.0, xfm.get(2,3));
        _testUnitVectors(xfm);
        _testInverse(xfm);
    }

    @Test
    @DisplayName("test X rotation")
    void testRotateX() {
        Xfm4x4d xfm = new Xfm4x4d().rotate(Xfm4x4d.AXIS_X, new AngleD(AngleD.DEGREES,45.0));
        _testUnitVectors(xfm);
        _testInverse(xfm);
    }

    @Test
    @DisplayName("test Y rotation")
    void testRotateY() {
        Xfm4x4d xfm = new Xfm4x4d().rotate(Xfm4x4d.AXIS_Y, new AngleD(AngleD.DEGREES,45.0));
        _testUnitVectors(xfm);
        _testInverse(xfm);
    }

    @Test
    @DisplayName("test Z rotation")
    void testRotateZ() {
        Xfm4x4d xfm = new Xfm4x4d().rotate(Xfm4x4d.AXIS_Z, new AngleD(AngleD.DEGREES,45.0));
        _testUnitVectors(xfm);
        _testInverse(xfm);
    }

    // =============================================================================================================================
    // Rotation/translation transformation testing.
    // -----------------------------------------------------------------------------------------------------------------------------
    // Rotation/translation is a special case of affine transformation that preserves lengths and angles. Al long as we are using
    // only translation/rotation transformations our angles and lengths are also invariant. Adding shear and scale screws up angles
    // and lengths making ray tracing unpredictable. So if we have a set of reference unit vectors, they should stay unit vectors.
    // Dot products and cross products should be the same after transformation (within the zero tolerance).
    static Vector3d[] _testVectors = {
            new Vector3d(1.0f,0.0f,0.0f),
            new Vector3d(0.0f,1.0f,0.0f),
            new Vector3d(0.0f,0.0f,1.0f),
            new Vector3d(Math.sqrt(1.0f/3.0f),Math.sqrt(1.0f/3.0f),Math.sqrt(1.0f/3.0f))
    };

    static double[] _testdotProducts = {
            _testVectors[0].dot(_testVectors[3]),
            _testVectors[1].dot(_testVectors[3]),
            _testVectors[2].dot(_testVectors[3])
    };

    static Vector3d[] _xfmTestVectors = {
            new Vector3d(),
            new Vector3d(),
            new Vector3d(),
            new Vector3d()
    };

    static Vector3d[] _backXfmTestVectors = {
            new Vector3d(),
            new Vector3d(),
            new Vector3d(),
            new Vector3d()
    };

    /**
     *
     * This tests a set of unit vectors to make sure they are still unit vectors after transform. NOTE: scale and shear
     * transformations do not preserve unit vectors.
     * @param xfm (Xfm4x4d) The transformation to be tested.
     */
    static private void _testUnitVectors(Xfm4x4d xfm) {
        // transform the vectors
        xfm.transform(_testVectors, _xfmTestVectors);
        // test that they are still unit vectors
        for (int i = 0; i < _xfmTestVectors.length; i++) {
            double length = _xfmTestVectors[i].getLength();
            if (!PackageConstants.isZero(length - 1.0f)) {
                fail("expected unit vector after transformation, expected 1.000, but length was: " + length);
            }
        }
        for (int i = 0; i < _testdotProducts.length; i++) {
            double testDot = _testVectors[i].dot(_testVectors[3]);
            if (!PackageConstants.isZero(_testdotProducts[i] - testDot)) {
                fail("unexpected dot product after transformation, expected " + _testdotProducts[i]
                        + " but dot was: " + testDot);
            }
        }
    }

    static Xfm4x4d _inverseXfmForTest = new Xfm4x4d();


    static private void _testInverse(Xfm4x4d xfm) {
        // invert the transform, test transforming the unit vectors through the inverse
        _inverseXfmForTest.invert(xfm);
        _testUnitVectors(_inverseXfmForTest);
        // OK, now back transform and verify the back-transformed vectors are the same as the original vectors.
        xfm.transform(_xfmTestVectors, _backXfmTestVectors);
        for (int i=0; i < _backXfmTestVectors.length; i++) {
            _vectorEquals(_testVectors[i], _backXfmTestVectors[i]);
        }

    }

    static private void _vectorEquals(Vector3d expected, Vector3d actual) {
        if (!PackageConstants.isZero(expected.i - actual.i) &&
                !PackageConstants.isZero(expected.j - actual.j) &&
                !PackageConstants.isZero(expected.k - actual.k)) {
            fail("unexpected unequal vector (" + actual.i + "," + actual.j + "," + actual.k +
                    ") is not equal to the expected vector (" + expected.i + "," + expected.j + "," + expected.k + ").");

        }
    }
}
