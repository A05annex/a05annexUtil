package org.a05annex.util.geo3d;

import org.a05annex.util.AngleD;
import org.a05annex.util.AngleUnit;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import java.util.StringTokenizer;

/**
 * A class representing a
 * transformation (Xfm) in 3D of double precision (the components are represented by <code>double</code> values),
 * hence the name <code>Xfm4x4d</code>.
 * <p>
 * This class implements the basic functionality for a 3D transformation required for robotics use.
 * <p>
 * Point transformation is of the form:<br><br>
 * <pre>
 *     [ xfm[0][0] xfm[0][1] xfm[0][2] xfm[0][3] ] [ X ]   [ Xt ]
 *     | xfm[1][0] xfm[1][1] xfm[1][2] xfm[1][3] | | Y | = | Yt |
 *     | xfm[2][0] xfm[2][1] xfm[2][2] xfm[2][3] | | Z |   | Zt |
 *     [ xfm[3][0] xfm[3][1] xfm[3][2] xfm[3][3] ] [ H ]   [ Ht ]
 * </pre>
 * Where <code>[X,Y,Z,H]</code> is the un-transformed point or vector and <code>[Xt,Yt,Zt,Ht]</code> is the transformed point or
 * vector.  Normally, <code>H = 1</code> for a point transformation and <code>H = 0</code> for a vector transformation.
 */

public class Xfm4x4d {

    // the axis for rotation
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    private double[][] xfm = new double[4][4];

    /**
     * Creates a new instance of <code>Xfm4x4d</code> that is initialized to an identity.
     */
    public Xfm4x4d() {
        this.identity();
    }

    /**
     * Creates a new instance of <code>Xfm4x4d</code> that is initialized to another transform.
     *
     * @param xfmInit The trans formation this transformation should be set equal to.
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Xfm4x4d(final Xfm4x4d xfmInit) {
        this.setValue(xfmInit);
    }

    /**
     * Sets the value of this transformation to be equal to the value of another transformation.  By setting equal, we mean
     * that each of the 16 terms in the transformation is set equal.
     *
     * @param xfmInit The transformation to set this transformation equal to.
     * @return Returns this transformation after it have been set equal to <code>xfmInit</code>.
     */
    public Xfm4x4d setValue(final Xfm4x4d xfmInit) {
        for (int row = 0; row < 4; row++) {
            System.arraycopy(xfmInit.xfm[row], 0, xfm[row], 0, 4);
        }
        return this;
    }

    public Xfm4x4d setValue(final Point3d ptOrigin, final Point3d ptAimedAt) throws ZeroLengthVectorException {
        identity();
        // set temp vector - from the originr to the aimedAt point
        final Vector3d vTmp = new Vector3d(ptAimedAt.x - ptOrigin.x, ptAimedAt.y - ptOrigin.y, ptAimedAt.z - ptOrigin.z).normalize();
        final AngleD aAzimuth = new AngleD().atan2(-vTmp.i, -vTmp.j);
        final AngleD aAltitude = new AngleD().asin(-vTmp.k);
        final AngleD aRoll = new AngleD(AngleUnit.DEGREES, 0.0f);
        compose(aAzimuth, aAltitude, aRoll, new Vector3d(ptOrigin.x, ptOrigin.y, ptOrigin.z));
        return this;
    }

    /**
     * Get the value of an element of the transformation.
     *
     * @param row (int) The row of the element.
     * @param col (int) The column of the element.
     * @return (double) Returns the element.
     */
    public double get(final int row, final int col) {
        return xfm[row][col];
    }


