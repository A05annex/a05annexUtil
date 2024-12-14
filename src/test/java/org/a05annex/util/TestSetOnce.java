package org.a05annex.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
class TestSetOnce {

	static class TestInstance {
		private String field;

		public String getField() {
			return field;
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

	// Helper method to create instance
	private TestInstance createInstance() {
		return new TestInstance();
	}

	// Test instance field set
	@Test
	void testSetOnceSuccessful() {
		TestInstance instance = createInstance();
		String value = "testValue";

		String result = Utl.setOnce(value, instance);

		assertEquals(value, result, "The returned value should match the provided value.");
		assertNotNull(instance.getField(), "The field should be set on the instance.");
		assertEquals(value, instance.getField(), "The field should equal the provided value.");
	}

	// Test if exception is thrown when the field is already set
	@Test
	void testSetOnceThrowsIfFieldAlreadySet() {
		TestInstance instance = createInstance();
		instance.field = "existingValue"; // Pre-set the field
		String value = "newValue";

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(value, instance),
				"Setting a field more than once should throw an IllegalStateException."
		);

		assertTrue(exception.getMessage().contains("more than once"),
				"The exception message should indicate that the field was set more than once.");
	}

	// Test if exception is thrown when no suitable field exists
	@Test
	void testSetOnceThrowsIfNoFieldIsSuitable() {
		TestInstance instance = createInstance();

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(123, instance),
				"Setting a value that doesn't match any field type should throw an IllegalStateException."
		);

		assertTrue(exception.getMessage().contains("No suitable variable"),
				"The exception message should indicate that no suitable field was found.");
	}

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

	// Test if an attempt to set a field of incompatible type throws an exception
	@Test
	void testSetOnceThrowsIfIncompatibleType() {
		TestInstance instance = createInstance();

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Utl.setOnce(123, instance),
				"Setting a field with an incompatible type should throw an IllegalStateException."
		);

		assertTrue(exception.getMessage().contains("No suitable variable"),
				"The exception message should indicate that no suitable field was found.");
	}
}
