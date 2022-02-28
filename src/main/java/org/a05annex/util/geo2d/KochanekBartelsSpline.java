package org.a05annex.util.geo2d;

import org.a05annex.util.AngleConstantD;
import org.a05annex.util.AngleD;
import org.a05annex.util.AngleUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Iterable;
import java.util.Iterator;

import static org.a05annex.util.JsonSupport.*;

/**
 * This is an implementation of the
 * <a href="https://en.wikipedia.org/wiki/Kochanek%E2%80%93Bartels_spline">Kochanek-Bartels Spline</a> designed
 * for interactive editing of the tangent vector to implicitly control bias and tension. The original reference
 * for this spline formulation can be found at
 * <a href="https://www.engr.colostate.edu/ECE455/Readings/TCB.pdf">Interpolating Splines
 * with Local Tension, Continuity, and Bias Control</a>. There is
 * no continuity control in this implementation as we expect robot movement to be continuous unless we end
 * this path and the robot stops to do something. In that case, we would expect to start a new path once
 * the action was complete.
 * <p>
 * When control points are created the tangent (derivatives) at that control point and surrounding control points are
 * computed using the <a href="https://en.wikipedia.org/wiki/Cubic_Hermite_spline">Cardinal-Spline</a> formulation
 * with the default tension specified by {@link #DEFAULT_TENSION}. The tangent is adjusted using a control handle
 * which intuitively manipulates the shape of the spline at the control point to implicitly edit tension and bias.
 * <p>
 * This class is primarily a container-editor for a doubly-linked list of control points, and a factory for
 * path point iterators (iterating through the path points), or path point followers (generating a series of
 * path points at specified times along the path).
 * <p>
 * <b>Handling Time:</b>
 * <p>
 * Time expansion/compression, as well as specified unequal time intervals between control points are useful
 * in modifying existing paths that generally work well, but could work better:
 * <ul>
 *      <li><b>expansion/compression</b> - The path time in the path definition is, for example, 10 seconds
 *      in duration. The path designer would like to speed it up to 9 or 8 seconds; or slow it down to 11 or 12 seconds.
 *      Instead of designing a new path with new control points, the path designer would like to scale the existing
 *      path to be faster or slower.</li>
 *      <li><b>repositioning control points in time</b> - The path is generally good, but, some control points
 *      should be reached sooner or later than the 1 second default interval. The path designer would like to
 *      reposition those points in time.</li>
 * </ul>
 * <p>
 * <b>Robot Actions:</b>
 * <p>
 * Robot actions: like starting a parallel command (i.e. a command that runs in parallel with path following like
 * start/stop collector); or stopping to run a serial command (i.e. stop and shoot, then resume the path) need to
 * be incorporated into the path description. To accommodate that we have added:
 * <ul>
 *      <li><b>to control points</b> - the option to
 *      specify that the robot should stop at the control point, run a command, and then resume the path. Additionally,
 *      if a stop and run command is specified, an approximate time for the command to run can be specified to aid in
 *      path planning for a specified duration autonomous period;</li>
 *      <li><b>path time actions</b> - The specification of actions (commands) that should happen parallel to the
 *      traversal of the path and be initiates at specific time along the path</li>
 * </ul>
 */
public class KochanekBartelsSpline {

    // -----------------------------------------------------------------------------------------------------------------
    // these are the keys for the JSON representation of the spline.
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String SPEED_MULTIPLIER = "speedMultiplier";
    static final String CONTROL_POINTS = "controlPoints";
    static final String FIELD_X = "fieldX";
    static final String FIELD_Y = "fieldY";
    static final String FIELD_HEADING = "fieldHeading";
    static final String TIME = "time";
    static final String LOCATION_DERIVATIVES_EDITED = "derivativesEdited";
    static final String FIELD_dX = "field_dX";
    static final String FIELD_dY = "field_dY";
    static final String FIELD_dHEADING = "field_dHeading";
    static final String ROBOT_ACTION_COMMAND = "robotActionCommand";
    static final String ROBOT_ACTION_DURATION = "robotActionDuration";
    static final String ROBOT_SCHEDULED_ACTIONS = "robotScheduledActions";
    static final String ROBOT_SCHEDULED_ACTION_TIME = "robotScheduledActionTime";

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * The length of the heading control handle in meters.
     */
    private static final double ROBOT_HEADING_HANDLE = 1.0;
    /**
     * In the formulation of the spline the tension scales the derivative. This was a tension selected for best
     * default appearance of the field path i.e. the default field path best represents the intent of the path
     * planner.
     */
    private static final double DEFAULT_TENSION = 0.85;
    /**
     * In the formulation of the spline the tension scales heading derivative. This was a tension selected for best
     * default behaviour of the heading interpolation i.e. the default heading interpolation best represents the
     * intent of the path planner.
     */
    private static final double DEFAULT_HEADING_TENSION = 0.55;
    /**
     * A scale factor applied to the derivative when computing the position of the editing handle.
     */
    private static final double DERIVATIVE_UI_SCALE = 0.5;

    /**
     * The default path delta in seconds, useful for drawing path curves on the field. The FRC control loop runs
     * at 20ms per cycle, or 50 samples per second). We found 20 samples per second (a sample every {@code 0.05}sec)
     * worked best for out interactive path planning tools.
     */
    public static final double DEFAULT_PATH_DELTA = 0.05;

    /**
     * The default title for a newly instantiated curve, {@code "untitled"}.
     */
    public static final String DEFAULT_TITLE = "untitled";

    /**
     * The default description for a newly instantiated curve, {@code "No description provided."}.
     */
    public static final String DEFAULT_DESCRIPTION = "No description provided.";

    /**
     * The basis matrix that provides the weighting of the [s] matrix (position on the segment of the spline to
     * various powers) as applied to the start and end positions and derivatives.
     */
    static final double[][] basis = {
            {2.0, -2.0, 1.0, 1.0},
            {-3.0, 3.0, -2.0, -1.0},
            {0.0, 0.0, 1.0, 0.0},
            {1.0, 0.0, 0.0, 0.0}
    };

    /**
     * The title of this path.
     */
    private String title = DEFAULT_TITLE;
    /**
     * The description of this path.
     */
    private String description = DEFAULT_DESCRIPTION;

    private double speedMultiplier = 1.0;
    /**
     * The first control point in this doubly-linked list of control points for the spline.
     */
    private ControlPoint first = null;
    /**
     * The last control point in this doubly-linked list of control points for the spline.
     */
    private ControlPoint last = null;
    /**
     * This is a time-sorted linked list of actions to be scheduled at specific times along the path.
     */
    private final ScheduledActionList scheduledActions = new ScheduledActionList();

    // -----------------------------------------------------------------------------------------------------------------
    // Robot actions - either:
    //  * Stop and perform some action, then resume (for planning purposes, we have an approximate duration)
    //  * Schedule a command to run in parallel with the path following
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * This enumeration represents the types of robot actions that can be initiated along the path.
     */
    public enum RobotActionType {
        /**
         * This is an action normally associated with a control point where the robot stops, path following
         * relinquishes the drive subsystem, and a command such as centering on the target and shooting is
         * performed. Once that command completes, the path follower resumes with time set back by however
         * long it took the action to complete.
         */
        STOP_AND_RUN_COMMAND,
        /**
         * This action schedules a command to happen concurrently with path following. Obviously, the
         * command must not require the drive subsystem, or this will interrupt path following - and will
         * not happen concurrently with path following.
         */
        SCHEDULE_COMMAND
    }

