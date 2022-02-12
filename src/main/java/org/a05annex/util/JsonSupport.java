package org.a05annex.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class provides utility methods for standard data types for robotics with well known default
 * and failure behaviour.
 */
public class JsonSupport {

    /**
     * Parse the double value for an optional key in a JSON dictionary. If the key does not exist, the
     * default will be returned.
     *
     * @param dict         The JSON representation of a dictionary.
     * @param key          The key for the value to be obtained.
     * @param defaultValue The default if the key has not been specified.
     * @return Returns the value for the key.
     * @throws ClassCastException The value of the key is not a {@code double}.
     */
    static public double parseDouble(@NotNull JSONObject dict, @NotNull String key, double defaultValue) {
        double value = defaultValue;
        Object valueObj = dict.get(key);
        if (null != valueObj) {
            value = (double) valueObj;
        }
        return value;
    }

    /**
     * Parse the boolean value for an optional key in a JSON dictionary. If the key does not exist, the
     * default will be returned.
     *
     * @param dict         The JSON representation of a dictionary.
     * @param key          The key for the value to be obtained.
     * @param defaultValue The default if the key has not been specified.
     * @return Returns the value for the key.
     * @throws ClassCastException The value of the key is not a {@code boolean}.
     */
    static public boolean parseBoolean(@NotNull JSONObject dict, @NotNull String key, boolean defaultValue) {
        boolean value = defaultValue;
        Object valueObj = dict.get(key);
        if (null != valueObj) {
            value = (boolean) valueObj;
        }
        return value;
    }

    /**
     * Parse the point value for an optional key in a JSON dictionary. If the key does not exist,
     * {@code null} will be returned.
     *
     * @param dict The JSON representation of a dictionary.
     * @param key  The key for the value to be obtained.
     * @return The value of the parse point if the @code key} is specified in the JSON, {@code null} otherwise.
     * @throws ClassCastException The value of the key is not a {@code Point2d}.
     */
    @Nullable
    static public Point2D parsePoint(@NotNull JSONObject dict, @NotNull String key) {
        Object valueObj = dict.get(key);
        if ((null != valueObj) && (valueObj.getClass() == JSONArray.class)) {
            return parsePoint((JSONArray) valueObj);
        }
        return null;
    }

    /**
     * Parse a {@link Point2D} from a JSON list.
     *
     * @param coordinateList The JSON list containing the [X,Y] coordinates of the point.
     * @return The parsed {@link Point2D}.
     * @throws ClassCastException The value in the list is not a {@code Point2d}.
     */
    @NotNull
    static public Point2D parsePoint(@NotNull JSONArray coordinateList) {
        return new Point2D.Double((double) coordinateList.get(0), (double) coordinateList.get(1));
    }

    /**
     * Parse the {@code String} value for an optional key in a JSON dictionary. If the key does not exist, the
     * default will be returned.
     *
     * @param dict         The JSON representation of a dictionary.
     * @param key          The key for the value to be obtained.
     * @param defaultValue The default value for the string.
     * @return The parsed String if the {@code key} is specified in the JSON, {@code defaultValue} otherwise.
     * @throws ClassCastException The value of the key is not a {@code String}.
     */
    @Nullable
    static public String parseString(@NotNull JSONObject dict, @NotNull String key, @Nullable String defaultValue) {
        String value = defaultValue;
        Object valueObj = dict.get(key);
        if (null != valueObj) {
            value = (String) valueObj;
        }
        return value;
    }

    /**
     * Parse the {@link JSONArray} value for a required key. If the key does not exist a {@link NullPointerException}
     * will be thrown.
     *
     * @param dict The JSON representation of a dictionary.
     * @param key  The key for the value to be obtained.
     * @return The parsed {@link JSONArray} value at the {@code key}.
     * @throws NullPointerException Thrown if the {@code key} does not exist.
     * @throws ClassCastException   The value of the key is not a list.
     */
    @NotNull
    static public JSONArray getJSONArray(@NotNull JSONObject dict, @NotNull String key) {
        return getJSONArray(dict, key, true);
    }