    /**
     * Sets this transformation to an identity transformation as:<br>
     * <pre>
     *     [ 1 0 0 0 ]
     *     | 0 1 0 0 |
     *     | 0 0 1 0 |
     *     [ 0 0 0 1 ]
     * </pre>
     *
     * @return Returns this transform after it has been set to an identity transform.
     */
    public Xfm4x4d identity() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                xfm[row][col] = (row == col) ? 1.0f : 0.0f;
            }
        }
        return this;
    }

    /**
     * Premultiply this transformation by a translation transformation of the form:
     * <pre>
     *     [ 1 0 0 fTx ]
     *     | 0 1 0 fTy |
     *     | 0 0 1 fTz |
     *     [ 0 0 0  1  ]
     * </pre>
     * <p>
     * If you want to set this transformation to be the translation transformation,
     * set this transformation to be an identity transformation before applying the translate.
     *
     * @param fTx The X translation.
     * @param fTy The Y translation.
     * @param fTz The Z translation.
     * @return Returns this transform after it has been premultiplied by a translation transformation.
     */
    public Xfm4x4d translate(final double fTx, final double fTy, final double fTz) {
        // The revised implementation taking advantage of the form of the transformation matrix to
        //  optimize the operations and do the premultiply locally.
        if (0.0f != xfm[3][0]) {
            xfm[0][0] += xfm[3][0] * fTx;
            xfm[1][0] += xfm[3][0] * fTy;
            xfm[2][0] += xfm[3][0] * fTz;
        }
        if (0.0f != xfm[3][1]) {
            xfm[0][1] += xfm[3][1] * fTx;
            xfm[1][1] += xfm[3][1] * fTy;
            xfm[2][1] += xfm[3][1] * fTz;
        }
        if (0.0f != xfm[3][2]) {
            xfm[0][2] += xfm[3][2] * fTx;
            xfm[1][2] += xfm[3][2] * fTy;
            xfm[2][2] += xfm[3][2] * fTz;
        }
        if (0.0f != xfm[3][3]) {
            xfm[0][3] += xfm[3][3] * fTx;
            xfm[1][3] += xfm[3][3] * fTy;
            xfm[2][3] += xfm[3][3] * fTz;
        }
        return this;
    }

    /**
     * Premultiply this transformation by a translation transformation of the form:
     * <pre>
     *     [ 1 0 0 vTranslate.i ]
     *     | 0 1 0 vTranslate.j |
     *     | 0 0 1 vTranslate.k |
     *     [ 0 0 0      1       ]
     * </pre>
     * <p>
     * If you want to set this transformation to be the translation transformation,
     * set this transformation to be an identity transformation before applying the translate.
     *
     * @param vTranslate The translation vector (direction and magnitude of translation).
     * @return Returns this transform after it has been premultiplied by a translation transformation.
     */
    public Xfm4x4d translate(final Vector3d vTranslate) {
        return translate(vTranslate.i, vTranslate.j, vTranslate.k);
    }

    /**
     * Premultiply this transformation by a scaling transformation of the form:
     * <pre>
     *     [ fSx  0   0  0 ]
     *     |  0  fSy  0  0 |
     *     |  0   0  fSz 0 |
     *     [  0   0   0  1 ]
     * </pre>
     * <p>
     * If you want to set this transformation to be the scaling transformation,
     * set this transformation to be an identity transformation before applying the scale.
     *
     * @param sx The X scale factor.
     * @param sy The Y scale factor.
     * @param sz The Z scale factor.
     * @return Returns this transform after it has been premultiplied by a scaling transformation.
     */
    public Xfm4x4d scale(final double sx, final double sy, final double sz) {
        return preMul(sx, 0.0, 0.0, 0.0, sy, 0.f, 0.0, 0.0, sz);
    }

    /**
     * Premultiply this transformation by a rotation transformation for the specified rotation about the specified axis.
     * This function is a front for the version of {@link Xfm4x4d#rotate(int, AngleD)} that takes
     * the sin and cosine of the rotation angle as arguments.
     * <p>
     * If you want to set this transformation to be the rotation transformation,
     * set this transformation to be an identity transformation before applying the rotation.
     *
     * @param nAxis The axis of rotation: {@link Xfm4x4d#AXIS_X}, {@link Xfm4x4d#AXIS_Y},
     *              or {@link Xfm4x4d#AXIS_Z}
     * @param aRot  The angle of rotation.
     * @return Returns this transform after it has been premultiplied by a rotation transformation.
     */
    public Xfm4x4d rotate(final int nAxis, final AngleD aRot) {
        final double fSin = aRot.sin();
        final double fCos = aRot.cos();
        return rotate(nAxis, fSin, fCos);
    }

    public Xfm4x4d rotate(final int nAxis, final double fSin, final double fCos) {
        switch (nAxis) {
            case AXIS_X:
                return preMul(1.0f, 0.0f, 0.0f, 0.0f, fCos, -fSin, 0.0f, fSin, fCos);
            case AXIS_Y:
                return preMul(fCos, 0.0f, fSin, 0.0f, 1.0f, 0.0f, -fSin, 0.0f, fCos);
            case AXIS_Z:
                return preMul(fCos, -fSin, 0.0f, fSin, fCos, 0.0f, 0.0f, 0.0f, 1.0f);
            default:
                // this is an unknown axis
                throw new IllegalArgumentException("Unrecognised rotation axis specified");
        }
    }

    /**
     * @param vAxis The axis of rotation.
     * @param aRot  The angle of rotation.
     * @return Returns the transformation with the rotation applied.
     */
    public Xfm4x4d rotate(final Vector3d vAxis, final AngleD aRot) {
        // rotation for an arbitrary axis (from Rogers and Adams) -
        //  > Check length of the input vector (0,0,0 vector provides no axis info, and an identity is returned)
        //  > Rotate the axis to be coincident with the Z axis by an i and j rotation.
        //      Apply the rotation in Z
        //      Apply the inverse of the X and Y rotations
        final double length = vAxis.getLength();
        if (PackageConstants.isZero(length)) {
            throw new ZeroLengthVectorException();
        } else {
            // get the rotation into an axis aligned state
            final Xfm4x4d xfmRotIn = new Xfm4x4d().identity();
            final double fxyLen = Math.sqrt((vAxis.j * vAxis.j) + (vAxis.i * vAxis.i));
            if (!PackageConstants.isZero(fxyLen)) {
                xfmRotIn.rotate(AXIS_Z, -(vAxis.j / fxyLen), vAxis.i / fxyLen);
            }
            xfmRotIn.rotate(AXIS_Y, -fxyLen, vAxis.k);
            // get the rotation out of the axis aligned state
            final Xfm4x4d xfmRotOut = new Xfm4x4d(xfmRotIn).invert();    // save and invert to get the rotation out
            //  apply the rotation in the axis aligned state
            xfmRotIn.rotate(AXIS_Z, aRot);
            // now do the multiplications to the current transform
            preMul(xfmRotIn);
            preMul(xfmRotOut);
        }
        return this;
    }

    /**
     * Premultiply this transformation by a shearing transformation of the form:
     * <pre>
     *     [ 1  Kxy Kxz  0 ]
     *     | 0   1  Kyz  0 |
     *     | 0   0   1   0 |
     *     [ 0   0   0   1 ]
     * </pre>
     * <p>
     * If you want to set this transformation to be the shearing transformation,
     * set this transformation to be an identity transformation before applying the shear.
     *
     * @param fShearXY The shear in X as a function of Y (Kxy in the matrix above).  For example a value of 2 means that for
     *                 every unit of displacement Y, the X value is shifted by 2.
     * @param fShearXZ The shear in X as a function of Z (Kxz in the matrix above).  For example a value of 2 means that for
     *                 every unit of displacement Z, the X value is shifted by 2.
     * @param fShearYZ The shear in Y as a function of Z (Kyz in the matrix above).  For example a value of 2 means that for
     *                 every unit of displacement Z, the Y value is shifted by 2.
     * @return Returns this transform after it has been premultiplied by a shearing transformation.
     */
    public Xfm4x4d shear(final double fShearXY, final double fShearXZ, final double fShearYZ) {
        return preMul(1.0f, fShearXY, fShearXZ, 0.0f, 1.0f, fShearYZ, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Build up a transformation that include combined scale, shear, rotation, and translation - in that order.  Rotation is
     * applied as roll (rotation around Z) followed by altitude (rotation about X) followed by azimuth (rotation about Z).  To
     * follow the geographic conventions of azimuth specification, this is a clockwise rotation when looking down the positive
     * Z axis towards the origin in a right handed coordinate system (which is a -azimuth rotation around Z using standard
     * graphics conventions).
     * <p>
     * If a composed transformation is decomposed, the scale, shear, rotation, and translation that create this transformation
     * will be returned.  It may be the case that these vary from the original specification due to numerical and round off
     * error, and that the angles will be set the their -180 degree to +180 degree equivalents.
     *
     * @param fSx       The X scale factor.
     * @param fSy       The Y scale factor.
     * @param fSz       The Z scale factor.
     * @param fKxy      The shear in X as a function of Y.  For example a value of 2 means that for
     *                  every unit of displacement Y, the X value is shifted by 2.
     * @param fKxz      The shear in X as a function of Z.  For example a value of 2 means that for
     *                  every unit of displacement Z, the X value is shifted by 2.
     * @param fKyz      The shear in Y as a function of Z.  For example a value of 2 means that for
     *                  every unit of displacement Z, the Y value is shifted by 2.
     * @param aAzimuth  The azimuth angle.
     * @param aAltitude The altitude angle.
     * @param aRoll     The roll angle.
     * @param vTrans    The translation vector (direction and magnitude of translation).
     * @return Returns this transformation after it has been set as specified.
     */
    public Xfm4x4d compose(final double fSx, final double fSy, final double fSz,
                           final double fKxy, final double fKxz, final double fKyz,
                           final AngleD aAzimuth, final AngleD aAltitude, final AngleD aRoll,
                           final Vector3d vTrans) {
        // this assumes xy is the ground plane and k is up (right handed)
        return identity().scale(fSx, fSy, fSz).shear(fKxy, fKxz, fKyz).
                rotate(AXIS_Y, aRoll).
                rotate(AXIS_X, aAltitude).
                rotate(AXIS_Z, -aAzimuth.sin(), aAzimuth.cos()).
                translate(vTrans);
    }

    /**
     * Build up a rigid body transformation that include combined rotation and translation - in that order.  Rotation is applied
     * as roll (rotation around Z) followed by altitude (rotation about X) followed by azimuth (rotation about Z).  To follow
     * the geographic conventions of azimuth specification, this is a clockwise rotation when looking down the positive Z axis
     * towards the origin in a right handed coordinate system (which is a -azimuth rotation around Z using standard graphics
     * conventions).
     * <p>
     * If a composed transformation is decomposed, the scale, shear, rotation, and translation that create this transformation
     * will be returned.  It may be the case that these vary from the original specification due to numerical and round off
     * error, and that the angles will be set the their -180 degree to +180 degree equivalents.
     *
     * @param aAzimuth  The azimuth angle.
     * @param aAltitude The altitude angle.
     * @param aRoll     The roll angle.
     * @param vTrans    The translation vector (direction and magnitude of translation).
     * @return Returns this transformation after it has been set as specified.
     */
    public Xfm4x4d compose(final AngleD aAzimuth, final AngleD aAltitude, final AngleD aRoll, final Vector3d vTrans) {
        // this assumes xy is the ground plane and k is up (right handed)
        return identity().
                rotate(AXIS_Y, aRoll).
                rotate(AXIS_X, aAltitude).
                rotate(AXIS_Z, -aAzimuth.sin(), aAzimuth.cos()).
                translate(vTrans);
    }

    /**
     * Decompose this transformation into a set of scale, shear, rotate, and translate components that will generate the
     * transformation.  Decomposition and recomposition is a useful operation set when trying to reduce numerical errors
     * accumulated during interactive manipulations that repeatedly pre-multiply the transformation by incremental
     * repositioning transformations.
     * <p>
     * NOTE: the use of tuples for returning the decomposition is not my favorite choice, but it allows you to get the
     * values in whatever form is most convenient for future manipulation.
     *
     * @param scale     The Sx, Sy, Sz scaling as the i, j, k fields of the tuple.
     * @param shear     The XY, XZ, YZ sharing as the i, j, k fields of the tuple.
     * @param translate The Tx, Ty, Tz translation as the i, j, k fields of the tuple.
     * @param aAzimuth  The azimuth (plan angle).
     * @param aAltitude The altitude angle.
     * @param aRoll     The roll angle.
     * @throws ZeroLengthVectorException Thrown if the transformation is singular and cannot be uniquely decomposed.
     */
    public void decompose(final Vector3d scale, final Vector3d shear, final Vector3d translate, final AngleD aAzimuth,
                          final AngleD aAltitude, final AngleD aRoll) throws ZeroLengthVectorException {
        final double sx;
        final double sy;
        final double sz;
        // first back out the translation part of the transform
        translate.i = xfm[0][3];
        translate.j = xfm[1][3];
        translate.k = xfm[2][3];
        // this is the easiest way to visualize what is happening - think of passing the axis vectors (1,0,0),
        //  (0,1,0), (0,0,1) through this transform.  This produces the transformed axis system i',j',k' (almost).
        //  If there is no scale or shear, we can look at the rotations of these axis and build the rotation
        //  angles.  If there is scale and shear, we need to compute  and undo those before we compute the
        //  rotation angles.
        final Vector3d vXrot = new Vector3d(xfm[0][0], xfm[1][0], xfm[2][0]);
        final Vector3d vYxfm = new Vector3d(xfm[0][1], xfm[1][1], xfm[2][1]);
        final Vector3d vZxfm = new Vector3d(xfm[0][2], xfm[1][2], xfm[2][2]);
        sx = vXrot.getLength();
        vXrot.normalize();
        // Assume X transforms to i'.  Y transforms to be on the i'j' plane - though if there was shear, it may not be
        //  perpendicular to i' and the shear factored in.  the transformed k needs to be scaled and sheared to be
        //  perpendicular to the i'j' plane and of length 1
        final Vector3d vZrot = new Vector3d(vXrot).cross(vYxfm).normalize();   // true k'
        final Vector3d vYrot = new Vector3d(vZrot).cross(vXrot);                   // true j'
        // We can use the dot products between the true i'
        sy = vYxfm.dot(vYrot);
        sz = vZxfm.dot(vZrot);
        if (null != scale) {
            scale.i = sx;
            scale.j = sy;
            scale.k = sz;
        }
        if (PackageConstants.isZero(sy) || PackageConstants.isZero(sz)) {
            throw new ZeroLengthVectorException();
        }
        if (null != shear) {
            shear.i = vXrot.dot(vYxfm) / sy;
            shear.j = vXrot.dot(vZxfm) / sz;
            shear.k = vYrot.dot(vZxfm) / sz;
        }
        // work from the rotated coordinate system to derive the rotations that would get me there
        if (PackageConstants.isZero(vYrot.k - 1.0f)) {
            // the Y axis has been rotated to be coincident with the +Z (pointing up)
            aAltitude.setDegrees(90.0f);
            aAzimuth.atan2(-vXrot.j, vXrot.i);
        } else if (PackageConstants.isZero(vYrot.k + 1.0f)) {
            // the Y axis has been rotated to be coincident with the -Z (pointing down)
            aAltitude.setDegrees(-90.0f);
            aAzimuth.atan2(-vXrot.j, vXrot.i);
        } else if (PackageConstants.isZero(vXrot.k - 1.0f)) {
            // the X axis has been rotated to be coincident with the +Z (pointing up)
            aRoll.setDegrees(-90.0f);
            aAzimuth.atan2(vYrot.i, vYrot.j);
        } else if (PackageConstants.isZero(vXrot.k + 1.0f)) {
            // the X axis has been rotated to be coincident with the -Z (pointing down)
            aRoll.setDegrees(90.0f);
            aAzimuth.atan2(vYrot.i, vYrot.j);
        } else {
            aRoll.atan2(-vXrot.k, vZrot.k);
            aAltitude.asin(vYrot.k);
            if (Math.abs(vXrot.k) < Math.abs(vYrot.k)) {
                aAzimuth.atan2(-vXrot.j, vXrot.i);
            } else {
                aAzimuth.atan2(vYrot.i, vYrot.j);
            }
        }
    }

    public final void transpose() {
        double temp;
        for (int row = 0; row < 4; row++) {
            for (int col = row + 1; col < 4; col++) {
                temp = this.xfm[row][col];
                this.xfm[row][col] = this.xfm[col][row];
                this.xfm[col][row] = temp;
            }
        }
    }

    /**
     * General invert routine.  Inverts m1 and places the result in "this".
     * Note that this routine handles both the "this" version and the
     * non-"this" version.
     * <p>
     * Also note that since this routine is slow anyway, we won't worry
     * about allocating a little bit of garbage.
     *
     * @param xfm (readonly) The transformation to be inverted into this transformation.
     */
    private void invertGeneral(final @NotNull Xfm4x4d xfm) {
        final double[] temp = new double[16];
        final double[] result = new double[16];
        final int[] row_perm = new int[4];
        int i, j, k;
        // Use LU decomposition and back-substitution code specifically
        // for double 4x4 matrices.

        // Copy source matrix to t1tmp
        for (k = 0, i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                temp[k++] = xfm.xfm[i][j];
            }
        }
        // Calculate LU decomposition: Is the matrix singular?
        if (!luDecomposition(temp, row_perm)) {
            // Matrix has no inverse
            throw new SingularMatrixException();
        }

        // Perform back substitution on the identity matrix
        for (i = 0; i < 16; i++) result[i] = 0.0;
        result[0] = 1.0;
        result[5] = 1.0;
        result[10] = 1.0;
        result[15] = 1.0;
        luBackSubstitution(temp, row_perm, result);

        for (k = 0, i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                this.xfm[i][j] = result[k++];
            }
        }
    }

    /**
     * Given a 4x4 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "row_perm".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[4]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     * <p>
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 4x4 matrices.
     *
     * @param matrix0  (modified) The matrix being decomposed.
     * @param row_perm (modified) The row permutation array (where the original rows moved)
     * @return <code>true</code> if the decomposition was successful, false otherwise.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //        Numerical Recipes in C, Cambridge University Press,
    //        1988, pp 40-45.
    //
    private static boolean luDecomposition(final @NotNull double[] matrix0,
                                           final @NotNull int[] row_perm) {

        final double[] row_scale = new double[4];

        // Determine implicit scaling information by looping over rows
        {
            int i, j;
            int ptr, rs;
            double big, temp;

            ptr = 0;
            rs = 0;

            // For each row ...
            i = 4;
            while (i-- != 0) {
                big = 0.0;

                // For each column, find the largest element in the row
                j = 4;
                while (j-- != 0) {
                    temp = matrix0[ptr++];
                    temp = Math.abs(temp);
                    if (temp > big) {
                        big = temp;
                    }
                }

                // Is the matrix singular?
                if (big == 0.0) {
                    return false;
                }
                row_scale[rs++] = 1.0 / big;
            }
        }

        {
            int j;
            final int mtx;

            mtx = 0;

            // For all columns, execute Crout's method
            for (j = 0; j < 4; j++) {
                int i, imax, k;
                int target, p1, p2;
                double sum, big, temp;

                // Determine elements of upper diagonal matrix U
                for (i = 0; i < j; i++) {
                    target = mtx + (4 * i) + j;
                    sum = matrix0[target];
                    k = i;
                    p1 = mtx + (4 * i);
                    p2 = mtx + j;
                    while (k-- != 0) {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 4;
                    }
                    matrix0[target] = sum;
                }

                // Search for largest pivot element and calculate
                // intermediate elements of lower diagonal matrix L.
                big = 0.0;
                imax = -1;
                for (i = j; i < 4; i++) {
                    target = mtx + (4 * i) + j;
                    sum = matrix0[target];
                    k = j;
                    p1 = mtx + (4 * i);
                    p2 = mtx + j;
                    while (k-- != 0) {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 4;
                    }
                    matrix0[target] = sum;

                    // Is this the best pivot so far?
                    if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
                        big = temp;
                        imax = i;
                    }
                }

                if (imax < 0) {
                    throw new RuntimeException();
                }

                // Is a row exchange necessary?
                if (j != imax) {
                    // Yes: exchange rows
                    k = 4;
                    p1 = mtx + (4 * imax);
                    p2 = mtx + (4 * j);
                    while (k-- != 0) {
                        temp = matrix0[p1];
                        matrix0[p1++] = matrix0[p2];
                        matrix0[p2++] = temp;
                    }

                    // Record change in scale factor
                    row_scale[imax] = row_scale[j];
                }

                // Record row permutation
                row_perm[j] = imax;

                // Is the matrix singular
                if (matrix0[(mtx + (4 * j) + j)] == 0.0) {
                    return false;
                }

                // Divide elements of lower diagonal matrix L by pivot
                if (j != (4 - 1)) {
                    temp = 1.0 / (matrix0[(mtx + (4 * j) + j)]);
                    target = mtx + (4 * (j + 1)) + j;
                    i = 3 - j;
                    while (i-- != 0) {
                        matrix0[target] *= temp;
                        target += 4;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD4x4 and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a 4x4 matrix of double values.  The procedure takes each
     * column of "matrix2" in turn and treats it as the right-hand side of the
     * matrix equation Ax = LUx = b.  The solution vector replaces the
     * original column of the matrix.
     * <p>
     * If "matrix2" is the identity matrix, the procedure replaces its contents
     * with the inverse of the matrix from which "matrix1" was originally
     * derived.
     *
     * @param matrix1  (readonly) The decomposed matrix
     * @param row_perm (readonly) The row permutation array.
     * @param matrix2  (modified) and identity matrix.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //        Numerical Recipes in C, Cambridge University Press,
    //        1988, pp 44-45.
    //
    private static void luBackSubstitution(final double[] matrix1,
                                           final int[] row_perm,
                                           final double[] matrix2) {
        int i, ii, ip, j, k;
        final int rp;
        int cv, rv;

        //  rp = row_perm;
        rp = 0;

        // For each column vector of matrix2 ...
        for (k = 0; k < 4; k++) {
            //      cv = &(matrix2[0][k]);
            cv = k;
            ii = -1;

            // Forward substitution
            for (i = 0; i < 4; i++) {
                double sum;

                ip = row_perm[rp + i];
                sum = matrix2[cv + 4 * ip];
                matrix2[cv + 4 * ip] = matrix2[cv + 4 * i];
                if (ii >= 0) {
                    //          rv = &(matrix1[i][0]);
                    rv = i * 4;
                    for (j = ii; j <= i - 1; j++) {
                        sum -= matrix1[rv + j] * matrix2[cv + 4 * j];
                    }
                } else if (sum != 0.0) {
                    ii = i;
                }
                matrix2[cv + 4 * i] = sum;
            }

            // Back substitution
            //      rv = &(matrix1[3][0]);
            rv = 3 * 4;
            matrix2[cv + 4 * 3] /= matrix1[rv + 3];

            rv -= 4;
            matrix2[cv + 4 * 2] = (matrix2[cv + 4 * 2] -
                    matrix1[rv + 3] * matrix2[cv + 4 * 3]) / matrix1[rv + 2];

            rv -= 4;
            matrix2[cv + 4 * 1] = (matrix2[cv + 4 * 1] -
                    matrix1[rv + 2] * matrix2[cv + 4 * 2] -
                    matrix1[rv + 3] * matrix2[cv + 4 * 3]) / matrix1[rv + 1];

            rv -= 4;
            matrix2[cv + 4 * 0] = (matrix2[cv + 4 * 0] -
                    matrix1[rv + 1] * matrix2[cv + 4 * 1] -
                    matrix1[rv + 2] * matrix2[cv + 4 * 2] -
                    matrix1[rv + 3] * matrix2[cv + 4 * 3]) / matrix1[rv + 0];
        }
    }

    /**
     * Inverts this transform and returns the result in this transformation.
     *
     * @return Returns this transformation.
     */
    @NotNull
    public Xfm4x4d invert() {
        invertGeneral(this);
        return this;
    }

    /**
     * Invert this transformation into the supplied transformation.
     *
     * @param inverse (Xfm4x4d, modified) The transformation that will be set to the inverse.
     * @return Returns the inverse transformation <code>inverse</code>.
     */
    @NotNull
    public Xfm4x4d invert(@NotNull Xfm4x4d inverse) {
        inverse.invertGeneral(this);
        return inverse;
    }

    /**
     * Pre-multiply this transformation by another transformation.  Pre-transformation is used when you already have a
     * transformation that locates an object, and you want to apply additional transformation operators to the
     * object.  This often happens during interactive environment editing.
     *
     * @param xfm The transformation which will be pre-multiplied with this transformation.
     * @return Returns this transformation after pre-multiplication with <code>xfm</code>
     */
    @NotNull
    public Xfm4x4d preMul(final Xfm4x4d xfm) {
        final Xfm4x4d xfmTmp = new Xfm4x4d(this);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.xfm[row][col] = 0.0f;
                for (int i = 0; i < 4; i++) {
                    this.xfm[row][col] += (xfm.xfm[row][i] * xfmTmp.xfm[i][col]);
                }
            }
        }
        return this;
    }

    /**
     * @param srs00
     * @param srs01
     * @param srs02
     * @param srs10
     * @param srs11
     * @param srs12
     * @param srs20
     * @param srs21
     * @param srs22
     * @return
     */
    @NotNull
    private Xfm4x4d preMul(final double srs00, final double srs01, final double srs02,
                           final double srs10, final double srs11, final double srs12,
                           final double srs20, final double srs21, final double srs22) {
        // This is a local pre-multiply by the scale-rotation-shear 3x3 transform.  It is used to minimize wasted work and to
        //  eliminate the need to borrow an intermediate transformation when performing a scale, rotate, or shear operation.
        double c0, c1, c2;     // the temporary column - we process this transform by column - these are the untransformed
        // state of the column
        for (int col = 0; col < 3; col++) {
            c0 = xfm[0][col];
            c1 = xfm[1][col];
            c2 = xfm[2][col];
            xfm[0][col] = (srs00 * c0) + (srs01 * c1) + (srs02 * c2);
            xfm[1][col] = (srs10 * c0) + (srs11 * c1) + (srs12 * c2);
            xfm[2][col] = (srs20 * c0) + (srs21 * c1) + (srs22 * c2);

        }
        return this;
    }

    /**
     * Transform a point in place.
     *
     * @param pt (Point3d, modified) The point to be transformed by this transformation.
     * @return Returns this transformed point <code>pt</code>.
     */
    @NotNull
    public Point3d transform(@NotNull final Point3d pt) {
        return transform(pt, pt);
    }

    /**
     * Transform a point into a target point.
     *
     * @param pt    (Point3d, readonly) The point to be transformed.
     * @param xfmPt (Point3d, modified) The point to receive the transformed point (the target point).
     * @return Returns the transformed point, <code>xfmPt</code>.
     */
    @NotNull
    public Point3d transform(@NotNull final Point3d pt, @NotNull final Point3d xfmPt) {
        return xfmPt.setValue(
                (xfm[0][0] * pt.x) + (xfm[0][1] * pt.y) + (xfm[0][2] * pt.z) + xfm[0][3],
                (xfm[1][0] * pt.x) + (xfm[1][1] * pt.y) + (xfm[1][2] * pt.z) + xfm[1][3],
                (xfm[2][0] * pt.x) + (xfm[2][1] * pt.y) + (xfm[2][2] * pt.z) + xfm[2][3]);
    }

    /**
     * Transform an array of points in place.
     *
     * @param pts (Point3d[], modified) The array of points to be transformed.
     * @return Returns the transformed point array <code>pts</code>.
     */
    @NotNull
    public Point3d[] transform(@NotNull final Point3d[] pts) {
        for (int iPt = pts.length; --iPt >= 0; ) {
            final Point3d pt = pts[iPt];
            transform(pt, pt);
        }
        return pts;
    }

    /**
     * Clone and Transform an array of points.
     *
     * @param pts (Point3d[], readonly) The array of points to be cloned and transformed.
     * @return Returns the cloned and transformed point array.
     */
    @NotNull
    public Point3d[] cloneAndTransform(@NotNull final Point3d[] pts) {
        Point3d[] xfmPts = new Point3d[pts.length];
        for (int iPt = pts.length; --iPt >= 0; ) {
            xfmPts[iPt] = transform(pts[iPt], new Point3d());
        }
        return xfmPts;
    }

    /**
      * Transform a vector in place.
      *
      * @param v (Vector3d, modified) The vector to be transformed by this transformation.
      * @return Returns the transformed vector <code>v</code>.
      */
    @NotNull
    public Vector3d transform(@NotNull final Vector3d v) {
        return transform(v, v);
    }

    /**
     * Transform a vector into a target vector.
     *
     * @param v    (Vector3d, readonly) The vector to be transformed.
     * @param xfmV (Vector3d, modified) The vector to receive the transformed vector (the target vector).
     * @return Returns the transformed vector, <code>xfmV</code>.
     */
    @NotNull
    public Vector3d transform(@NotNull final Vector3d v, @NotNull final Vector3d xfmV) {
        return xfmV.setValue(
                (xfm[0][0] * v.i) + (xfm[0][1] * v.j) + (xfm[0][2] * v.k),
                (xfm[1][0] * v.i) + (xfm[1][1] * v.j) + (xfm[1][2] * v.k),
                (xfm[2][0] * v.i) + (xfm[2][1] * v.j) + (xfm[2][2] * v.k));
    }

    /**
     * Transform an array of vectors into a target vector array.
     *
     * @param vs    (Vector3d[], readonly) An array of vectors to be transformed.
     * @param xfmVs (Vector3d[], modified) An array of vectors to receive the transformed vectors.
     * @return Returns the transformed vector array, <code>xfmVs</code>.
     */
    @NotNull
    public Vector3d[] transform(@NotNull final Vector3d[] vs, @NotNull final Vector3d[] xfmVs) {
        for (int iV = vs.length; --iV >= 0; ) {
            transform(vs[iV], xfmVs[iV]);
        }
        return xfmVs;
    }

    /**
     * Clone and transform an array of vectors.
     *
     * @param vs    (Vector3d, readonly) The array of vectors to be cloned and transformed.
     * @return Returns the cloned and transformed vector array.
     */
    @NotNull
    public Vector3d[] cloneAndTransform(@NotNull final Vector3d[] vs) {
        Vector3d[] xfmVs = new Vector3d[vs.length];
        for (int iV = vs.length; --iV >= 0; ) {
            xfmVs[iV] = transform(vs[iV], new Vector3d());
        }
        return xfmVs;
    }
}
