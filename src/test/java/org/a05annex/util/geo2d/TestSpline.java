package org.a05annex.util.geo2d;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(JUnitPlatform.class)
public class TestSpline {

    private static final double REF_SPEED = 10.0;
    private static final double START_END_X = 10.0;
    private static final double START_END_Y = 20.0;
    private final KochanekBartelsSpline m_startEndDerivativeTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline m_scaleSpeedTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline m_adjustTimeTest = new KochanekBartelsSpline();
    private final List<KochanekBartelsSpline.ControlPoint> m_adjustTimeTestPoints = new ArrayList<>();

    public TestSpline() {
        // create the test spline for the first set of tests - Start and end nearly coincident with the
        // adjacent point on the spline.
        m_startEndDerivativeTest.addControlPoint(new Point2D.Double(START_END_X, START_END_Y));
        m_startEndDerivativeTest.addControlPoint(new Point2D.Double(START_END_X, START_END_Y));
        m_startEndDerivativeTest.addControlPoint(new Point2D.Double(START_END_X, START_END_Y));

        // create a test spline for speed multiplier
        addControlPoint(m_scaleSpeedTest, 0.0, 0.0, REF_SPEED, 0.0);
        addControlPoint(m_scaleSpeedTest, 10.0, 0.0, REF_SPEED, 0.0);

        // create a test spline for resetting the time for a single point. This is a 3 point curve initially
        // starting as a straight line path at REF_SPEED and going for 2 seconds at that speed.
        createLinearTestSpline(m_adjustTimeTest, m_adjustTimeTestPoints);
    }

    private KochanekBartelsSpline.ControlPoint addControlPoint(KochanekBartelsSpline spline,
                                                               double fieldX, double fieldY,
                                                               double field_dX, double field_dY) {
        KochanekBartelsSpline.ControlPoint thisPt;
        thisPt = spline.addControlPoint(fieldX, fieldY);
        thisPt.setTangent(field_dX, field_dY);
        return thisPt;
    }

    private KochanekBartelsSpline.ControlPoint addControlPoint(KochanekBartelsSpline spline,
                                                               double fieldX, double fieldY,
                                                               double field_dX, double field_dY,
                                                               double time, boolean propagate) {
        KochanekBartelsSpline.ControlPoint thisPt = addControlPoint(spline, fieldX, fieldY, field_dX, field_dY);
        thisPt.setTime(time, propagate);
        return thisPt;
    }

