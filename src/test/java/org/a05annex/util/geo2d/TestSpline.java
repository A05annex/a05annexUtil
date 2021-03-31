package org.a05annex.util.geo2d;
import org.a05annex.util.Utl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(JUnitPlatform.class)
public class TestSpline {

    private static double REF_SPEED = 10.0;
    private static double START_END_X = 10.0;
    private static double START_END_Y = 20.0;
    private final KochanekBartelsSpline m_startEndDerivativeTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline m_scaleSpeedTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline.ControlPoint m_scaleSpeedTestStart;
    private final KochanekBartelsSpline.ControlPoint m_scaleSpeedTestEnd;

    public TestSpline() {
        // create the test spline for the first set of tests - Start and end nearly coincident with the
        // adjacent point on the spline.
        m_startEndDerivativeTest.addControlPoint(START_END_X, START_END_Y);
        m_startEndDerivativeTest.addControlPoint(START_END_X, START_END_Y);
        m_startEndDerivativeTest.addControlPoint(START_END_X, START_END_Y);

        m_scaleSpeedTestStart = m_scaleSpeedTest.addControlPoint(0.0, 0.0);
        m_scaleSpeedTestStart.setTangent(REF_SPEED,0.0);
        m_scaleSpeedTestEnd = m_scaleSpeedTest.addControlPoint(10.0, 0.0);
        m_scaleSpeedTestEnd.setTangent(REF_SPEED,0.0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for the reported bug in computing start and end derivatives when the start and end control points are
    // in the same field position as the point immediately adjacent to them (i.e. the robot is rotating, but is
    // otherwise stationary on the field. The bug was that the automatically generated derivatives were NAN because
    // of a divide by zero in the default slope calculations. The fix was to detect that the point adjacent to
    // the start or end terminating point was so close that the derivative for the start and end point should be 0.0.
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * This test verifies there are 3 control points, all coincident at ({@link #START_END_X}, {@link #START_END_Y})
     */
    @Test
    @DisplayName("Verify start-end-derivative control points")
    void testSet1_verifyControlPoints() {
        int ptCt = 0;
        for (KochanekBartelsSpline.ControlPoint controlPoint : m_startEndDerivativeTest.getControlPoints()) {
            assertEquals(START_END_X, controlPoint.getFieldX());
            assertEquals(START_END_Y, controlPoint.getFieldY());
            ptCt++;
        }
        assertEquals(3, ptCt);
    }

    /**
     * Since all control points are coincident, the start derivative should be 0.0
     */
    @Test
    @DisplayName("Verify start-derivative")
    void testSet1_verifyStartDerivative() {
        for (KochanekBartelsSpline.ControlPoint controlPoint : m_startEndDerivativeTest.getControlPoints()) {
            assertEquals(0.0, controlPoint.m_dX);
            assertEquals(0.0, controlPoint.m_dY);
            break;
        }
    }

    /**
     * Since all control points are coincident, the intermediate derivative should be 0.0
     */
    @Test
    @DisplayName("Verify intermediate-derivative")
    void testSet1_verifyIntermediateDerivative() {
        KochanekBartelsSpline.ControlPoint intermediateControlPoint = null;
        for (KochanekBartelsSpline.ControlPoint controlPoint : m_startEndDerivativeTest.getControlPoints()) {
            if (null != intermediateControlPoint) {
                intermediateControlPoint = controlPoint;
                break;
            }
            intermediateControlPoint = controlPoint;
        }
        assertEquals(0.0, intermediateControlPoint.m_dX);
        assertEquals(0.0, intermediateControlPoint.m_dY);
    }

    /**
     * Since all control points are coincident, the end derivative should be 0.0
     */
    @Test
    @DisplayName("Verify end-derivative")
    void testSet1_verifyEndDerivative() {
        KochanekBartelsSpline.ControlPoint lastControlPoint = null;
        for (KochanekBartelsSpline.ControlPoint controlPoint : m_startEndDerivativeTest.getControlPoints()) {
            lastControlPoint = controlPoint;
        }
        assertEquals(0.0, lastControlPoint.m_dX);
        assertEquals(0.0, lastControlPoint.m_dY);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for adding a speed multiplier
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Verify speedMultiplier 1.0")
    void testSet1_verifySpeedMultiplier_1() {
        m_scaleSpeedTest.setSpeedMultiplier(1.0);
        assertEquals(1.0, m_scaleSpeedTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = m_scaleSpeedTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
//            System.out.printf("%10.3f, %10.3f, %10.3f, %10.3f, %10.3f %n",
//                    pathPt.time, pathPt.speedForward, pathPt.speedStrafe, pathPt.speedRotation, pathPt.fieldPt.getX());
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    @Test
    @DisplayName("Verify speedMultiplier 0.5")
    void testSet1_verifySpeedMultiplier_0_5() {
        m_scaleSpeedTest.setSpeedMultiplier(0.5);
        assertEquals(0.5, m_scaleSpeedTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = m_scaleSpeedTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
//            System.out.printf("%10.3f, %10.3f, %10.3f, %10.3f, %10.3f %n",
//                    pathPt.time, pathPt.speedForward, pathPt.speedStrafe, pathPt.speedRotation, pathPt.fieldPt.getX());
            assertEquals(time * 0.5, pathPt.time, 0.00001);
            assertEquals(REF_SPEED * 0.5, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    @Test
    @DisplayName("Verify speedMultiplier 1.5")
    void testSet1_verifySpeedMultiplier_1_5() {
        m_scaleSpeedTest.setSpeedMultiplier(1.5);
        assertEquals(1.5, m_scaleSpeedTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = m_scaleSpeedTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
//            System.out.printf("%10.3f, %10.3f, %10.3f, %10.3f, %10.3f %n",
//                    pathPt.time, pathPt.speedForward, pathPt.speedStrafe, pathPt.speedRotation, pathPt.fieldPt.getX());
            assertEquals(time * 1.5, pathPt.time, 0.00001);
            assertEquals(REF_SPEED * 1.5, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

}