    /**
     * This is the specification of a robot action that should be initiated at some point on the path.
     */
    public static class RobotAction {
        /**
         * The type of action. The robot action type is specified as either:
         * <ul>
         *     <li>{@link RobotActionType#STOP_AND_RUN_COMMAND}: stop robot and run a command. Refer
         *     to the {@link RobotActionType} documentation for details;</li>
         *     <li>{@link RobotActionType#SCHEDULE_COMMAND}: schedule a command to run concurrently
         *     with path following. Refer to the {@link RobotActionType} documentation for details.</li>
         * </ul>
         */
        public final RobotActionType actionType;
        /**
         * The action class. It is assumed the action is a command in the <code>frc.robot.commands</code> of
         * a typical wpilib project with a no-argument constructor.
         */
        public final String command;
        /**
         * An approximate duration for a {@link RobotActionType#STOP_AND_RUN_COMMAND} {@link #command}. This is
         * used for path planning only. In competition the actual duration of the command may vary widely subject
         * to the demands of the competition. This field is meaningless for a
         * {@link RobotActionType#SCHEDULE_COMMAND} and should be set to {@code 0.0}.
         */
        public final double approxDuration;

        double pathTime;



        /**
         * Instantiate a schedule command action that should be performed in parallel with path following.
         *
         * @param pathTime The time along the path hen the command should be scheduled to execute.
         * @param command The name of the class (not including path info).
         */
        RobotAction(double pathTime, @NotNull String command) {
            this.actionType = RobotActionType.SCHEDULE_COMMAND;
            this.command = command;
            this.approxDuration = -1.0;
            this.pathTime = pathTime;
        }

        /**
         * Instantiate either a stop and run command.
         *
         * @param command        The name of the class (not including path info).
         * @param approxDuration An approximate duration for the command, used in path planning. Completely
         *                       ignored when the robot is following a path.
         */
        RobotAction(@NotNull String command, double approxDuration) {
            this.actionType = RobotActionType.STOP_AND_RUN_COMMAND;
            this.command = command;
            this.approxDuration = approxDuration;
            this.pathTime = -1.0;
        }
    }

    /**
     * This is a linked list of schedule commands ordered by when they should be scheduled in path time.
     */
    class ScheduledActionList {

        class ScheduledAction {
            final RobotAction robotAction;
            private ScheduledAction next = null;

            private ScheduledAction(@NotNull RobotAction robotAction, ScheduledAction next) {
                this.robotAction = robotAction;
                this.next = next;
            }
        }

        private ScheduledAction head = null;

        ScheduledActionList() {  }

        ScheduledAction getHead() {
            return head;
        }

        RobotAction scheduleAction(double pathTime, @NotNull String command) {
            RobotAction robotAction = new RobotAction(pathTime, command);
            if ((null == head) || (pathTime < head.robotAction.pathTime)) {
                // the first one, this is easy.
                head = new ScheduledAction(robotAction, head);
            } else {
                // step through the list until you find the right insertion point.
                ScheduledAction currentAction = head;
                while (null != currentAction) {
                    if ((null == currentAction.next) || (currentAction.next.robotAction.pathTime > pathTime)) {
                        // this action should be scheduled after the currentAction and before the
                        // next action.
                        currentAction.next = new ScheduledAction(robotAction, currentAction.next);
                        break;
                    }
                    currentAction = currentAction.next;
                }
            }
            return robotAction;
        }

        boolean deleteAction(@NotNull RobotAction robotAction) {
            if (null == head) {
                return false;
            }
            ScheduledAction lastScheduledAction = null;
            ScheduledAction nextScheduledAction = head;
            while (null != nextScheduledAction) {
                if ((nextScheduledAction.robotAction.pathTime == robotAction.pathTime) &&
                        (nextScheduledAction.robotAction.command.equals(robotAction.command))) {
                    // this is the action to delete
                    if (null == lastScheduledAction) {
                        head = nextScheduledAction.next;
                    } else {
                        lastScheduledAction.next = nextScheduledAction.next;
                    }
                    return true;
                }
                lastScheduledAction = nextScheduledAction;
                nextScheduledAction = lastScheduledAction.next;
            }
            return false;
        }

        void clear() {
            head = null;
        }

        void toJson(JSONArray jsonScheduledActions) {
            ScheduledAction scheduledAction = head;
            while (null != scheduledAction) {
                JSONObject jsonScheduledAction = new JSONObject();
                jsonScheduledAction.put(ROBOT_SCHEDULED_ACTION_TIME, scheduledAction.robotAction.pathTime);
                jsonScheduledAction.put(ROBOT_ACTION_COMMAND, scheduledAction.robotAction.command);
                jsonScheduledActions.add(jsonScheduledAction);
                scheduledAction = scheduledAction.next;
            }
        }

