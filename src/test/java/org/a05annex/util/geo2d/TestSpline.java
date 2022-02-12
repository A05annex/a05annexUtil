package org.a05annex.util.geo2d;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;


@RunWith(JUnitPlatform.class)
public class TestSpline {

    private static final double REF_SPEED = 10.0;
    private static final double START_END_X = 10.0;
    private static final double START_END_Y = 20.0;
    private final KochanekBartelsSpline startEndDerivativeTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline scaleSpeedTest = new KochanekBartelsSpline();
    private final KochanekBartelsSpline adjustTimeTest;
    private final List<KochanekBartelsSpline.ControlPoint> adjustTimeTestPoints = new ArrayList<>();

    public TestSpline() {
        // create the test spline for the first set of tests - Start and end nearly coincident with the
        // adjacent point on the spline.
        startEndDerivativeTest.addControlPoint(new Point2D.Double(START_END_X, START_END_Y));
        startEndDerivativeTest.addControlPoint(new Point2D.Double(START_END_X, START_END_Y));
        startEndDerivativeTest.addControlPoint(new Point2D.Double(START_END_X, START_END_Y));

        // create a test spline for speed multiplier
        addControlPoint(scaleSpeedTest, 0.0, 0.0, REF_SPEED, 0.0);
        addControlPoint(scaleSpeedTest, 10.0, 0.0, REF_SPEED, 0.0);

        // create a test spline for resetting the time for a single point. This is a 3 point curve initially
        // starting as a straight line path at REF_SPEED and going for 2 seconds at that speed.
        adjustTimeTest = createLinearTestSpline(adjustTimeTestPoints);
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

    /**
     * This method simply creates a spline and adds control points to create a very predictable linear
     * path with 4 control points.
     *
     * @param ctrlPts (modified) the list of control points added to create this spline.
     */
    private KochanekBartelsSpline createLinearTestSpline(@NotNull List<KochanekBartelsSpline.ControlPoint> ctrlPts) {
        final KochanekBartelsSpline spline = new KochanekBartelsSpline();
        ctrlPts.add(addControlPoint(spline, 0.0 * REF_SPEED, 0.0,
                REF_SPEED, 0.0));
        ctrlPts.add(addControlPoint(spline, 0.5 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 0.5, false));
        ctrlPts.add(addControlPoint(spline, 1.5 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 1.5, false));
        ctrlPts.add(addControlPoint(spline, 3.0 * REF_SPEED, 0.0,
                REF_SPEED, 0.0, 3.0, false));
        return spline;
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
        for (KochanekBartelsSpline.ControlPoint controlPoint : startEndDerivativeTest.getControlPoints()) {
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
        for (KochanekBartelsSpline.ControlPoint controlPoint : startEndDerivativeTest.getControlPoints()) {
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
        for (KochanekBartelsSpline.ControlPoint controlPoint : startEndDerivativeTest.getControlPoints()) {
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
        for (KochanekBartelsSpline.ControlPoint controlPoint : startEndDerivativeTest.getControlPoints()) {
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
        scaleSpeedTest.setSpeedMultiplier(1.0);
        assertEquals(1.0, scaleSpeedTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = scaleSpeedTest.getPathFollower();
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
        scaleSpeedTest.setSpeedMultiplier(0.5);
        assertEquals(0.5, scaleSpeedTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = scaleSpeedTest.getPathFollower();
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
        scaleSpeedTest.setSpeedMultiplier(1.5);
        assertEquals(1.5, scaleSpeedTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = scaleSpeedTest.getPathFollower();
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
        adjustTimeTest.setSpeedMultiplier(1.0);
        assertEquals(1.0, adjustTimeTest.getSpeedMultiplier());
        KochanekBartelsSpline.PathFollower follower = adjustTimeTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }

        adjustTimeTestPoints.get(2).setTime(2.5, false);
        adjustTimeTestPoints.get(2).setFieldLocation(2.5 * REF_SPEED, 0.0);
        follower = adjustTimeTest.getPathFollower();
        time = 0.0;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }

        adjustTimeTestPoints.get(1).setTime(1.5, false);
        adjustTimeTestPoints.get(1).setFieldLocation(1.5 * REF_SPEED, 0.0);
        follower = adjustTimeTest.getPathFollower();
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
        assertEquals(adjustTimeTestPoints.get(0).getNext(), adjustTimeTestPoints.get(1));
        assertEquals(adjustTimeTestPoints.get(1).getNext(), adjustTimeTestPoints.get(2));
        assertEquals(adjustTimeTestPoints.get(2).getNext(), adjustTimeTestPoints.get(3));
        assertNull(adjustTimeTestPoints.get(3).getNext());

    }

    @Test
    @DisplayName("Verify ControlPoint.getLast()")
    void testSet3_verifyGetLast() {
        assertNull(adjustTimeTestPoints.get(0).getLast());
        assertEquals(adjustTimeTestPoints.get(1).getLast(), adjustTimeTestPoints.get(0));
        assertEquals(adjustTimeTestPoints.get(2).getLast(), adjustTimeTestPoints.get(1));
        assertEquals(adjustTimeTestPoints.get(3).getLast(), adjustTimeTestPoints.get(2));

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

    /**
     * Create a linear path with 4 control points; deletes the second control point (index 1); run a path
     * follower and test the returned points to verify this is still a linear path.
     */
    @Test
    @DisplayName("Verify KochanekBartelsSpline.deleteControlPoint() Operation")
    void ControlPointDelete() {
        // build a known 4 control point linear path
        final List<KochanekBartelsSpline.ControlPoint> controlPoints = new ArrayList<>();
        final KochanekBartelsSpline spline = createLinearTestSpline(controlPoints);
        // when a control point is deleted the times around that control point should not change, and the path
        // should still be linear
        spline.deleteControlPoint(controlPoints.get(1));
        KochanekBartelsSpline.PathFollower follower = scaleSpeedTest.getPathFollower();
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
        // only one point in the spline - there is no where to inset a new control point
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
        final List<KochanekBartelsSpline.ControlPoint> controlPoints = new ArrayList<>();
        final KochanekBartelsSpline spline = createLinearTestSpline(controlPoints);
        // Insert a random point and things should be fine - i.e. It should still be a linear path over
        // the same path duration
        spline.insertControlPoint(1.0);
        KochanekBartelsSpline.PathFollower follower = scaleSpeedTest.getPathFollower();
        double time = 0.0;
        KochanekBartelsSpline.PathPoint pathPt;
        while (null != (pathPt = follower.getPointAt(time))) {
            assertEquals(time, pathPt.time, 0.00001);
            assertEquals(REF_SPEED, pathPt.speedStrafe, 0.00001);
            time += 0.1;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Test adding a robot action, and removing a robot action.
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Create a linear path with 4 control points; verify the second control point has no action; set an
     * action for the second control point and verify an action has been set and derivatives are reset to zero;
     * remove the action and verify it is removed and the derivatives are reset as expected.
     */
    @Test
    @DisplayName("Verify KochanekBartelsSpline.ControlPoint,setRobotAction() Operation")
    void AddRemoveRobotAction() {
        // build a known 4 control point linear path
        final List<KochanekBartelsSpline.ControlPoint> controlPoints = new ArrayList<>();
        final KochanekBartelsSpline spline = createLinearTestSpline(controlPoints);
        // there are no robot actions on this path, so asking for an action should return null.
        for (KochanekBartelsSpline.ControlPoint ctrlPt : spline.getControlPoints()) {
            assertNull(ctrlPt.getRobotAction());;
        }
        // release the specified tangent on control point 1 so we can get the default derivative for future tests
        controlPoints.get(1).resetDerivative();
        double defaultFreeXDerivative = controlPoints.get(1).getRawTangentX();

        // Add an action to the second control point. VEryfy this resets the robot velocity to zero so
        // derive control can hand aff to the action we re stopping for
        String actionCommand = "TestCommand";
        double approxDuration = 2.0;
        controlPoints.get(1).setRobotAction(actionCommand, approxDuration);
        KochanekBartelsSpline.RobotAction action = controlPoints.get(1).getRobotAction();
        assertNotNull(action);
        assertEquals(KochanekBartelsSpline.RobotActionType.STOP_AND_RUN_COMMAND, action.actionType);
        assertEquals(actionCommand, action.command);
        assertEquals(approxDuration, action.approxDuration);
        assertEquals(0.0, controlPoints.get(1).getRawTangentX());
        assertEquals(0.0, controlPoints.get(1).getRawTangentY());
        // Remove the action and confirm things go back to pretty normal (we saved the default implementation
        // computed derivative earlier for the comparison.
        controlPoints.get(1).setRobotAction(null, approxDuration);
        assertNull(controlPoints.get(1).getRobotAction());
        assertEquals(defaultFreeXDerivative, controlPoints.get(1).getRawTangentX());
        assertEquals(0.0, controlPoints.get(1).getRawTangentY());
    }

}