    /**
     * Parse the {@link JSONArray} value for a key. If the key does not exist, the {@code required} argument
     * of {@code True} dictates a {@link NullPointerException} will be thrown, otherwise {@code null} will
     * be returned.
     *
     * @param dict     The JSON representation of a dictionary.
     * @param key      The key for the value to be obtained.
     * @param required {@code true} if this key is required, {@code false} if this key is optional
     * @return The parsed {@link JSONArray} value at the {@code key}, {@code null} if this key
     * is not found and the key is not required.
     * @throws NullPointerException Thrown if the {@code key} is required and does not exist.
     * @throws ClassCastException   The value of the key is not a list.
     */
    @Nullable
    static public JSONArray getJSONArray(@NotNull JSONObject dict, @NotNull String key, boolean required) {
        Object obj = dict.get(key);
        if (required && (null == obj)) {
            throw new NullPointerException(String.format("No value for key '%s'", key));
        }
        return (JSONArray) obj;
    }

    /**
     * Parse the {@link JSONArray} value for a required key. If the key does not exist a {@link NullPointerException}
     * will be thrown.
     *
     * @param dict The JSON representation of a dictionary.
     * @param key  The key for the value to be obtained.
     * @return The parsed {@link JSONObject} value at the {@code key}.
     * @throws NullPointerException Thrown if the {@code key} does not exist.
     * @throws ClassCastException   The value of the key is not a dictionary.
     */
    @NotNull
    static public JSONObject getJSONObject(@NotNull JSONObject dict, @NotNull String key) {
        return getJSONObject(dict, key, true);
    }

    /**
     * Parse the {@link JSONArray} value for a key. If the key does not exist, the {@code required} argument
     * of {@code True} dictates a {@link NullPointerException} will be thrown, otherwise {@code null} will
     * be returned.
     *
     * @param dict     The JSON representation of a dictionary.
     * @param key      The key for the value to be obtained.
     * @param required {@code true} if this key is required, {@code false} if this key is optional
     * @return The parsed {@link JSONObject} value at the {@code key}, {@code null} if this key
     * is not found and the key is not required.
     * @throws NullPointerException Thrown if the {@code key} is required and does not exist.
     * @throws ClassCastException   The value of the key is not a dictionary.
     */
    @Nullable
    static public JSONObject getJSONObject(@NotNull JSONObject dict, @NotNull String key, boolean required) {
        Object obj = dict.get(key);
        if (required && (null == obj)) {
            throw new NullPointerException(String.format("No value for key '%s'", key));
        }
        return (JSONObject) obj;
    }

    /**
     * Read a file into a {@link JSONObject}.
     *
     * @param filename The filename.
     * @return The {@link JSONObject} read from the file.
     * @throws FileNotFoundException if the named file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for
     * @throws IOException           Error reading from the file.
     * @throws ParseException        JSON format error in the file contents.
     * @throws ClassCastException    The contents of the JSON file is not a dictionary.
     */
    @NotNull
    static public JSONObject readJsonFileAsJSONObject(@NotNull String filename) throws
            IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(filename);
        //Read JSON file
        Object obj = jsonParser.parse(reader);
        return (JSONObject) obj;
    }

    /**
     * Read a file into a {@link JSONArray}.
     *
     * @param filename The filename.
     * @return The {@link JSONArray} read from the file.
     * @throws FileNotFoundException if the named file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for
     * @throws IOException           Error reading from the file.
     * @throws ParseException        JSON format error in the file contents.
     * @throws ClassCastException    The contents of the JSON file is not an array.
     */
    @NotNull
    static public JSONArray readJsonFileAsJSONArray(@NotNull String filename) throws
            IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(filename);
        //Read JSON file
        Object obj = jsonParser.parse(reader);
        return (JSONArray) obj;
    }
}
