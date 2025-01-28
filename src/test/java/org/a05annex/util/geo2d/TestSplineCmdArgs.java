package org.a05annex.util.geo2d;

import org.a05annex.util.geo2d.KochanekBartelsSpline.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
public class TestSplineCmdArgs {
    // -----------------------------------------------------------------------------------------------------------------
    // This first set of tests is for the instantiation argument class
    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("Valid command String arg creation")
    void testValidStringArgumentCreation() {
        RobotActionArg arg = new RobotActionArg("String", "junk");
        assertEquals("String", arg.getArgType());
        assertEquals(String.class, arg.getArgClass());
        assertEquals("junk", arg.getValueString());
        assertEquals("junk", arg.getValueObject());
    }

    @Test
    @DisplayName("Valid command String arg creation - no value")
    void testValidStringArgumentCreationNoValue() {
        RobotActionArg arg = new RobotActionArg("String");
        assertEquals("String", arg.getArgType());
        assertEquals(String.class, arg.getArgClass());
        assertEquals(null, arg.getValueString());
        assertEquals(null, arg.getValueObject());
    }

    @Test
    @DisplayName("Valid command Boolean arg creation")
    void testValidBooleanArgumentCreation() {
        RobotActionArg arg = new RobotActionArg("Boolean", "true");
        assertEquals("Boolean", arg.getArgType());
        assertEquals(Boolean.class, arg.getArgClass());
        assertEquals("true", arg.getValueString());
        assertEquals(true, arg.getValueObject());
    }

    @Test
    @DisplayName("Valid command Integer arg creation")
    void testValidIntegerArgumentCreation() {
        RobotActionArg arg = new RobotActionArg("Integer", "42");
        assertEquals("Integer", arg.getArgType());
        assertEquals(Integer.class, arg.getArgClass());
        assertEquals("42", arg.getValueString());
        assertEquals(42, arg.getValueObject());
    }

    @Test
    @DisplayName("Valid command Double arg creation")
    void testValidDoubleArgumentCreation() {
        RobotActionArg arg = new RobotActionArg("Double", "3.14156");
        assertEquals("Double", arg.getArgType());
        assertEquals(Double.class, arg.getArgClass());
        assertEquals("3.14156", arg.getValueString());
        assertEquals(3.14156, arg.getValueObject());
    }

    @Test
    @DisplayName("Valid command Double arg creation - no value")
    void testValidDoubleArgumentCreationNoValue() {
        RobotActionArg arg = new RobotActionArg("Double");
        assertEquals("Double", arg.getArgType());
        assertEquals(Double.class, arg.getArgClass());
        assertEquals(null, arg.getValueString());
        assertEquals(null, arg.getValueObject());
    }

