package org.a05annex.util.geo2d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(JUnitPlatform.class)
public class TestSpline {

    private static double REF_SPEED = 10.0;
    private static double START_END_X = 10.0;
    private static double START_END_Y = 20.0;
    private final KochanekBartelsSpline m_startEndDerivativeTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline m_scaleSpeedTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline m_adjustTimeTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline.ControlPoint m_scaleSpeedTestStart;
    private final KochanekBartelsSpline.ControlPoint m_scaleSpeedTestEnd;
    private final List<KochanekBartelsSpline.ControlPoint> m_adjustTimeTestPoints =
            new ArrayList<KochanekBartelsSpline.ControlPoint>();

    public TestSpline() {
        // create the test spline for the first set of tests - Start and end nearly coincident with the
        // adjacent point on the spline.
        m_startEndDerivativeTest.addControlPoint(START_END_X, START_END_Y);
        m_startEndDerivativeTest.addControlPoint(START_END_X, START_END_Y);
        m_startEndDerivativeTest.addControlPoint(START_END_X, START_END_Y);

        // create a test spline for speed multiplier
        m_scaleSpeedTestStart = m_scaleSpeedTest.addControlPoint(0.0, 0.0);
        m_scaleSpeedTestStart.setTangent(REF_SPEED,0.0);
        m_scaleSpeedTestEnd = m_scaleSpeedTest.addControlPoint(10.0, 0.0);
        m_scaleSpeedTestEnd.setTangent(REF_SPEED,0.0);

        // create a test spline for resetting the time for a single point. This is a 3 point curve initially
        // starting as a straight line path at REF_SPEED and going for 2 seconds at that speed.
        KochanekBartelsSpline.ControlPoint thisPt =
                m_adjustTimeTest.addControlPoint(0.0 * REF_SPEED, 0.0);
        thisPt.setTangent(REF_SPEED,0.0);
        m_adjustTimeTestPoints.add(thisPt);

        thisPt = m_adjustTimeTest.addControlPoint(0.5 * REF_SPEED, 0.0);
        thisPt.setTangent(REF_SPEED,0.0);
        thisPt.setTime(0.5, false);
        m_adjustTimeTestPoints.add(thisPt);

        thisPt = m_adjustTimeTest.addControlPoint(1.5 * REF_SPEED, 0.0);
        thisPt.setTangent(REF_SPEED,0.0);
        thisPt.setTime(1.5, false);
        m_adjustTimeTestPoints.add(thisPt);

        thisPt = m_adjustTimeTest.addControlPoint(3.0 * REF_SPEED, 0.0);
        thisPt.setTangent(REF_SPEED,0.0);
        thisPt.setTime(3.0, false);
        m_adjustTimeTestPoints.add(thisPt);

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
    void testSet2_verifySpeedMultiplier_1() {
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
    void testSet2_verifySpeedMultiplier_0_5() {
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
    void testSet2_verifySpeedMultiplier_1_5() {
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

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for setting the time of an individual control point
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Verify different segment times")
    void testSet3_verifyDifferentSegmentTimes() {
        m_adjustTimeTest.setSpeedMultiplier(1.0);
        assertEquals(1.0, m_adjustTimeTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = m_adjustTimeTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
//            System.out.printf("%10.3f, %10.3f, %10.3f, %10.3f, %10.3f %n",
//                    pathPt.time, pathPt.speedForward, pathPt.speedStrafe, pathPt.speedRotation, pathPt.fieldPt.getX());
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }

        m_adjustTimeTestPoints.get(2).setTime(2.5, false);
        m_adjustTimeTestPoints.get(2).setFieldLocation(2.5 * REF_SPEED,0.0);
        follower = m_adjustTimeTest.getPathFollower();
        time = 0.0;
        while (null != (pathPt = follower.getPointAt(time))) {
//            System.out.printf("%10.3f, %10.3f, %10.3f, %10.3f, %10.3f %n",
//                    pathPt.time, pathPt.speedForward, pathPt.speedStrafe, pathPt.speedRotation, pathPt.fieldPt.getX());
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }

        m_adjustTimeTestPoints.get(1).setTime(1.5, false);
        m_adjustTimeTestPoints.get(1).setFieldLocation(1.5 * REF_SPEED,0.0);
        follower = m_adjustTimeTest.getPathFollower();
        time = 0.0;
        while (null != (pathPt = follower.getPointAt(time))) {
//            System.out.printf("%10.3f, %10.3f, %10.3f, %10.3f, %10.3f %n",
//                    pathPt.time, pathPt.speedForward, pathPt.speedStrafe, pathPt.speedRotation, pathPt.fieldPt.getX());
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    @Test
    @DisplayName("Verify ControlPoint.getNext()")
    void testSet3_verifyGetNext() {
        assertEquals(m_adjustTimeTestPoints.get(0).getNext(), m_adjustTimeTestPoints.get(1));
        assertEquals(m_adjustTimeTestPoints.get(1).getNext(), m_adjustTimeTestPoints.get(2));
        assertEquals(m_adjustTimeTestPoints.get(2).getNext(), m_adjustTimeTestPoints.get(3));
        assertEquals(m_adjustTimeTestPoints.get(3).getNext(), null);

    }

    @Test
    @DisplayName("Verify ControlPoint.getLast()")
    void testSet3_verifyGetLast() {
        assertEquals(m_adjustTimeTestPoints.get(0).getLast(), null);
        assertEquals(m_adjustTimeTestPoints.get(1).getLast(), m_adjustTimeTestPoints.get(0));
        assertEquals(m_adjustTimeTestPoints.get(2).getLast(), m_adjustTimeTestPoints.get(1));
        assertEquals(m_adjustTimeTestPoints.get(3).getLast(), m_adjustTimeTestPoints.get(2));

    }

    @Test
    @DisplayName("Verify ControlPoint.setTime(time,false)")
    void test_setTimeNoPropagate() {
        final KochanekBartelsSpline testSpline = new KochanekBartelsSpline();
        final List<KochanekBartelsSpline.ControlPoint> controlPoints =
                new ArrayList<KochanekBartelsSpline.ControlPoint>();
        controlPoints.add(testSpline.addControlPoint(0.0, 0.0));
        controlPoints.add(testSpline.addControlPoint(10.0, 0.0));
        controlPoints.add(testSpline.addControlPoint(20.0, 0.0));
        controlPoints.add(testSpline.addControlPoint(30.0, 0.0));
        // set the time of the second point - no propagation
        controlPoints.get(1).setTime(1.1, false);
        assertEquals(0.0, controlPoints.get(0).getTime(), 0.00001);
        assertEquals(1.1, controlPoints.get(1).getTime(), 0.00001);
        assertEquals(2.0, controlPoints.get(2).getTime(), 0.00001);
        assertEquals(3.0, controlPoints.get(3).getTime(), 0.00001);
    }
    @Test
    @DisplayName("Verify ControlPoint.setTime(time,true)")
    void test_setTimeWithPropagate() {
        final KochanekBartelsSpline m_startEndDerivativeTest = new KochanekBartelsSpline();
        final KochanekBartelsSpline testSpline = new KochanekBartelsSpline();
        final List<KochanekBartelsSpline.ControlPoint> controlPoints =
                new ArrayList<KochanekBartelsSpline.ControlPoint>();
        controlPoints.add(testSpline.addControlPoint(0.0, 0.0));
        controlPoints.add(testSpline.addControlPoint(10.0, 0.0));
        controlPoints.add(testSpline.addControlPoint(20.0, 0.0));
        controlPoints.add(testSpline.addControlPoint(30.0, 0.0));
        // set the time of the second point - no propagation
        controlPoints.get(1).setTime(1.1, true);
        assertEquals(0.0, controlPoints.get(0).getTime(), 0.00001);
        assertEquals(1.1, controlPoints.get(1).getTime(), 0.00001);
        assertEquals(2.1, controlPoints.get(2).getTime(), 0.00001);
        assertEquals(3.1, controlPoints.get(3).getTime(), 0.00001);
    }

}
