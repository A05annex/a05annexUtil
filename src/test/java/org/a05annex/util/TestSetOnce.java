package org.a05annex.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


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

	// *****************************************************************************************************************
	// Test instance field set - set eat of the fields in the instance
	@Test
	void testSetOnceSuccessful() {
		TestInstance instance = new TestInstance();
		Boolean boolValue = Boolean.valueOf(false);
		Integer intValue = Integer.valueOf(42);
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

	final static class TestStatic {
		private static Boolean boolField = null;
		private static Integer intField = null;
		private static String strField1 = null;
		private static String strField2 = null;

		public static Boolean getBoolField() {
			return boolField;
		}

		public static Integer getIntField() {
			return intField;
		}

		public static String getStrField1() {
			return strField1;
		}

		public static String getStrField2() {
			return strField2;
		}
	}

	@BeforeEach
	void resetStaticFields() {
		TestStatic.boolField = null;
		TestStatic.intField = null;
		TestStatic.strField1 = null;
		TestStatic.strField2 = null;
	}

	// *****************************************************************************************************************
// Test static field set - set each of the static fields in the class
	@Test
	void testSetOnceStaticSuccessful() {
		Boolean boolValue = Boolean.valueOf(false);
		Integer intValue = Integer.valueOf(42);
		String strValue1 = " test value 1";
		String strValue2 = " test value 2";
		Class<TestStatic> test = TestStatic.class;

		// test all of the value set commands for static fields
		Object result = Utl.setOnce(TestStatic.class, "boolField", boolValue);
		assertEquals(boolValue, result, "The returned value should match the provided boolValue.");
		result = Utl.setOnce(TestStatic.class, "intField", intValue);
		assertEquals(intValue, result, "The returned value should match the provided intValue.");
		result = Utl.setOnce(TestStatic.class, "strField1", strValue1);
		assertEquals(strValue1, result, "The returned value should match the provided strValue1.");
		result = Utl.setOnce(TestStatic.class, "strField2", strValue2);
		assertEquals(strValue2, result, "The returned value should match the provided strValue2.");

		// test that all of the static fields are correctly set
		assertEquals(boolValue, TestStatic.getBoolField(), "The field should equal the provided boolValue.");
		assertEquals(intValue, TestStatic.getIntField(), "The field should equal the provided intValue.");
		assertEquals(strValue1, TestStatic.getStrField1(), "The field should equal the provided strValue1.");
		assertEquals(strValue2, TestStatic.getStrField2(), "The field should equal the provided strValue2.");
	}

	// Test if exception is thrown when the static field name does not exist
	@Test
	void testSetOnceStaticInvalidFieldName() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> Utl.setOnce(TestStatic.class, "junk", "junk"),
				"Setting a non-existent static field should throw an IllegalArgumentException."
		);
	}

	// Test if exception is thrown when the static field is already set at class initialization.
	@Test
	void testSetOnceStaticThrowsIfFieldSetAtInitialization() {
		String instantiationValue = "existingStrValue1";

		// Set static fields at initialization
		Utl.setOnce(TestStatic.class, "strField1", instantiationValue);
		String value = "newValue";

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(TestStatic.class, "strField1", value),
				"Setting a static field more than once should throw an IllegalStateException."
		);
		assertTrue(exception.getMessage().contains("is already set"),
				"The exception message should indicate that the field 'is already set'.");
		// Verify the value of the static field was not changed
		assertEquals(instantiationValue, TestStatic.getStrField1(), "The field should have the instantiation value.");
	}

	// Test if exception is thrown when the static field is set twice using setOnceStatic()
	@Test
	void testSetOnceStaticThrowsIfFieldSetTwice() {
		String firstValue = "firstNewValue";
		String secondValue = "secondNewValue";

		// Set the value, the first set should work
		Object result = Utl.setOnce(TestStatic.class, "strField1", firstValue);
		assertEquals(firstValue, result, "The returned value should match the provided strValue1.");
		// Try to set the value again, this should fail
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(TestStatic.class, "strField1", secondValue),
				"Setting a static field more than once should throw an IllegalStateException."
		);
		assertTrue(exception.getMessage().contains("is already set"),
				"The exception message should indicate that the field 'is already set'.");
		// Verify the value of the static field was not changed
		assertEquals(firstValue, TestStatic.getStrField1(), "The field should have the instantiation value.");
	}

	// Test if exception is thrown when the specified static field has an incompatible type
	@Test
	void testSetOnceStaticThrowsIfIncompatibleType() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> Utl.setOnce(TestStatic.class, "strField1", 123),
				"Setting a value that doesn't match any static field type should throw an IllegalArgumentException."
		);

		assertTrue(exception.getMessage().contains("cannot be set to a"),
				"The exception message should indicate that 'cannot be set to a'.");
	}

}
