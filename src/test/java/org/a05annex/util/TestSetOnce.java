package org.a05annex.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
class TestSetOnce {

	static class TestInstance {
		private final Boolean boolField ;
		private final Integer intField;
		private final String strField1;
		private final String strField2;

		public TestInstance() {
			this.boolField = null;
			this.intField = null;
			this.strField1 = null;
			this.strField2 = null;
		}

		public TestInstance(Boolean boolField, Integer intField, String strField1, String strField2) {
			this.boolField = boolField;
			this.intField = intField;
			this.strField1 = strField1;
			this.strField2 = strField2;
		}
		public Boolean getBoolField() {
			return boolField;
		}
		public Integer getIntField() {
			return intField;
		}
		public String getStrField1() {
			return strField1;
		}
		public String getStrField2() {
			return strField2;
		}
	}

	static class TestStaticInstance {
		private static String staticField;

		public static String getStaticField() {
			return staticField;
		}

		public static void resetStaticField() {
			staticField = null; // Reset the static variable to ensure test isolation
		}
	}

	@AfterEach
	void resetStaticFields() {
		// Ensure the static field is reset between tests
		TestStaticInstance.resetStaticField();
	}

	// *****************************************************************************************************************
	// Test instance field set - set eat of the fields in the instance
	@Test
	void testSetOnceSuccessful() {
		TestInstance instance = new TestInstance();
		Boolean boolValue = new Boolean(false);
		Integer intValue = new Integer(42);
		String strValue1 = " test value 1";
		String strValue2 = " test value 2";

		// test all of the value set commands
		Object result = Utl.setOnce(instance, "boolField", boolValue);
		assertEquals(boolValue, result, "The returned value should match the provided boolValue.");
		result = Utl.setOnce(instance, "intField", intValue);
		assertEquals(intValue, result, "The returned value should match the provided intValue.");
		result = Utl.setOnce(instance, "strField1", strValue1);
		assertEquals(strValue1, result, "The returned value should match the provided strValue1.");
		result = Utl.setOnce(instance, "strField2", strValue2);
		assertEquals(strValue2, result, "The returned value should match the provided strValue2.");

		// test that all of the values are correctly set
		assertEquals(boolValue, instance.getBoolField(), "The field should equal the provided boolValue.");
		assertEquals(intValue, instance.getIntField(), "The field should equal the provided intValue.");
		assertEquals(strValue1, instance.getStrField1(), "The field should equal the provided strValue1.");
		assertEquals(strValue2, instance.getStrField2(), "The field should equal the provided strValue2.");
	}

	@Test
	void testInvalidFieldName() {
		TestInstance instance = new TestInstance();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> Utl.setOnce(instance, "junk", "junk"),
				"Setting a non-existent field should throw an IllegalArgumentException "
		);

	}

	// Test if exception is thrown when the field is already set at object instantiation.
	@Test
	void testSetOnceThrowsIfFieldSetAtInstantiation() {
		String instantiationValue = "existingStrValue1";
		TestInstance instance = new TestInstance(null,null,instantiationValue,null);
		String value = "newValue";

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(instance, "strField1", value),
				"Setting a field more than once should throw an IllegalStateException."
		);
		assertTrue(exception.getMessage().contains("is already set"),
				"The exception message should indicate that the field 'is already set'.");
		// verify the value of the field was not changed
		assertEquals(instantiationValue, instance.getStrField1(), "The field should have the instantiation value.");
	}

	// Test if exception is thrown when the field is set twice using setOnce()
	@Test
	void testSetOnceThrowsIfFieldSetTwice() {
		TestInstance instance = new TestInstance();
		String firstValue = "firstNewValue";
		String secondValue = "secondNewValue";

		// set the value, the first set shuld work
		Object result = Utl.setOnce(instance, "strField1", firstValue);
		assertEquals(firstValue, result, "The returned value should match the provided strValue1.");
		// Try to set the value again, this should fail
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(instance, "strField1", secondValue),
				"Setting a field more than once should throw an IllegalStateException."
		);
		assertTrue(exception.getMessage().contains("is already set"),
				"The exception message should indicate that the field 'is already set'.");
		// verify the value of the field was not changed
		assertEquals(firstValue, instance.getStrField1(), "The field should have the instantiation value.");
	}

	// Test if exception is thrown when the specified field has an incompatible type
	@Test
	void testSetOnceThrowsIfIncompatibleType() {
		TestInstance instance = new TestInstance();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> Utl.setOnce(instance, "strField1", 123),
				"Setting a value that doesn't match any field type should throw an IllegalArgumentException."
		);

		assertTrue(exception.getMessage().contains("cannot be set to a"),
				"The exception message should indicate that 'cannot be set to a'.");
	}

	// *****************************************************************************************************************
	// Test static field set
	@Test
	void testStaticSetOnceSuccessful() {
		String value = "staticTestValue";

		String result = Utl.setOnce(value, TestStaticInstance.class);

		assertEquals(value, result, "The returned value should match the provided value.");
		assertNotNull(TestStaticInstance.getStaticField(), "The static field should be set on the class.");
		assertEquals(value, TestStaticInstance.getStaticField(),
				"The static field should equal the provided value.");
	}

	// Test if exception is thrown for static field already set
	@Test
	void testStaticSetOnceThrowsIfFieldAlreadySet() {
		TestStaticInstance.staticField = "existingStaticValue"; // Pre-set the static field
		String value = "newStaticValue";

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(value, TestStaticInstance.class),
				"Setting a static field more than once should throw an IllegalStateException."
		);

		assertTrue(exception.getMessage().contains("more than once"),
				"The exception message should indicate that the field was set more than once.");
	}

	// Test if exception is thrown for no suitable static field
	@Test
	void testStaticSetOnceThrowsIfNoFieldIsSuitable() {
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(123, TestStaticInstance.class),
				"Setting a value that doesn't match any static field type should throw an IllegalStateException."
		);

		assertTrue(exception.getMessage().contains("No suitable static variable"),
				"The exception message should indicate that no suitable static field was found.");
	}

}
