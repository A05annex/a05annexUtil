package org.a05annex.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.awt.geom.Point2D;
import java.io.IOException;

import static org.a05annex.util.JsonSupport.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a set of tests that confirm the error conditions when the JSON data files are not formatted as
 * expected, or when required fields have not been specified.
 */
@RunWith(JUnitPlatform.class)
public class TestJson {

    // -----------------------------------------------------------------------------------------------------------------
    // Basic tests for valid file paths and associated
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Invalid file path, should throw {@link IOException}
     */
    @Test
    @DisplayName("file does not exist")
     void test_file_does_not_exist() {
        assertThrows(IOException.class,
                () -> readJsonFileAsJSONObject("./src/test/resources/junk"));
    }

    /**
     * Invalid JSON syntax in the file, should throw {@link ParseException}.
     */
    @Test
    @DisplayName("not valid JSON")
    void test_bad_JSON_format() {
        assertThrows(ParseException.class,
                () -> readJsonFileAsJSONObject("./src/test/resources/TestBadFormat.json"));
    }

    /**
     * Make sure a dictionary JSON file reads as a dictionary.
     */
    @Test
    @DisplayName("read JSON dictionary")
    void test_read_JSON_object() {
        readTestDictionary();
    }

    /**
     * Make sure a dictionary JSON file reads as a dictionary.
     */
    @Test
    @DisplayName("read JSON list as dictionary")
    void test_read_JSON_list_as_object() {
        assertThrows(ClassCastException.class,
                () -> readJsonFileAsJSONObject("./src/test/resources/testList.json"));
    }

    /**
     * Make sure a list JSON file reads as a list.
     */
    @Test
    @DisplayName("read JSON list")
    void test_read_JSON_array() {
        try {
            JSONArray jsonArray = readJsonFileAsJSONArray("./src/test/resources/testList.json");
        } catch (IOException | ParseException e) {
            fail(e);
        }
    }

    /**
     * Make sure an array JSON file does not read as a list.
     */
    @Test
    @DisplayName("read JSON Dict as list")
    void test_read_JSON_dict_as_array() {
        assertThrows(ClassCastException.class,
                () -> readJsonFileAsJSONArray("./src/test/resources/testDict.json"));
    }

    /**
     * Test the {@link JsonSupport#parseString(JSONObject, String, String)} method correctly parses a key
     * that exists, correctly defaults if the key does not exist, and throws a {@link ClassCastException}
     * if the value at the key is not a string.
     */
    @Test
    @DisplayName("read JSON string")
    void test_read_strings() {
        JSONObject jsonObj = readTestDictionary();
        assertNotNull(jsonObj);
        if (null != jsonObj) {
            // test a string that is specified.
            assertEquals(parseString(jsonObj, "valueString", "junk"), "A string value");
            // Test a string that is not specified to make sure it defaults correctly.
            assertEquals(parseString(jsonObj, "missingString", "junk"), "junk");
            // If the value at this key is not a string there will be a ClassCastException thrown.
            assertThrows(ClassCastException.class, () -> parseString(jsonObj, "valueTrue", "junk"));
            assertThrows(ClassCastException.class, () -> parseString(jsonObj, "valueDouble", "junk"));
            assertThrows(ClassCastException.class, () -> parseString(jsonObj, "valuePoint", "junk"));
            assertThrows(ClassCastException.class, () -> parseString(jsonObj, "valueDict", "junk"));
            assertThrows(ClassCastException.class, () -> parseString(jsonObj, "valueList", "junk"));
        }
    }

    /**
     * Test the {@link JsonSupport#parseDouble(JSONObject, String, double)} method correctly parses a key
     * that exists, correctly defaults if the key does not exist, and throws a {@link ClassCastException}
     * if the value at the key is not a double.
     */
    @Test
    @DisplayName("read JSON double")
    void test_read_double() {
        JSONObject jsonObj = readTestDictionary();
        assertNotNull(jsonObj);
        if (null != jsonObj) {
            // test a double that is specified
            assertEquals(parseDouble(jsonObj, "valueDouble", 0.00), 4.5555);
            // Test a double that is not specified to make sure it defaults correctly
            assertEquals(parseDouble(jsonObj, "missingDouble", 8.889), 8.889);
            // If the value at this key is not a number there will be a ClassCastException thrown.
            assertThrows(ClassCastException.class, () -> parseDouble(jsonObj, "valueTrue", 8.889));
            assertThrows(ClassCastException.class, () -> parseDouble(jsonObj, "valueString", 8.889));
            assertThrows(ClassCastException.class, () -> parseDouble(jsonObj, "valuePoint", 8.889));
            assertThrows(ClassCastException.class, () -> parseDouble(jsonObj, "valueDict", 8.889));
            assertThrows(ClassCastException.class, () -> parseDouble(jsonObj, "valueList", 8.889));
        }
    }