        void fromJson(@NotNull JSONArray scheduledActions) {
            for (Object scheduledAction : scheduledActions) {
                JSONObject saJson = (JSONObject)scheduledAction;
                double pathTime = parseDouble(saJson, ROBOT_SCHEDULED_ACTION_TIME, 0.0);
                String command = parseString(saJson, ROBOT_ACTION_COMMAND, "");
                scheduleAction(pathTime, command);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // PathPoint - a generated point on the path that includes expected field position and heading as well as
    // forward speed, strafe speed, and rotation speed for the robot.
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The representation of a generated point along the path. The path points is expressed as both the field
     * position and heading, and the derivative (velocities) of the field position and heading. The derivatives
     * are transformed to be robot-relative so they can be used to set the robot speeds for following this path.
     */
    public static class PathPoint {
        public final ControlPoint previousControlPoint;
        public final ControlPoint nextControlPoint;

        public final double time;
        /**
         * The point on the field where the robot should be when it reaches this point in the path.
         */
        public final Point2D.Double fieldPt;
        /**
         * The field heading for the robot when it reaches this point on the path
         */
        public final AngleConstantD fieldHeading;
        /**
         *
         */
        public final double field_dX;
        public final double field_dY;
        public final double field_dHeading;
        /**
         * The forward chassis velocity of the robot in meters/sec.
         */
        public final double speedForward;
        /**
         * The strafe chassis velocity of the robot in meters/sec.
         */
        public final double speedStrafe;
        /**
         * The rotation speed of the robot in radians/sec.
         */
        public final double speedRotation;

        public final RobotAction action;

        public PathPoint(@NotNull ControlPoint controlPoint, @NotNull RobotAction robotAction) {
            this.time = controlPoint.m_time;
            this.fieldPt = new Point2D.Double(controlPoint.m_fieldX, controlPoint.m_fieldY);
            this.fieldHeading = controlPoint.m_fieldHeading;
            this.field_dX = 0.0;
            this.field_dY = 0.0;
            this.field_dHeading = 0.0;
            this.speedForward = 0.0;
            this.speedStrafe = 0.0;
            this.speedRotation = 0.0;
            this.action = robotAction;
            this.previousControlPoint = controlPoint;
            this.nextControlPoint = controlPoint.m_next;
        }

        /**
         * Instantiate a Path Point.
         *
         * @param time                 The time this point on the path occurs.
         * @param fieldX               The expected field X position of the robot in meters.
         * @param fieldY               The expected field Y position of the robot in meters.
         * @param fieldHeading         The expected heading of the robot.
         * @param field_dX             The expected field dX velocity of the robot in meters/sec.
         * @param field_dY             The expected field dY velocity of the robot in meters/sec.
         * @param field_dHeading       The expected dHeading of the robot in radians/sec.
         * @param speedForward         The forward chassis speed of the robot in meters/sec.
         * @param speedStrafe          The strafe chassis velocity of the robot in meters/sec.
         * @param speedRotation        The rotation speed of the robot in radians/sec.
         * @param action               (Nullable) The action the robot should perform at this path point.
         * @param previousControlPoint (not null) The control point at the start of the curve segment
         *                             containing this path point.
         * @param nextControlPoint     (not null) The control point at the end of the curve segment
         *                             containing this path point.
         */
        public PathPoint(double time, double fieldX, double fieldY, AngleConstantD fieldHeading,
                         double field_dX, double field_dY, double field_dHeading,
                         double speedForward, double speedStrafe, double speedRotation,
                         @Nullable RobotAction action,
                         @NotNull ControlPoint previousControlPoint, @NotNull ControlPoint nextControlPoint) {
            this.time = time;
            this.fieldPt = new Point2D.Double(fieldX, fieldY);
            this.fieldHeading = fieldHeading;
            this.field_dX = field_dX;
            this.field_dY = field_dY;
            this.field_dHeading = field_dHeading;
            this.speedForward = speedForward;
            this.speedStrafe = speedStrafe;
            this.speedRotation = speedRotation;
            this.action = action;
            this.previousControlPoint = previousControlPoint;
            this.nextControlPoint = nextControlPoint;
        }

        /**
         * Test whether a field position (probably a mouse position during path editing) is over this path
         * point.
         *
         * @param fieldX    (double) The X coordinate of the test point.
         * @param fieldY    (double) The Y coordinate of the test point.
         * @param tolerance (double) The test tolerance - specifically, the distance from the actual field
         *                  position that the test point must be within to be considered a hit on the
         *                  path point.
         * @return Returns {@code true} if the test point is over the path point, and {@code false} otherwise.
         */
        public boolean testOverPathPoint(double fieldX, double fieldY, double tolerance) {
            double dx = fieldPt.getX() - fieldX;
            double dy = fieldPt.getY() - fieldY;
            return Math.sqrt((dx * dx) + (dy * dy)) < tolerance;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ControlPoint - a control point with heading and derivatives for the spline
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * This class represents a control point and has the operations that manipulate a control point in context with
     * its surrounding control points. Note that control points are maintained as a doubly-linked list because
     * manipulation of a control point affects derivatives of the adjacent control points.
     */
    public static class ControlPoint {
        final KochanekBartelsSpline m_path;
        ControlPoint m_next = null;
        ControlPoint m_last = null;
        double m_fieldX = 0.0;
        double m_fieldY = 0.0;
        AngleD m_fieldHeading = new AngleD(AngleD.ZERO);
        double m_time;
        // Have the location derivatives been edited? If so, then the user has explicitly set the derivatives,
        //  and they must be maintained, otherwise, derivatives are recomputed as control points are moved around.
        boolean m_locationDerivativesEdited = false;
        double m_dX = 0.0;
        double m_dY = 0.0;
        double m_dHeading = 0.0;
        // This is a robot action to stop at this control point and do something
        String m_robotAction = null;
        double m_actionDuration = 0.0;
        // These are never saved, they are always computed based on the saved tangents and the times of this
        // point and the surrounding points.
        double m_dXin = 0.0;
        double m_dXout = 0.0;
        double m_dYin = 0.0;
        double m_dYout = 0.0;
        double m_dHeadingIn = 0.0;
        double m_dHeadingOut = 0.0;

        /**
         * Instantiate a control point and set the time this control point should be reached when the path
         * is traversed.
         *
         * @param path (not null, KochanekBartelsSpline) The path this is a control point for.
         * @param timeInSec (double) The time this control point should be reached (in seconds).
         */
        public ControlPoint(@NotNull  KochanekBartelsSpline path, double timeInSec) {
            m_path = path;
            m_time = timeInSec;
        }

        /**
         * Instantiate a control point read from a JSON file.
         *
         * @param path (not null, KochanekBartelsSpline) The path this is a control point for.
         * @param json (not null, JSONObject) The JSONObject to read the control point from.
         */
        public ControlPoint(@NotNull  KochanekBartelsSpline path, @NotNull JSONObject json) {
            m_path = path;
            m_fieldX = parseDouble(json, FIELD_X, 0.0);
            m_fieldY = parseDouble(json, FIELD_Y, 0.0);
            m_fieldHeading.setValue(AngleUnit.RADIANS, parseDouble(json, FIELD_HEADING, 0.0));
            m_time = parseDouble(json, TIME, 0.0);
            m_locationDerivativesEdited = parseBoolean(json, LOCATION_DERIVATIVES_EDITED, false);
            m_dX = parseDouble(json, FIELD_dX, 0.0);
            m_dY = parseDouble(json, FIELD_dY, 0.0);
            m_dHeading = parseDouble(json, FIELD_dHEADING, 0.0);
            m_robotAction = parseString(json, ROBOT_ACTION_COMMAND, null);
            m_actionDuration = parseDouble(json, ROBOT_ACTION_DURATION, 0.0);
        }

        /**
         * Create a JSONObject and write a control point to that JSONObject.
         *
         * @return (not null, JSONObject) The JSONObject containing this control point.
         */
        @SuppressWarnings("unchecked")
        public @NotNull JSONObject toJSON() {
            JSONObject controlPoint = new JSONObject();
            controlPoint.put(FIELD_X, m_fieldX);
            controlPoint.put(FIELD_Y, m_fieldY);
            controlPoint.put(FIELD_HEADING, m_fieldHeading.getRadians());
            controlPoint.put(TIME, m_time);
            controlPoint.put(LOCATION_DERIVATIVES_EDITED, m_locationDerivativesEdited);
            controlPoint.put(FIELD_dX, m_dX);
            controlPoint.put(FIELD_dY, m_dY);
            controlPoint.put(FIELD_dHEADING, m_dHeading);
            if (null != m_robotAction) {
                controlPoint.put(ROBOT_ACTION_COMMAND, m_robotAction);
                controlPoint.put(ROBOT_ACTION_DURATION, m_actionDuration);
            }
            return controlPoint;
        }

        /**
         * Get the next control point on this curve.
         *
         * @return (nullable) The next control point on this curve. {@code null} if this is the last control
         * point on the curve.
         */
        @Nullable
        public ControlPoint getNext() {
            return m_next;
        }

        /**
         * Get the last (previous) control point on this curve.
         *
         * @return (nullable) The last (previous) point on this curve. {@code null} if this is the first control
         * point on the curve.
         */
        @Nullable
        public ControlPoint getLast() {
            return m_last;
        }

        /**
         * Restore the control point to automated derivative recalculation when it or surrounding
         * control points are moved. This only effects points whose derivatives have been manually edited.
         */
        public void resetDerivative() {
            if (m_locationDerivativesEdited) {
                m_locationDerivativesEdited = false;
                updateLocationDerivatives();
                updateHeadingDerivative();
            }
        }

        /**
         * Get information about whether the field velocity (derivatives of field position) have been
         * manually edited.
         *
         * @return {@code true} if the velocity has been manually edited, {@code false} otherwise.
         */
        public boolean getDerivativesManuallyEdited() {
            return m_locationDerivativesEdited;
        }

        /**
         * Get the field X position of the control point.
         *
         * @return (double) The field X position.
         */
        public double getFieldX() {
            return m_fieldX;
        }

        /**
         * Get the field Y position of the control point.
         *
         * @return (double) The field Y position.
         */
        public double getFieldY() {
            return m_fieldY;
        }

        public double getRawTangentX() {
            return m_dX;
        }

        public double getRawTangentY() {
            return m_dY;
        }

        /**
         * Set the field location of a control point to the specified point. This method forwards the request
         * to {@link #setFieldLocation(double, double)} which documents the actual behaviour.
         *
         * @param pt (not null, Point2D) The new field location for the point.
         */
        public void setFieldLocation(Point2D pt) {
            setFieldLocation(pt.getX(), pt.getY());
        }

        /**
         * Set the field location of a control point to the specified point {@code fieldX, fieldY}
         * position. This method is normally called during an interactive manipulation of the location
         * handle. When the position of a control point is updated the derivatives of that point and the
         * control points before and after that point are recomputed. If the derivatives for any of those
         * points has been previously manually edited, the manually edited derivatives are preserved.
         *
         * @param fieldX (double) The new field X position.
         * @param fieldY (double) The new field Y position.
         */
        public void setFieldLocation(double fieldX, double fieldY) {
            m_fieldX = fieldX;
            m_fieldY = fieldY;
            // update the derivatives
            updateLocationDerivatives();
            if (m_last != null) {
                m_last.updateLocationDerivatives();
                if ((m_last.m_last != null) && (m_last.m_last.m_last == null)) {
                    // changing these velocities may affect the start point
                    m_last.m_last.updateLocationDerivatives();
                }
            }
            if (m_next != null) {
                m_next.updateLocationDerivatives();
                if ((m_next.m_next != null) && (m_next.m_next.m_next == null)) {
                    // changing these velocities may affect the start point
                    m_next.m_next.updateLocationDerivatives();
                }
            }
        }

        /**
         * Recompute the derivatives for this control point. If the derivatives have been previously manually
         * edited this request is ignored and the manually edited derivatives are preserved.
         */
        private void updateLocationDerivatives() {
            // NOTE: If the derivative has been edited, then we assume the edited derivative is the intended
            // derivative and should not be recomputed when the control point is moved.
            if (!m_locationDerivativesEdited) {
                double fieldXprev = m_fieldX;
                double fieldYprev = m_fieldY;
                double fieldXnext = m_fieldX;
                double fieldYnext = m_fieldY;

                if (m_last != null) {
                    // There is a previous point
                    fieldXprev = m_last.m_fieldX;
                    fieldYprev = m_last.m_fieldY;
                } else if ((m_next != null) && (m_next.m_next != null)) {
                    // we are going to manufacture a previous point from the position of the next point
                    Vector2d chord = new Vector2d(m_fieldX, m_fieldY, m_next.m_fieldX, m_next.m_fieldY);
                    if (chord.length() > Vector2d.ZERO_TOLERANCE) {
                        chord.normalize();
                        Vector2d nextVelocityVector = new Vector2d(m_next.m_dX, m_next.m_dY);
                        double nextVelocity = nextVelocityVector.length();
                        // so here is an interesting corner case - if  dx and dy are zero (i.e. we stop at the control
                        // point) then nextVelocity will be zero, so we have a divide by zero. In that case we set the
                        // derivatives to zero.
                        if (nextVelocity > Vector2d.ZERO_TOLERANCE) {
                            double dot = chord.dot(nextVelocityVector.scale(1.0 / nextVelocity));
                            Vector2d difference = new Vector2d(nextVelocityVector, chord.scale(dot), Vector2d.VECTOR_SUBTRACT);
                            Vector2d thisVelocityVector =
                                    new Vector2d(chord, difference, Vector2d.VECTOR_ADD).scale(nextVelocity);
                            m_dX = thisVelocityVector.getI();
                            m_dY = thisVelocityVector.getJ();
                        } else {
                            m_dX = 0.0;
                            m_dY = 0.0;
                        }
                        return;
                    }
                }

                if (m_next != null) {
                    fieldXnext = m_next.m_fieldX;
                    fieldYnext = m_next.m_fieldY;
                } else if ((m_last != null) && (m_last.m_last != null)) {
                    // we are going to manufacture a next point from the position of the last point
                    Vector2d chord = new Vector2d(m_last.m_fieldX, m_last.m_fieldY, m_fieldX, m_fieldY);
                    if (chord.length() > Vector2d.ZERO_TOLERANCE) {
                        chord.normalize();
                        Vector2d lastVelocityVector = new Vector2d(m_last.m_dX, m_last.m_dY);
                        double lastVelocity = lastVelocityVector.length();
                        if (lastVelocity > Vector2d.ZERO_TOLERANCE) {
                            double dot = chord.dot(lastVelocityVector.scale(1.0 / lastVelocity));
                            Vector2d difference = new Vector2d(lastVelocityVector, chord.scale(dot), Vector2d.VECTOR_SUBTRACT);
                            Vector2d thisVelocityVector =
                                    new Vector2d(chord, difference, Vector2d.VECTOR_ADD).scale(lastVelocity);
                            m_dX = thisVelocityVector.getI();
                            m_dY = thisVelocityVector.getJ();
                        } else {
                            m_dX = 0.0;
                            m_dY = 0.0;
                        }
                        return;
                    }
                }
                m_dX = DEFAULT_TENSION * (fieldXnext - fieldXprev);
                m_dY = DEFAULT_TENSION * (fieldYnext - fieldYprev);
            }
        }

        /**
         * Get the field X location for the display of af a tangent editing handle for this control point.
         *
         * @return (double) The field X location of the handle for editing the tangent of the control point.
         */
        public double getTangentX() {
            return m_fieldX + (DERIVATIVE_UI_SCALE * m_dX);
        }

        /**
         * Get the field Y location for the display of af a tangent editing handle for this control point.
         *
         * @return (double) The field Y location of the handle for editing the tangent of the control point.
         */
        public double getTangentY() {
            return m_fieldY + (DERIVATIVE_UI_SCALE * m_dY);
        }

        public void setTangent(double dX, double dY) {
            m_dX = dX;
            m_dY = dY;
            m_locationDerivativesEdited = true;
            // update the derivatives
            updateLocationDerivatives();
            if (m_last != null) {
                m_last.updateLocationDerivatives();
            }
            if (m_next != null) {
                m_next.updateLocationDerivatives();
            }
        }

        private void pkgComputeTangentInOut() {
            if (m_next != null) {
                double outScale = m_next.m_time - m_time;
                m_dXout = m_dX * outScale;
                m_dYout = m_dY * outScale;
                m_dHeadingOut = m_dHeading * outScale;
            }

            if (m_last != null) {
                double inScale = m_time - m_last.m_time;
                m_dXin = m_dX * inScale;
                m_dYin = m_dY * inScale;
                m_dHeadingIn = m_dHeading * inScale;
            }

        }

        /**
         * Set the tangent location, used during interactive curve editing. This method forwards the request
         * to {@link #setTangentLocation(double, double)} which documents the actual behaviour.
         *
         * @param pt (not null, Point2D) The new field location for the tangent editing handle.
         */
        public void setTangentLocation(Point2D pt) {
            setTangentLocation(pt.getX(), pt.getY());
        }

        /**
         * Set the tangent location, used during interactive curve editing. This method is normally called during
         * an interactive manipulation of the tangent handle. When the tangent is manipulated through this method
         * it is marked as manually manipulated, and it will not be automatically updated as this or adjacent
         * control points are moved.
         *
         * @param fieldX (double) The new field X position of the tangent handle.
         * @param fieldY (double) The new field Y position of the tangent handle.
         */
        public void setTangentLocation(double fieldX, double fieldY) {
            setTangent((fieldX - m_fieldX) / DERIVATIVE_UI_SCALE, (fieldY - m_fieldY) / DERIVATIVE_UI_SCALE);
        }

        public AngleD getFieldHeading() {
            return m_fieldHeading;
        }

        /**
         * Get the field X location for the display af a heading editing handle for this control point.
         *
         * @return (not null, double) The field X location for the heading editing handle.
         */
        public double getHeadingX() {
            return m_fieldX + (ROBOT_HEADING_HANDLE * m_fieldHeading.sin());
        }

        /**
         * Get the field Y location for the display af a heading editing handle for this control point.
         *
         * @return (not null, double) The field Y location for the heading editing handle.
         */
        public double getHeadingY() {
            return m_fieldY + (ROBOT_HEADING_HANDLE * m_fieldHeading.cos());
        }

        /**
         * Set the robot heading vector to go through the specified point at this control point. NOTE: the
         * heading handle/display is a fixed distance from the centroid of the robot. The specified {@code pt}
         * is taken to be a point somewhere along the heading vector, and the heading handle will be repositioned
         * to be on that heading vector.
         *
         * @param pt (not null, Point2D) A point through which the heading vector from the centroid of the
         *           robot should pass.
         */
        public void setHeadingLocation(Point2D pt) {
            // OK, the simple action here is to look at the current mouse position relative to the control
            // point position, use the atan2, and get a heading. However, tis does not handle the -180/180 degree
            // transition, so we need some logic like the NavX logic. for passing over the boundary
            setFieldHeading(new AngleD().atan2(pt.getX() - m_fieldX, pt.getY() - m_fieldY));
        }

        /**
         * Set the heading direction for this control point.
         *
         * @param heading (AngleConstantD) The heading direction for this control point.
         */
        public void setFieldHeading(AngleConstantD heading) {
            AngleD adjustedHeading = new AngleD(heading);
            while ((adjustedHeading.getRadians() - m_fieldHeading.getRadians()) > Math.PI) {
                adjustedHeading.subtract(AngleD.TWO_PI);
            }
            while ((adjustedHeading.getRadians() - m_fieldHeading.getRadians()) < -Math.PI) {
                adjustedHeading.add(AngleD.TWO_PI);
            }
            m_fieldHeading = adjustedHeading;
            // update the derivatives
            updateHeadingDerivative();
            if (m_last != null) {
                m_last.updateHeadingDerivative();
            }
            if (m_next != null) {
                m_next.updateHeadingDerivative();
            }
        }

        /**
         *
         */
        private void updateHeadingDerivative() {
            // this is a bit different than the path field position derivative because the path derivative
            // is displayed and editable - until we figure out how to do that with the heading derivative
            // we need to figure out how to best handle the first and last point.
            if ((null == m_last) && (null == m_next)) {
                m_dHeading = 0.0;
            } else {
                double fieldHeadingPrev = m_last != null ?
//                        last.m_fieldHeading : m_fieldHeading - (m_next.m_fieldHeading - m_fieldHeading);
                        m_last.m_fieldHeading.getRadians() :
                        m_fieldHeading.getRadians() -
                                (m_next.m_fieldHeading.getRadians() - m_fieldHeading.getRadians());
                double fieldHeadingNext = m_next != null ?
//                        m_next.m_fieldHeading : m_fieldHeading + (m_fieldHeading - last.m_fieldHeading);
                        m_next.m_fieldHeading.getRadians() :
                        m_fieldHeading.getRadians() +
                                (m_fieldHeading.getRadians() - m_last.m_fieldHeading.getRadians());
                m_dHeading = DEFAULT_HEADING_TENSION * (fieldHeadingNext - fieldHeadingPrev);
            }
        }

        /**
         * Get the time for this control point.
         *
         * @return The time this point on the path will be reached.
         */
        public double getTime() {
            return m_time;
        }

        /**
         * Reset the time for this control point, Note, the time for the first control point must always be 0.0,
         * and cannot be changed.
         *
         * @param time      The new time, in seconds, for this control point. The time must be greater than the time
         *                  of the last (previous) control point, and less than the time of the next control point.
         * @param propagate {@code false} if the new time applies only to this point, {@code true} if the
         *                  time change should propagate to all control points after this point. For
         *                  example, if you had a path with 4 control points at times (0.0, 1.0, 2.0, 3.0) and
         *                  you set the time of the second control point to 1.1: then without propagation only
         *                  the second control point time will be modified as (0.0, 1.1, 2.0, 3.0); with propagation
         *                  the following control points will also be shifted by 0.1 as (0.0, 1.1, 2.1, 3.1).
         */
        public void setTime(double time, boolean propagate) {
            if (null == m_last) {
                throw new IllegalArgumentException("The time of the first control point cannot be reset.");
            }
            if (time <= m_last.m_time) {
                throw new IllegalArgumentException(
                        "The time must be greater than the time of the previous control point.");
            }
            if ((null != m_next) && time >= m_next.m_time) {
                throw new IllegalArgumentException("The time must be less than the time of the next control point.");
            }
            double propagationStartTime = m_time;
            double delta = time - m_time;
            m_time = time;
            if (propagate) {
                // We are propagating this time change through the path following this control point.
                // Loop through the control points after this control point and add 'delta' to
                //    their time.
                ControlPoint controlPoint = this;
                while (null != controlPoint.m_next) {
                    controlPoint = controlPoint.m_next;
                    controlPoint.m_time += delta;
                }
                // Loop through scheduled commands and for commands after this control point add
                //    'delta' to their time.
                ScheduledActionList.ScheduledAction scheduledAction = controlPoint.m_path.scheduledActions.head;
                while (null != scheduledAction) {
                    if (scheduledAction.robotAction.pathTime > propagationStartTime) {
                        scheduledAction.robotAction.pathTime += delta;
                    }
                    scheduledAction = scheduledAction.next;
                }
            }
        }

        /**
         * Set (or unset)  a {@link RobotActionType#STOP_AND_RUN_COMMAND} to be executed at this control
         * point. If the {@code CommandName} is not <code>null</code>. this method sets that action and
         * sets the control points derivatives to <code>0.0</code> (the robot is stopped) to execute
         * the command); and if <code>null</code>,restores derivatives to default control and
         * <code>null</code>'s any actions associated with this control point.
         *
         * @param commandName         The name of the action to be performed (a class in the
         *                            <code>frc.robot.command</code> package with a no-argument constructor).
         * @param approximateDuration The approximate duration of the command (in seconds) to be used
         *                            only in path planning applications.
         */
        public void setRobotAction(@Nullable String commandName, double approximateDuration) {
            // apply this action only if something has changed
            if (null != commandName) {
                // Here we stop, relinquish drive, and do something, then resume,
                m_robotAction = commandName;
                setTangent(0.0, 0.0);    // we are not moving
                m_actionDuration = approximateDuration;
            } else if (null != m_robotAction) {
                // This is releasing the 'stop and run command' constraint, so the derivatives go back the
                // default computations of the derivatives
                m_robotAction = null;
                resetDerivative();
                m_actionDuration = 0.0;
            }
        }

        /**
         * Get the robot action that should be performed at this control point.
         *
         * @return The robot action, or <code>null</code> is no action, other than continuing along the path,
         * should be performed.
         */
        public RobotAction getRobotAction() {
            return (null == m_robotAction) ? null : new RobotAction(m_robotAction, m_actionDuration);
        }

        /**
         * Test whether a field position (probably a mouse position during path editing) is over this control
         * point.
         *
         * @param fieldX    (double) The X coordinate of the test point.
         * @param fieldY    (double) The Y coordinate of the test point.
         * @param tolerance (double) The test tolerance - specifically, the distance from the actual field
         *                  position that the test point must be within to be considered a hit on the
         *                  control point position.
         * @return Returns {@code true} if the test X,Y is over the control point, and {@code false} otherwise.
         */
        public boolean testOverControlPoint(double fieldX, double fieldY, double tolerance) {
            double dx = m_fieldX - fieldX;
            double dy = m_fieldY - fieldY;
            return Math.sqrt((dx * dx) + (dy * dy)) < tolerance;
        }

        /**
         * Test whether a field position (probably a mouse position during path editing) is over this control
         * point tangent editing handle.
         *
         * @param fieldX    (double) The X coordinate of the test point.
         * @param fieldY    (double) The Y coordinate of the test point.
         * @param tolerance (double) The test tolerance - specifically, the distance from the actual field
         *                  position that the test point must be within to be considered a hit on the
         *                  control point tangent handle.
         * @return Returns {@code true} if the test point is over the tangent point, and {@code false} otherwise.
         */
        public boolean testOveTangentPoint(double fieldX, double fieldY, double tolerance) {
            double dx = getTangentX() - fieldX;
            double dy = getTangentY() - fieldY;
            return Math.sqrt((dx * dx) + (dy * dy)) < tolerance;
        }

        /**
         * Test whether a field position (probably a mouse position during path editing) is over this control
         * point robot heading control handle.
         *
         * @param fieldX    (double) The X coordinate of the test point.
         * @param fieldY    (double) The Y coordinate of the test point.
         * @param tolerance (double) The test tolerance - specifically, the distance from the actual field
         *                  position that the test point must be within to be considered a hit on the
         *                  control point heading handle.
         * @return Returns {@code true} if the test point is over the heading point, and {@code false} otherwise.
         */
        public boolean testOverHeadingPoint(double fieldX, double fieldY, double tolerance) {
            double dx = getHeadingX() - fieldX;
            double dy = getHeadingY() - fieldY;
            return Math.sqrt((dx * dx) + (dy * dy)) < tolerance;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * This class is an iterator over the control points of the path. It is normally used in path editing
     * programs that needs to display some representation of the control points.
     */
    public class ControlPointIterator implements Iterator<ControlPoint>, Iterable<ControlPoint> {

        private ControlPoint m_current = first;

        /**
         * Instantiate the control point iterator.
         */
        private ControlPointIterator() {
        }


        @Override
        public boolean hasNext() {
            return m_current != null;
        }

        @Override
        public ControlPoint next() {
            ControlPoint current = m_current;
            m_current = current.m_next;
            return current;
        }

        @NotNull
        @Override
        public Iterator<ControlPoint> iterator() {
            return this;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * This is an abstract class that handles the mechanics of generating points along a path described by this
     * spline. This base class assumes that points along the path will be evaluated with an ever increasing
     * time and support a {@link PathIterator} that generates points at a defined time interval (useful in path
     * display during planning), and {@link PathFollower} that generates points at the requested time (useful
     * in animation or when the robot is in a command loop to generate the points at whatever time they are
     * requested).
     * <p>
     * The path is divided into segments, each having a start and end control point. As points are generated
     * this class has a method to update the segment start-end parameters whenever a control point boundary
     * is passed. The implementing class is responsible for calling this method at the appropriate time.
     */
    private abstract class PathGenerator {
        /**
         * The start of the segment being generated, will be {@code null} if no control points have been defined.
         */
        ControlPoint thisSegmentStart = first;
        /**
         * The end of the segment being generated, will be{@code null} if less than 2 control points have been
         * defined.
         */
        ControlPoint thisSegmentEnd = first == null ? null : first.m_next;
        /**
         * The control point location, heading, derivatives matrix for this segment of
         */
        final double[][] segment = {
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0}
        };

        final double speedMultiplier;

        boolean firstPoint = true;

        ScheduledActionList.ScheduledAction nextAction = scheduledActions.getHead();

        PathGenerator(double speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
            if (first != null) {
                first.pkgComputeTangentInOut();
            }
            resetSegment();
        }

        /**
         * Reset the {@link #segment} matrix when the control points for the segment are advanced.
         */
        protected void resetSegment() {
            if (null == thisSegmentEnd) {
                // we are done with this spline, just return.
                return;
            }
            thisSegmentEnd.pkgComputeTangentInOut();
            segment[0][0] = thisSegmentStart.m_fieldX;
            segment[1][0] = thisSegmentEnd.m_fieldX;
            segment[2][0] = thisSegmentStart.m_dXout;
            segment[3][0] = thisSegmentEnd.m_dXin;
            segment[0][1] = thisSegmentStart.m_fieldY;
            segment[1][1] = thisSegmentEnd.m_fieldY;
            segment[2][1] = thisSegmentStart.m_dYout;
            segment[3][1] = thisSegmentEnd.m_dYin;
            segment[0][2] = thisSegmentStart.m_fieldHeading.getRadians();
            segment[1][2] = thisSegmentEnd.m_fieldHeading.getRadians();
            segment[2][2] = thisSegmentStart.m_dHeadingOut;
            segment[3][2] = thisSegmentEnd.m_dHeadingIn;
        }

        /**
         * Generate a {@link PathPoint} in the context of a path iterator or path follower for this spline
         * for the specified time along the path. NOTE: The time must be monotonically increasing
         *
         * @param time The time, in seconds, along the path for which the {@link PathPoint} will be generated.
         * @return Returns the {@link PathPoint} for the specified time, or {@code null} if the time is beyond
         * the last control point in the path.
         */
        protected PathPoint getPointOnSegment(double time) {
            RobotAction robotAction = null;
            if (firstPoint) {
                // if this is a first point
                firstPoint = false;
                robotAction = thisSegmentStart.getRobotAction();
                // and there is a stop and do something action
                if (null != robotAction) {
                    return new PathPoint(thisSegmentStart, robotAction);
                }
            }
            double pathTime = time * speedMultiplier;
            while (pathTime > thisSegmentEnd.m_time) {
                // past the end of this segment, move on to the next.
                thisSegmentStart = thisSegmentEnd;
                thisSegmentEnd = thisSegmentStart.m_next;
                resetSegment();
                robotAction = thisSegmentStart.getRobotAction();
                if (null != robotAction) {
                    return new PathPoint(thisSegmentStart, robotAction);
                }
                if (null == thisSegmentEnd) {
                    // No more segments, we are done. However, it could be that there is an action at the
                    // last control point
                    return null;
                }
            }
            // create and return the path point
            double sValue = (pathTime - thisSegmentStart.m_time) / (thisSegmentEnd.m_time - thisSegmentStart.m_time);
            // get the next point on the curve
            // The s[] vector is s to the third, second, first, and 0th power
            double[] s = {sValue * sValue * sValue, sValue * sValue, sValue, 1.0};
            // the ds[] vector is the derivative of the s[] vector
            double[] ds = {3.0 * sValue * sValue, 2.0 * sValue, 1.0, 0.0};
            // This first transformation multiples the s and derivative s matrices by the
            // the basis functions to get the weights that are applied to the segment start and
            // end point
            double[] weights = {0.0, 0.0, 0.0, 0.0};
            double[] dWeights = {0.0, 0.0, 0.0, 0.0};
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    weights[i] += s[j] * basis[j][i];
                    dWeights[i] += ds[j] * basis[j][i];
                }
            }
            // Now that we have the weights, we multiply the field positions and derivatives of the
            // start and end control points by the weights to get the expected field position-heading and
            // the derivatives - the calculated robot forward, strafe, and rotation speeds necessary to
            // that should be happening at this point.
            double[] field = {0.0, 0.0, 0.0};
            double[] dField = {0.0, 0.0, 0.0};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    field[i] += weights[j] * segment[j][i];
                    dField[i] += dWeights[j] * segment[j][i];
                }
            }

            // Correct the derivatives for time
            for (int i = 0; i < 3; i++) {
                dField[i] /= (thisSegmentEnd.m_time - thisSegmentStart.m_time);
            }
            // OK, the position derivatives are X and Y relative to the field. These need to be transformed to
            // robot relative forward and strafe.
            double sinHeading = Math.sin(field[2]);
            double cosHeading = Math.cos(field[2]);
            double forward = ((dField[0] * sinHeading) + (dField[1] * cosHeading)) * speedMultiplier;
            double strafe = ((dField[0] * cosHeading) - (dField[1] * sinHeading)) * speedMultiplier;
            // create and return the path point
            robotAction  = null;
            if ((null != nextAction) && (nextAction.robotAction.pathTime <= pathTime)) {
                robotAction = nextAction.robotAction;
                nextAction = nextAction.next;
            }
            return new PathPoint(time, field[0], field[1], new AngleD(AngleUnit.RADIANS, field[2]),
                    dField[0], dField[1], dField[2],
                    forward, strafe, dField[2] * speedMultiplier,
                    robotAction, thisSegmentStart, thisSegmentEnd);
        }
    }

    /**
     * An iterator for points along a path at a specified time interval.
     */
    public class PathIterator extends PathGenerator implements Iterator<PathPoint>, Iterable<PathPoint> {
        /**
         * The current position on the segment being generated, from 0.0 being on {@link #thisSegmentStart}
         * to 1.0 being on {@link #thisSegmentEnd}.
         */
        double m_time;
        /**
         * The point spacing increment on the curve.
         */
        final double m_deltaTime;

        /**
         * Instantiate a path iterator that will generate points on the path at the specified time interval.
         *
         * @param deltaTime The ime interval, in seconds, at which the iterator will return {@link PathPoint}s.
         */
        private PathIterator(double deltaTime, double speedMultiplier) {
            super(speedMultiplier);
            m_time = 0.0;
            m_deltaTime = deltaTime;
        }

        /**
         * Test whether there is a next point in the path.
         *
         * @return {@code true} if there is a next point, {@code false} otherwise.
         */
        @Override
        public boolean hasNext() {
            return (null != thisSegmentEnd) && ((m_time * speedMultiplier) <= last.m_time);
        }

        /**
         * Get the next point in the path.
         *
         * @return The next point in the path, {@code null} if there is no next point in the path.
         */
        @Override
        public PathPoint next() {
            // get the next point on the curve
            PathPoint pathPoint = getPointOnSegment(m_time);
            // get ready for the next point
            m_time += m_deltaTime;
            // create and return the path point
            return pathPoint;
        }

        /**
         * Get a {@link PathPoint} iterator.
         *
         * @return A {@link PathPoint} iterator for this curve.
         */
        @NotNull
        @Override
        public Iterator<PathPoint> iterator() {
            return this;
        }

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A follower that will generate points along the path from start to finish as described by the
     * time for the point on the path. The time must be monotonically increasing. The follower is designed
     * for use in the robot control loop where the interval between calls may vary.
     */
    public class PathFollower extends PathGenerator {
        /**
         * Instantiate a path follower.
         */
        private PathFollower(double speedMultiplier) {
            super(speedMultiplier);
        }

        /**
         * Get the path point at the specified time.
         *
         * @param time The time, in seconds, along the path for which the {@link PathPoint} will be generated.
         * @return Returns the {@link PathPoint} for the specified time. Returns {@code null} if the time is
         * past the end of the end of the path (the time of the last control point on the path)..
         */
        public PathPoint getPointAt(double time) {
            // get the next point on the curve
            if (thisSegmentEnd == null) {
                return null;
            }
            // create and return the path point
            return getPointOnSegment(time);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // This is the actual implementation of the spline object - which is really just a manager of the control
    // points of the spline and a factory for the PathIterator and PathFollower.
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The implementation of the spline object. The spline object that manages the set of control points and
     * is a factory for path iterators and path followers.
     */
    public KochanekBartelsSpline() {
    }

    /**
     * Set the title for this curve. Note, this will appear in the JSON representation of the curve.
     *
     * @param title The title for this curve.
     */
    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    /**
     * Get the title for this curve.
     *
     * @return The title of this object, defaults to
     * {@code "untitled"}, see {@link #DEFAULT_DESCRIPTION}.
     */
    @NotNull
    public String getTitle() {
        return title;
    }

    /**
     * Set the description for this curve. Note, this will appear in the JSON representation of the curve.
     *
     * @param description The description for this curve.
     */
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    /**
     * Get the description for this curve.
     *
     * @return The description of this curve, defaults to
     * {@code "No description provided."}, see {@link #DEFAULT_DESCRIPTION}.
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Add a control point to the end of the path, which will extend that path to that new control point. This
     * method defers to {@link #addControlPoint(double, double, AngleConstantD)} with a heading of (@code 0.0}.
     *
     * @param pt The field location of the new control point.
     * @return Returns the added control point.
     */
    @NotNull
    public ControlPoint addControlPoint(@NotNull Point2D pt) {
        return addControlPoint(pt.getX(), pt.getY(), AngleD.ZERO);
    }

    /**
     * Add a control point to the end of the path, which will extend that path to that new control point. This
     * method defers to {@link #addControlPoint(double, double, AngleConstantD)} with a heading of 0.0.
     *
     * @param fieldX The X field position for the added control point.
     * @param fieldY The field Y position for the added control point.
     * @return Returns the added control point.
     */
    @NotNull
    public ControlPoint addControlPoint(double fieldX, double fieldY) {
        return addControlPoint(fieldX, fieldY, AngleD.ZERO);
    }

    /**
     * Add a control point to the end of the path, which will extend the path to that new control point at a time
     * of 1.0 seconds after the end of the current path. If this is the first control point added to the path the
     * time will be 0.0.
     *
     * @param fieldX       The X field position for the added control point.
     * @param fieldY       The field Y position for the added control point.
     * @param fieldHeading The heading for the added control point.
     * @return Returns the added control point.
     */
    @NotNull
    public ControlPoint addControlPoint(double fieldX, double fieldY, AngleConstantD fieldHeading) {
        return addControlPoint(fieldX, fieldY, fieldHeading, (null == last) ? 0.0 : last.m_time + 1.0);
    }

    /**
     * Add a control point to the end of the path, which will extend that path to that new control point.
     *
     * @param fieldX       The X field position for the added control point.
     * @param fieldY       The field Y position for the added control point.
     * @param fieldHeading The heading for the added control point.
     * @param time         The time the added control point should be reached. This should be greater than the time
     *                     of the last control point, and will be set to time of the last control point + 1.0 if
     *                     not. If this is the first control point of the path the time will be set to 0.0.
     * @return Returns the added control point.
     */
    @NotNull
    public ControlPoint addControlPoint(double fieldX, double fieldY, AngleConstantD fieldHeading, double time) {
        // make the time valid
        if (null == last) {
            time = 0.0;
        } else if (time <= last.m_time) {
            time = last.m_time + 1.0;
        }

        // add the point to the end of the curve
        ControlPoint newControlPoint = new ControlPoint(this, time);
        appendControlPoint(newControlPoint);
        newControlPoint.setFieldLocation(fieldX, fieldY);
        newControlPoint.setFieldHeading(new AngleD(fieldHeading));
        return newControlPoint;
    }

    private void appendControlPoint(ControlPoint newControlPoint) {
        if (null == first) {
            first = newControlPoint;
        }
        if (null != last) {
            last.m_next = newControlPoint;
            newControlPoint.m_last = last;
        }
        last = newControlPoint;

    }

    /**
     * Clear the path to an empty path with no control points.
     */
    public void clearPath() {
        title = DEFAULT_TITLE;
        description = DEFAULT_DESCRIPTION;
        first = null;
        last = null;
        scheduledActions.clear();
        speedMultiplier = 1.0;
    }

    /**
     * Insert a control point at a specific PathPoint. NOTE, for this to behave as expected the speed multiplier
     * must be 1.0 when the path point is generated.
     *
     * @param pathPoint The path point at which to insert the new control point.
     * @return Returns the added control point.
     */
    @NotNull
    public ControlPoint insertControlPoint(@NotNull PathPoint pathPoint) {
        // validity testing
        if (null == first) {
            throw new IllegalStateException("There is no path to insert control points into.");
        }

        // The path point has all the information we need for inserting the new point. The deal here is we insert
        // the control point with the path point parameters so that the curve is essentially unchanged by the insertion.

        // Create
        ControlPoint newControlPoint = new ControlPoint(this, pathPoint.time);
        newControlPoint.m_last = pathPoint.previousControlPoint;
        pathPoint.nextControlPoint.m_last = newControlPoint;
        newControlPoint.m_next = pathPoint.nextControlPoint;
        pathPoint.previousControlPoint.m_next = newControlPoint;

        // set the location, heading, and derivatives for this control point
        newControlPoint.setFieldLocation(pathPoint.fieldPt.getX(), pathPoint.fieldPt.getY());
        newControlPoint.setFieldHeading(pathPoint.fieldHeading);
        newControlPoint.setTangent(pathPoint.field_dX, pathPoint.field_dY);
        return newControlPoint;
    }

    /**
     * Insert a control point at a specified time along the path. There must be a path (i.e. at least a start
     * and an end control point) before a control point can be inserted.
     *
     * @param time The time the point should be inserted, which must be greater than 0.0 and
     *             less than the time the path ends.
     * @return Returns the added control point.
     */
    @NotNull
    public ControlPoint insertControlPoint(double time) {
        // validity testing
        if (null == first || first == last) {
            throw new IllegalStateException("There is no path to insert control points into.");
        }
        if (time <= 0.0) {
            throw new IllegalArgumentException("The time for an inserted control point must be greater than 0.0.");
        } else if (time >= last.m_time) {
            throw new IllegalArgumentException(
                    "The time for an inserted control point must be less than the time of the last point.");
        }

        // get the point on the path at the specified time
        PathFollower pathFollower = new PathFollower(1.0);
        PathPoint pathPoint = pathFollower.getPointAt(time);
        return insertControlPoint(pathPoint);
    }

    /**
     * Delete the specified control point. The first control point cannot be deleted. When a control point is
     * deleted there are no changes to adjacent control points except for the re-computation of derivatives
     * at adjacent control points if they have not been specifically set..
     *
     * @param controlPoint The control point to be deleted.
     */
    public void deleteControlPoint(@NotNull ControlPoint controlPoint) {
        // shift the time of any point past the one to be deleted.
        if (null == controlPoint.m_last) {
            throw new IllegalStateException("The initial point of a path cannot be deleted.");
        }

        // now delete the point (remove it from the list)
        controlPoint.m_last.m_next = controlPoint.m_next;
        if (null != controlPoint.m_next) {
            controlPoint.m_next.m_last = controlPoint.m_last;
        } else {
            // This is the last point being deleted
            last = controlPoint.m_last;
        }

        // and reset the derivatives for the surrounding points.
        if (controlPoint.m_last != null) {
            controlPoint.m_last.updateLocationDerivatives();
            controlPoint.m_last.updateHeadingDerivative();
        }
        if (controlPoint.m_next != null) {
            controlPoint.m_next.updateLocationDerivatives();
            controlPoint.m_next.updateHeadingDerivative();
        }
    }

    public RobotAction scheduleCommand(double pathTime, @NotNull String command) {
        return scheduledActions.scheduleAction(pathTime, command);
    }

    public boolean deleteScheduledCommand(@NotNull RobotAction robotAction) {
        return scheduledActions.deleteAction(robotAction);
    }

    /**
     * This factory method instantiates a control point iterator.
     *
     * @return Returns an iterator for the control points of this spline.
     */
    @NotNull
    public Iterable<ControlPoint> getControlPoints() {
        return new ControlPointIterator();
    }


    /**
     * This factory method instantiates an iterator for {@link PathPoint}s defining segments along the curve
     * at a default interval of {@code 0.05}sec, see {@link #DEFAULT_PATH_DELTA}. This is useful in path
     * planning when you want to draw the path on the field.
     *
     * @return An iterator for points along the path at a specified time interval
     */
    @NotNull
    public Iterable<PathPoint> getCurveSegments() {
        return new PathIterator(DEFAULT_PATH_DELTA, speedMultiplier);
    }

    /**
     * This factory method instantiates an iterator for {@link PathPoint}s defining segments along the curve
     * at a specified time interval. This is useful in path planning when you want to draw the path on
     * the field.
     *
     * @param timeInterval The time interval (seconds) between generated {@link PathPoint}s on the curve
     * @return An iterator for points along the path at a specified time interval
     */
    @NotNull
    public Iterable<PathPoint> getCurveSegments(double timeInterval) {
        return new PathIterator(timeInterval, speedMultiplier);
    }

    /**
     * This factory method instantiates a path follower that generates {@link PathPoint}s along the curve
     * at the requested monotonically increasing times. This is used in the robot code because the command cycle
     * processing time is not assured to be constant.
     *
     * @return A {@link PathFollower} that generates {@link PathPoint}s at specified times along the path.
     */
    @NotNull
    public PathFollower getPathFollower() {
        return new PathFollower(speedMultiplier);
    }

    /**
     * Load a path from a {@code .json} path file.
     *
     * @param filename (String, not null) The name of the file the path will be read from.
     * @return {@code true} if the path was successfully loaded, {@code false} otherwise. If the path was not
     * successfully loaded, the path will be empty.
     */
    public boolean loadPath(String filename) {
        clearPath();
        try {
            // Load the path from the file.
            JSONObject path = readJsonFileAsJSONObject(filename);
            title = parseString(path, TITLE, DEFAULT_TITLE);
            description = parseString(path, DESCRIPTION, DEFAULT_DESCRIPTION);
            speedMultiplier = parseDouble(path, SPEED_MULTIPLIER, speedMultiplier);
            JSONArray controlPoints = getJSONArray(path, CONTROL_POINTS);
            for (Object cpObj : controlPoints) {
                JSONObject cpJson = (JSONObject) cpObj;
                ControlPoint newControlPoint = new ControlPoint(this, cpJson);
                appendControlPoint(newControlPoint);
            }
            JSONArray scheduledActions = getJSONArray(path, ROBOT_SCHEDULED_ACTIONS, false);
            if (null != scheduledActions) {
                this.scheduledActions.fromJson(scheduledActions);
            }

            // now that the points are reloaded, recompute the derivatives for any points that
            // have not been manually edited
            for (ControlPoint point : getControlPoints()) {
                point.updateLocationDerivatives();
                point.updateHeadingDerivative();
            }
            // and, the derivatives for the first point is dependent on the derivatives for
            // the latter points in the spline, so recompute that now that we have the latter points.
            if (null != first) {
                first.updateLocationDerivatives();
            }
            return true;

        } catch (IOException | ParseException | ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Save the path to a {@code .json} path file.
     *
     * @param filename (String, not null) The filename the path will be written to.
     * @return {@code true} if the path was successfully saved, {@code false} otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean savePath(@NotNull String filename) {
        JSONObject path = new JSONObject();
        path.put(TITLE, title);
        path.put(DESCRIPTION, description);
        path.put(SPEED_MULTIPLIER, speedMultiplier);
        JSONArray controlPoints = new JSONArray();
        path.put(CONTROL_POINTS, controlPoints);
        for (ControlPoint pt : getControlPoints()) {
            controlPoints.add(pt.toJSON());
        }
        JSONArray scheduledActions = new JSONArray();
        path.put(ROBOT_SCHEDULED_ACTIONS, scheduledActions);
        this.scheduledActions.toJson(scheduledActions);
        //Write JSON file
        try (FileWriter file = new FileWriter(filename)) {
            file.write(path.toJSONString());
            file.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
