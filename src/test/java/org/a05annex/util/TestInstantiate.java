package org.a05annex.util;

import org.a05annex.util.instantiate.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(JUnitPlatform.class)
public class TestInstantiate {

    final String TEST_OBJ_CLASS_NAME = TestObj.class.getCanonicalName();
    final String TEST_OBJ_BASE_CLASS_NAME = TestObjBase.class.getCanonicalName();
    final String TEST_OBJ_EXTENDS_CLASS_NAME = TestObjExtendsBase.class.getCanonicalName();
    final String TEST_OBJ_INTERFACE_CLASS_NAME = ITestInterface.class.getCanonicalName();

    // *****************************************************************************************************************
    // The basic tests for a no argument constructor to let us get the details right for:
    // * class names;
    // * casting the instantiated object to a base class or interface;
    // *****************************************************************************************************************
    /**
     * This is a test for basic instantiation with a no-argument constructor. This is an unlikely real
     * scenario because this type of reflection is generally for classes that are extending functionality
     * by extending a base class or implementing an interface and whose class name is unknown to the
     * who will be using the class, and is being dynamically loaded based on a data file referring to the
     * class.
     */
    @Test
    void testBasicInstantiation() {
        TestObj obj = Utl.instantiateObjectFromName(TestObj.class, TEST_OBJ_CLASS_NAME);
        assertNotNull(obj);
        assertTrue(obj instanceof TestObj);
    }

    /**
     * Testing a typical usage where the object being instantiated by reflection extends a base object
     * implementation.
     */
    @Test
    void testBasicInstantiationExtends() {
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, TEST_OBJ_EXTENDS_CLASS_NAME);
        assertNotNull(obj);
        assertTrue(obj instanceof TestObjBase);
        assertTrue(obj instanceof TestObjExtendsBase);
        assertNull(obj.getBoolField());
        assertNull(obj.getIntField());
        assertNull(obj.getStrField());
    }
    @Test
    /**
     * Testing a typical usage where the object being instantiated by reflection implements an interface
     */
    void testBasicInstantiationImplements() {
        ITestInterface obj = Utl.instantiateObjectFromName(ITestInterface.class, TEST_OBJ_EXTENDS_CLASS_NAME);
        assertNotNull(obj);
        assertTrue(obj instanceof ITestInterface);
        assertTrue(obj instanceof TestObjExtendsBase);
        assertTrue(obj.getTestInterfaceValue());
    }

    /**
     * A test where the instantiated class instance cannot be cast to the expected class.
     */
    @Test
    void testBasicInstantiationClassMismatch() {
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, TEST_OBJ_CLASS_NAME);
        assertNull(obj);
    }

    /**
     * A test where the requested class does not exist - an invalid class name is specified.
     */
    @Test
    void testBasicInstantiationBadClassName() {
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, "StupidJunkClassName");
        assertNull(obj);
    }

    @Test
    void testInstantiationExtendsWithArgs() {
        Boolean boolValue = true;
        Integer intValue = 12;
        String strValue = "This is a test String";
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, TEST_OBJ_EXTENDS_CLASS_NAME,
                new Class<?>[] {Boolean.class, Integer.class, String.class},
                new Object[] {boolValue, intValue, strValue});
        assertNotNull(obj);
        assertTrue(obj instanceof TestObjBase);
        assertTrue(obj instanceof TestObjExtendsBase);
        assertEquals(boolValue, obj.getBoolField());
        assertEquals(intValue, obj.getIntField());
        assertEquals(strValue, obj.getStrField());
    }

    /**
     * Test with mismatched {@code parameterTypes} and {@code instArgs} array lengths.
     */
    @Test
    void testInstantiationExtendsWithBadArgs1() {
        Boolean boolValue = true;
        Integer intValue = 12;
        String strValue = "This is a test String";
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, TEST_OBJ_EXTENDS_CLASS_NAME,
                new Class<?>[] {Boolean.class, Integer.class, String.class},
                new Object[] {boolValue, intValue});
        assertNull(obj);
    }

    /**
     * Test with an argument type order that does not match the constructor.
     */
    @Test
    void testInstantiationExtendsWithBadArgs2() {
        Boolean boolValue = true;
        Integer intValue = 12;
        String strValue = "This is a test String";
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, TEST_OBJ_EXTENDS_CLASS_NAME,
                new Class<?>[] {Boolean.class, String.class, Integer.class},
                new Object[] {boolValue, strValue, intValue});
        assertNull(obj);
    }
    /**
     * Test with an the {@code instArgs} types do not match the {@code parameterTypes}.
     */
    @Test
    void testInstantiationExtendsWithBadArgs3() {
        Boolean boolValue = true;
        Integer intValue = 12;
        String strValue = "This is a test String";
        TestObjBase obj = Utl.instantiateObjectFromName(TestObjBase.class, TEST_OBJ_EXTENDS_CLASS_NAME,
                new Class<?>[] {Boolean.class, Integer.class, String.class},
                new Object[] {boolValue, strValue, intValue});
        assertNull(obj);
    }
}
