package org.a05annex.util.geo2d;

import org.a05annex.util.geo2d.KochanekBartelsSpline.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a set of tests for the {@link org.a05annex.util.geo2d.KochanekBartelsSpline.RobotAction} class. It
 * tests the methods of that class to assure correct functionality. Tests relating specifically to instantiation
 * arguments for {@link org.a05annex.util.geo2d.KochanekBartelsSpline.RobotAction}s are in
 * {@link TestSplineRobotActionArgs}.
 */
@RunWith(JUnitPlatform.class)
public class TestSplineRobotAction {

   final static String INSTANTIATION_COMMAND = "SomeCommand";
   final static String SET_COMMAND = "SomeOtherCommand";

   final static double INSTANTIATION_DURATION = 2.0;
   final static double SET_DURATION = 3.0;
    /**
     * It is always a valid operation to reset the command name, regardless of the
     * action type. This is a basic test with a valid class name.
     */
    @Test
    @DisplayName("RobotAction - test valid setCommand()")
    void test_setCommandName() {
        // RobotActionType#SCHEDULE_COMMAND
        lcl_setCommandName(new RobotAction(1.3,INSTANTIATION_COMMAND), RobotActionType.SCHEDULE_COMMAND);
        lcl_setCommandName(new RobotAction(INSTANTIATION_COMMAND, INSTANTIATION_DURATION), RobotActionType.STOP_AND_RUN_COMMAND);
        lcl_setCommandName(new RobotAction(1.3,INSTANTIATION_COMMAND, INSTANTIATION_DURATION),
                RobotActionType.RELINQUISH_DRIVE_TO_COMMAND);
    }
    void lcl_setCommandName(RobotAction robotAction, RobotActionType expectedType) {
        assertEquals(expectedType, robotAction.actionType);
        assertEquals(INSTANTIATION_COMMAND, robotAction.getCommand());
        robotAction.setCommand(SET_COMMAND);
        assertEquals(SET_COMMAND, robotAction.getCommand());
    }

    /**
     * Command duration can only be set for
     * {@link org.a05annex.util.geo2d.KochanekBartelsSpline.RobotActionType#STOP_AND_RUN_COMMAND} and
     * {@link org.a05annex.util.geo2d.KochanekBartelsSpline.RobotActionType#RELINQUISH_DRIVE_TO_COMMAND}
     * robot action types.
     */
    @Test
    @DisplayName("RobotAction - test valid setDuration()")
    void test_setCommandDuration() {
        lcl_setDuration(new RobotAction(INSTANTIATION_COMMAND, INSTANTIATION_DURATION), RobotActionType.STOP_AND_RUN_COMMAND);
        lcl_setDuration(new RobotAction(1.3,INSTANTIATION_COMMAND, INSTANTIATION_DURATION),
                RobotActionType.RELINQUISH_DRIVE_TO_COMMAND);

    }
    void lcl_setDuration(RobotAction robotAction, RobotActionType expectedType) {
        assertEquals(expectedType, robotAction.actionType);
        assertEquals(INSTANTIATION_DURATION, robotAction.getApproxDuration());
        assertTrue(robotAction.canResetApproxDuration());
        robotAction.setApproxDuration(SET_DURATION);
        assertEquals(SET_DURATION, robotAction.getApproxDuration());
    }

    @Test
    @DisplayName("RobotAction - test valid setDuration() invalid: duration <= 0")
    void test_setCommandDurationInvalid() {
        lcl_setInvalidDuration(new RobotAction(INSTANTIATION_COMMAND, INSTANTIATION_DURATION), RobotActionType.STOP_AND_RUN_COMMAND);
        lcl_setInvalidDuration(new RobotAction(1.3,INSTANTIATION_COMMAND, INSTANTIATION_DURATION),
                RobotActionType.RELINQUISH_DRIVE_TO_COMMAND);

    }
    void lcl_setInvalidDuration(RobotAction robotAction, RobotActionType expectedType) {
        assertEquals(expectedType, robotAction.actionType);
        assertEquals(INSTANTIATION_DURATION, robotAction.getApproxDuration());
        assertTrue(robotAction.canResetApproxDuration());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->robotAction.setApproxDuration(0.0));
    }

    /**
     * It is invalid to set a duration for a
     * {@link org.a05annex.util.geo2d.KochanekBartelsSpline.RobotActionType#SCHEDULE_COMMAND} robot action.
     */
    @Test
    @DisplayName("RobotAction - test invalid setDuration() on a scheduled command")
    void test_setInvalidScheduleCommandDuration() {
        RobotAction robotAction = new RobotAction(1.3,INSTANTIATION_COMMAND);
        assertEquals(RobotActionType.SCHEDULE_COMMAND, robotAction.actionType);
        assertFalse(robotAction.canResetApproxDuration());
        assertEquals(-1.0, robotAction.getApproxDuration());
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> robotAction.setApproxDuration(SET_DURATION),
                "It is invalid to set the durtion of a 'Scheduled' RobotAction."
        );
    }


}