    private void createLinearTestSpline(KochanekBartelsSpline spline,
                                        List<KochanekBartelsSpline.ControlPoint> ctrlPts) {
        ctrlPts.add(addControlPoint(spline, 0.0 * REF_SPEED, 0.0,
                REF_SPEED, 0.0));
        ctrlPts.add(addControlPoint(spline, 0.5 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 0.5, false));
        ctrlPts.add(addControlPoint(spline, 1.5 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 1.5, false));
        ctrlPts.add(addControlPoint(spline, 3.0 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 3.0, false));
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
            assertEquals(time, pathPt.time, 0.00001);
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
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED * 1.5, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for setting the time of an individual control point
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * This is a test that moves control points in time along a linear path with a constant speed to assure the
     * path remains constant regardless of where in time the points are positioned.
     */
    @Test
    @DisplayName("Verify different segment times")
    void testSet3_verifyDifferentSegmentTimes() {
        m_adjustTimeTest.setSpeedMultiplier(1.0);
        assertEquals(1.0, m_adjustTimeTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = m_adjustTimeTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }

        m_adjustTimeTestPoints.get(2).setTime(2.5, false);
        m_adjustTimeTestPoints.get(2).setFieldLocation(2.5 * REF_SPEED, 0.0);
        follower = m_adjustTimeTest.getPathFollower();
        time = 0.0;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }

        m_adjustTimeTestPoints.get(1).setTime(1.5, false);
        m_adjustTimeTestPoints.get(1).setFieldLocation(1.5 * REF_SPEED, 0.0);
        follower = m_adjustTimeTest.getPathFollower();
        time = 0.0;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    /**
     * This is a test of the
     * {@link org.a05annex.util.geo2d.KochanekBartelsSpline.ControlPoint#setTime(double, boolean)} method to
     * assure time is correctly set without delta propagation.
     */
    @Test
    @DisplayName("Verify ControlPoint.setTime(time,false)")
    void test_setTimeNoPropagate() {
        final KochanekBartelsSpline testSpline = new KochanekBartelsSpline();
        final List<KochanekBartelsSpline.ControlPoint> controlPoints =
                new ArrayList<>();
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

    /**
     * This is a test of the
     * {@link org.a05annex.util.geo2d.KochanekBartelsSpline.ControlPoint#setTime(double, boolean)} method to
     * assure time is correctly set with delta propagation.
     */
    @Test
    @DisplayName("Verify ControlPoint.setTime(time,true)")
    void test_setTimeWithPropagate() {
        final KochanekBartelsSpline testSpline = new KochanekBartelsSpline();
        final List<KochanekBartelsSpline.ControlPoint> controlPoints =
                new ArrayList<>();
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

    // -----------------------------------------------------------------------------------------------------------------
    // Tests for miscellaneous methods for control point get/set methods.
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Verify ControlPoint.getNext()")
    void testSet3_verifyGetNext() {
        assertEquals(m_adjustTimeTestPoints.get(0).getNext(), m_adjustTimeTestPoints.get(1));
        assertEquals(m_adjustTimeTestPoints.get(1).getNext(), m_adjustTimeTestPoints.get(2));
        assertEquals(m_adjustTimeTestPoints.get(2).getNext(), m_adjustTimeTestPoints.get(3));
        assertNull(m_adjustTimeTestPoints.get(3).getNext());

    }

    @Test
    @DisplayName("Verify ControlPoint.getLast()")
    void testSet3_verifyGetLast() {
        assertNull(m_adjustTimeTestPoints.get(0).getLast());
        assertEquals(m_adjustTimeTestPoints.get(1).getLast(), m_adjustTimeTestPoints.get(0));
        assertEquals(m_adjustTimeTestPoints.get(2).getLast(), m_adjustTimeTestPoints.get(1));
        assertEquals(m_adjustTimeTestPoints.get(3).getLast(), m_adjustTimeTestPoints.get(2));

    }

    // -----------------------------------------------------------------------------------------------------------------
    // Tests control point insert and delete operations (add has already been exhaustively tested)
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Verify KochanekBartelsSpline.deleteControlPoint() Error Handling")
    void ControlPointDeleteErrors() {
        final KochanekBartelsSpline spline = new KochanekBartelsSpline();
        final KochanekBartelsSpline.ControlPoint firstPt = addControlPoint(spline, 0.0 * REF_SPEED, 0.0,
                REF_SPEED, 0.0);
        // try to delete the only control point - should fail
        assertThrows(IllegalStateException.class,
                () -> spline.deleteControlPoint(firstPt));
        // add a second point
        final KochanekBartelsSpline.ControlPoint secondPt = addControlPoint(spline, 0.5 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 0.5, false);
        // again try to delete the first control point - should fail
        assertThrows(IllegalStateException.class,
                () -> spline.deleteControlPoint(firstPt));
        // delete the second control point, this should be fine
        spline.deleteControlPoint(secondPt);
    }

    @Test
    @DisplayName("Verify KochanekBartelsSpline.deleteControlPoint() Operation")
    void ControlPointDelete() {
        // build a known 4 control point linear path
        final KochanekBartelsSpline spline = new KochanekBartelsSpline();
        final List<KochanekBartelsSpline.ControlPoint> controlPoints = new ArrayList<>();
        createLinearTestSpline(spline, controlPoints);
        // when a control point is deleted the times around that control point should not change, and the path
        // should still be linear
        spline.deleteControlPoint(controlPoints.get(1));
        KochanekBartelsSpline.PathFollower follower = m_scaleSpeedTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    @Test
    @DisplayName("Verify KochanekBartelsSpline.insertControlPoint() Error Handling")
    void ControlPointInsertErrors() {
        final KochanekBartelsSpline spline = new KochanekBartelsSpline();
        final KochanekBartelsSpline.ControlPoint firstPt = addControlPoint(spline, 0.0 * REF_SPEED, 0.0,
                REF_SPEED, 0.0);
        // only one point - there is no where to inset
        assertThrows(IllegalStateException.class,
                () -> spline.insertControlPoint(0.0));
        assertThrows(IllegalStateException.class,
                () -> spline.insertControlPoint(-0.1));
        // OK, add a second point, them try the before and after options
        final KochanekBartelsSpline.ControlPoint secondPt = addControlPoint(spline, 0.5 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 0.5, false);
        assertThrows(IllegalArgumentException.class,
                () -> spline.insertControlPoint(-0.1));
        assertThrows(IllegalArgumentException.class,
                () -> spline.insertControlPoint(0.6));
    }

    @Test
    @DisplayName("Verify KochanekBartelsSpline.insertControlPoint() Operation")
    void ControlPointInsert() {
        // build a known 4 control point linear path
        final KochanekBartelsSpline spline = new KochanekBartelsSpline();
        final List<KochanekBartelsSpline.ControlPoint> controlPoints = new ArrayList<>();
        createLinearTestSpline(spline, controlPoints);
        // Insert a random point and things should be fine - i.e. It should still be a linear path over
        // the same path duration
        spline.insertControlPoint(1.0);
        KochanekBartelsSpline.PathFollower follower = m_scaleSpeedTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }
}
