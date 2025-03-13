package org.a05annex.util.geo2d;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is a set of tests for the {@link org.a05annex.util.geo2d.KochanekBartelsSpline.RobotAction} class. It
 * tests the methods of that class to assure correct functionality. This class uses
 * {@link org.a05annex.util.geo2d.ç}s, and the testing specific to those
 * can be found in {@link TestSplineRobotActionArgs}.
 */
@RunWith(JUnitPlatform.class)
public class TestSplineRobotAction {

    /**
     * It is always a valid operation to reset the command name, regardless of the
     * action type. This is a basic test with a valid class name.
     */
    @Test
    @DisplayName("RobotAction - test valid setCommand()")
    void test_setCommandName() {
        KochanekBartelsSpline.RobotAction robotAction = new KochanekBartelsSpline.RobotAction(
                1.3,"SomeCommand");
        assertEquals("SomeCommand", robotAction.getCommand());
    }
    @Test
    @DisplayName("Verify start-end-derivative control points")
    void test_setCommandDuration() {

    }
    @Test
    @DisplayName("Verify start-end-derivative control points")
    void test_setCommandDurationInvalid() {

    }


}