    /**
     * Test the {@link JsonSupport#parseBoolean(JSONObject, String, boolean)} method correctly parses a key
     * that exists, correctly defaults if the key does not exist, and throws a {@link ClassCastException}
     * if the value at the key is not a boolean.
     */
    @Test
    @DisplayName("read JSON boolean")
    void test_read_boolean() {
        JSONObject jsonObj = readTestDictionary();
        assertNotNull(jsonObj);
        if (null != jsonObj) {
            // test booleans that is specified
            assertTrue(parseBoolean(jsonObj, "valueTrue", false));
            assertFalse(parseBoolean(jsonObj, "valueFalse", true));
            // Test a boolean that is not specified to make sure it defaults correctly
            assertFalse(parseBoolean(jsonObj, "missingBoolean", false));
            // If the value at this key is not a number there will be a ClassCastException thrown.
            assertThrows(ClassCastException.class, () -> parseBoolean(jsonObj, "valueString", false));
            assertThrows(ClassCastException.class, () -> parseBoolean(jsonObj, "valueDouble", false));
            assertThrows(ClassCastException.class, () -> parseBoolean(jsonObj, "valuePoint", false));
            assertThrows(ClassCastException.class, () -> parseBoolean(jsonObj, "valueDict", false));
            assertThrows(ClassCastException.class, () -> parseBoolean(jsonObj, "valueList", false));
        }
    }

    /**
     * Test the {@link JsonSupport#parsePoint(JSONObject, String)} method correctly parses a key
     * that exists, correctly defaults if the key does not exist, and throws a {@link ClassCastException}
     * if the value at the key is not a {@link Point2D}.
     */
    @Test
    @DisplayName("read JSON point2d")
    void test_read_point2d() {
        JSONObject jsonObj = readTestDictionary();
        assertNotNull(jsonObj);
        if (null != jsonObj) {
            // test point2d that is specified
            Point2D pt2d = parsePoint(jsonObj, "valuePoint");
            assertEquals(pt2d.getX(), 1.111);
            assertEquals(pt2d.getY(), 2.222);
            // test point2d for a missing key (should be null)
            assertNull(parsePoint(jsonObj, "missingPoint"));
            // test reading a point list
            JSONArray jsonArray = getJSONArray(jsonObj,"valuePointList" );
            Point2D pt0 = parsePoint((JSONArray) jsonArray.get(0));
            assertEquals(pt0.getX(), 3.333);
            assertEquals(pt0.getY(), 4.444);
            Point2D pt1 = parsePoint((JSONArray) jsonArray.get(1));
            assertEquals(pt1.getX(), 5.555);
            assertEquals(pt1.getY(), 6.666);
        }
    }

    /**
     * Test the {@link JsonSupport#getJSONObject(JSONObject, String)} method correctly parses a key
     * that exists, correctly defaults if the key does not exist, and throws a {@link ClassCastException}
     * if the value at the key is not a {@link JSONObject}.
     */
    @Test
    @DisplayName("parse JSON dict")
    void test_parse_dict() {
        JSONObject jsonObj = readTestDictionary();
        assertNotNull(jsonObj);
        if (null != jsonObj) {
            // test getting a getting that is required
            assertNotNull(getJSONObject(jsonObj, "valueDict"));
            assertThrows(NullPointerException.class, () -> getJSONObject(jsonObj, "missingDict"));
            // test getting a dict that is optional
            assertNull(getJSONObject(jsonObj, "missingDict", false));
            // rest getting a dict that is not a dict
            assertThrows(ClassCastException.class, () -> getJSONObject(jsonObj, "valueString"));
            assertThrows(ClassCastException.class, () -> getJSONObject(jsonObj, "valueDouble"));
            assertThrows(ClassCastException.class, () -> getJSONObject(jsonObj, "valueTrue"));
            assertThrows(ClassCastException.class, () -> getJSONObject(jsonObj, "valuePoint"));
            assertThrows(ClassCastException.class, () -> getJSONObject(jsonObj, "valueList"));
        }
    }

    /**
     * Test the {@link JsonSupport#getJSONArray(JSONObject, String)} method correctly parses a key
     * that exists, correctly defaults if the key does not exist, and throws a {@link ClassCastException}
     * if the value at the key is not a {@link JSONArray}.
     */
    @Test
    @DisplayName("parse JSON list")
    void test_parse_list() {
        JSONObject jsonObj = readTestDictionary();
        assertNotNull(jsonObj);
        if (null != jsonObj) {
            // test getting a getting that is required
            assertNotNull(getJSONArray(jsonObj, "valueList"));
            assertThrows(NullPointerException.class, () -> getJSONArray(jsonObj, "missingList"));
            // test getting a dict that is optional
            assertNull(getJSONArray(jsonObj, "missingList", false));
            // rest getting a dict that is not a dict
            assertThrows(ClassCastException.class, () -> getJSONArray(jsonObj, "valueString"));
            assertThrows(ClassCastException.class, () -> getJSONArray(jsonObj, "valueDouble"));
            assertThrows(ClassCastException.class, () -> getJSONArray(jsonObj, "valueTrue"));
            assertThrows(ClassCastException.class, () -> getJSONArray(jsonObj, "valueDict"));
        }
    }

    private JSONObject readTestDictionary() {
        try {
            return readJsonFileAsJSONObject("./src/test/resources/testDict.json");
        } catch (IOException | ParseException e) {
            fail(e);
        }
        return null;
    }
}