    @Test
    @DisplayName("Invalid command Double arg creation - bad value")
    void testInvalidDoubleArgumentCreation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new RobotActionArg("Double", "3PCO"));
        assertTrue(exception.getMessage().contains("cannot be parsed to a valid"),
                "The exception message should indicate the value cannot be parsed to the type");
    }

    @Test
    @DisplayName("Valid reset double argument value")
    void testValidResetDoubleValue() {
        RobotActionArg arg = new RobotActionArg("Double", "3.14156");
        assertEquals("Double", arg.getArgType());
        assertEquals(Double.class, arg.getArgClass());
        assertEquals("3.14156", arg.getValueString());
        assertEquals(3.14156, arg.getValueObject());
        arg.setValue("42");
        assertEquals("42", arg.getValueString());
        assertEquals(42.0, arg.getValueObject());
    }

    @Test
    @DisplayName("Invalid reset double argument value - bad value")
    void testInvalidResetDoubleValue() {
        RobotActionArg arg = new RobotActionArg("Double", "3.14156");
        assertEquals("Double", arg.getArgType());
        assertEquals(Double.class, arg.getArgClass());
        assertEquals("3.14156", arg.getValueString());
        assertEquals(3.14156, arg.getValueObject());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> arg.setValue("3PCO"));
        assertTrue(exception.getMessage().contains("cannot be parsed to a valid"),
                "The exception message should indicate the value cannot be parsed to the type");
        // if the rest value is invalid, the value should not be modified
        assertEquals("3.14156", arg.getValueString());
        assertEquals(3.14156, arg.getValueObject());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // This second set of tests is for the instantiation argument list for and action command
    // -----------------------------------------------------------------------------------------------------------------
    static final String STRING_ARG_VALUE = "my string arg";
    static final RobotActionArg STRING_ARG = new RobotActionArg("String", STRING_ARG_VALUE);
    static final String BOOLEAN_ARG_STRING_VALUE = "false";
    static final Boolean BOOLEAN_ARG_VALUE = false;
    static final RobotActionArg BOOLEAN_ARG = new RobotActionArg("Boolean", BOOLEAN_ARG_STRING_VALUE);
    static final String INTEGER_ARG_STRING_VALUE = "42";
    static final Integer INTEGER_ARG_VALUE = 42;
    static final RobotActionArg INTEGER_ARG = new RobotActionArg("Integer", INTEGER_ARG_STRING_VALUE);

    /**
     * @return
     */
    private RobotAction getRefAction() {
        RobotAction action = new RobotAction(4.5, "MyCommand");
        action.appendArgument(STRING_ARG);
        action.appendArgument(BOOLEAN_ARG);
        action.appendArgument(INTEGER_ARG);
        return action;

    }

    @Test
    @DisplayName("RobotAction append arg")
    void testRobotActionAppendArg() {
        RobotAction action = getRefAction();
        Class<?>[] actionArgTypes = action.getArgTypeArray();
        Object[] actionArgValues = action.getArgValueArray();
        assertEquals(3, actionArgTypes.length);
        assertEquals(3, actionArgValues.length);
        assertEquals(RobotActionArg.ARG_TYPES.get("String"), actionArgTypes[0]);
        assertEquals(STRING_ARG_VALUE, actionArgValues[0]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Boolean"), actionArgTypes[1]);
        assertEquals(BOOLEAN_ARG_VALUE, actionArgValues[1]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Integer"), actionArgTypes[2]);
        assertEquals(INTEGER_ARG_VALUE, actionArgValues[2]);
    }

    @Test
    @DisplayName("RobotAction delete arg")
    void testRobotActionDeleteArg() {
        RobotAction action = getRefAction();
        RobotActionArg deletedArg = action.deleteArgument(1);
        // test the args after deletion
        Class<?>[] actionArgTypes = action.getArgTypeArray();
        Object[] actionArgValues = action.getArgValueArray();
        assertEquals(2, actionArgTypes.length);
        assertEquals(2, actionArgValues.length);
        assertEquals(RobotActionArg.ARG_TYPES.get("String"), actionArgTypes[0]);
        assertEquals(STRING_ARG_VALUE, actionArgValues[0]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Integer"), actionArgTypes[1]);
        assertEquals(INTEGER_ARG_VALUE, actionArgValues[1]);
        // test the deleted arg (we may want to insert it later)
        assertEquals(RobotActionArg.ARG_TYPES.get("Boolean"), deletedArg.getArgClass());
        assertEquals(BOOLEAN_ARG_VALUE, deletedArg.getValueObject());
    }

    @Test
    @DisplayName("RobotAction insert arg at start")
    void testRobotActionArgInsertAtStart() {
        RobotAction action = getRefAction();
        RobotActionArg deletedArg = action.deleteArgument(1);
        action.insertArgument(0, deletedArg);
        // test the args after deletion
        Class<?>[] actionArgTypes = action.getArgTypeArray();
        Object[] actionArgValues = action.getArgValueArray();
        assertEquals(3, actionArgTypes.length);
        assertEquals(3, actionArgValues.length);
        assertEquals(RobotActionArg.ARG_TYPES.get("Boolean"), actionArgTypes[0]);
        assertEquals(BOOLEAN_ARG_VALUE, actionArgValues[0]);
        assertEquals(RobotActionArg.ARG_TYPES.get("String"), actionArgTypes[1]);
        assertEquals(STRING_ARG_VALUE, actionArgValues[1]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Integer"), actionArgTypes[2]);
        assertEquals(INTEGER_ARG_VALUE, actionArgValues[2]);
    }

    @Test
    @DisplayName("RobotAction insert arg at end")
    void testRobotActionArgInsertAtEnd() {
        RobotAction action = getRefAction();
        RobotActionArg deletedArg = action.deleteArgument(1);
        action.insertArgument(2, deletedArg);
        // test the args after deletion
        Class<?>[] actionArgTypes = action.getArgTypeArray();
        Object[] actionArgValues = action.getArgValueArray();
        assertEquals(3, actionArgTypes.length);
        assertEquals(3, actionArgValues.length);
        assertEquals(RobotActionArg.ARG_TYPES.get("String"), actionArgTypes[0]);
        assertEquals(STRING_ARG_VALUE, actionArgValues[0]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Integer"), actionArgTypes[1]);
        assertEquals(INTEGER_ARG_VALUE, actionArgValues[1]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Boolean"), actionArgTypes[2]);
        assertEquals(BOOLEAN_ARG_VALUE, actionArgValues[2]);
    }

    @Test
    @DisplayName("RobotAction insert arg after end - IndexOutOfBoundsException")
    void testRobotActionArgInsertAfterEnd() {
        RobotAction action = getRefAction();
        RobotActionArg deletedArg = action.deleteArgument(1);
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> action.insertArgument(3, deletedArg));
    }
    @Test
    @DisplayName("RobotAction move argument up")
    void testRobotActionArgMoveUp() {
        RobotAction action = getRefAction();
        action.moveArgumentUp(1);
        // test the args after the move up
        Class<?>[] actionArgTypes = action.getArgTypeArray();
        Object[] actionArgValues = action.getArgValueArray();
        assertEquals(3, actionArgTypes.length);
        assertEquals(3, actionArgValues.length);
        assertEquals(RobotActionArg.ARG_TYPES.get("Boolean"), actionArgTypes[0]);
        assertEquals(BOOLEAN_ARG_VALUE, actionArgValues[0]);
        assertEquals(RobotActionArg.ARG_TYPES.get("String"), actionArgTypes[1]);
        assertEquals(STRING_ARG_VALUE, actionArgValues[1]);
        assertEquals(RobotActionArg.ARG_TYPES.get("Integer"), actionArgTypes[2]);
        assertEquals(INTEGER_ARG_VALUE, actionArgValues[2]);
    }
    @Test
    @DisplayName("RobotAction move argument up - invalid low index")
    void testRobotActionArgMoveUpInvalidLowIndex() {
        RobotAction action = getRefAction();
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> action.moveArgumentUp(0));
    }
    @Test
    @DisplayName("RobotAction move argument up - invalid high index")
    void testRobotActionArgMoveUpInvalidHighIndex() {
        RobotAction action = getRefAction();
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> action.moveArgumentUp(3));
    }
}
