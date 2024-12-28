package org.a05annex.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This is a utility class with some commonly used math constants and utility methods
 */
public final class Utl {

    /**
     * This class is all static constants and methods, it cannot be instantiated.
     */
    private Utl() {
    }

    /**
     * Get the length of an n-dimensional set of lengths.
     *
     * @param values The dimensional lengths. The number of dimensional lengths is variable and may be zero.
     * @return The n-dimensional length, 0.0 of no dimensional lengths are specified.
     */
    public static double length(double... values) {
        double lengthSquared = 0.0;
        for (double v : values) {
            lengthSquared += v * v;
        }
        return Math.sqrt(lengthSquared);
    }

    /**
     * Get the maximum value of an arbitrary number of values.
     *
     * @param values The values. The number of values is variable and may be zero.
     * @return The maximum value, {@link Double#NEGATIVE_INFINITY} if no values are specified.
     */
    public static double max(double... values) {
        double max = Double.NEGATIVE_INFINITY;
        for (double v : values) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    /**
     * Get the minimum value of an arbitrary number of values.
     *
     * @param values The values. The number of values is variable and may be zero.
     * @return The minimum value, {@link Double#POSITIVE_INFINITY} if no values are specified.
     */
    public static double min(double... values) {
        double min = Double.POSITIVE_INFINITY;
        for (double v : values) {
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    /**
     * clip a value to be within the specified min-max range. Specifically, if {@code value} is
     * less than {@code min}, then set it to {@code min}; if {@code value} is greater than {@code max},
     * then set it to max.
     * @param value The value to be clipped.
     * @param min The minimum of the valid range. If {@code value} is less than {@code min},
     *            then {@code min} will be returned. Set to {@link Double#NEGATIVE_INFINITY} if
     *            there is no lower bound.
     * @param max The maximum of the valid range. If {@code value} is greater than {@code max},
     *            then {@code max} will be returned. Set to {@link Double#POSITIVE_INFINITY} if
     *            there is no upper bound.
     * @return Returns {@code value} clipped to the specified {@code min} {@code max} range.
     * @throws IllegalArgumentException Thrown if {@code (min >= max)}.
     */
    public static double clip(double value, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max.");
        }
        return Math.min(Math.max(value, min), max);
    }

    /**
     Determines if a given value is within a certain tolerance of a target value.
     @param value the value to compare with the target value
     @param target the target value to compare against
     @param tolerance the maximum amount by which the value may differ from the target value and still be considered "within tolerance"
     @return true if the absolute difference between the value and target is less than or equal to the tolerance, false otherwise
     */
    public static boolean inTolerance(double value, double target, double tolerance) {
        return Math.abs(value - target) <= tolerance;
    }


    /**
     * Sets the value of a field in the given instance exactly once. If a matching field
     * is already set, or if no suitable field exists to set, an exception is thrown.
     * <p>
     * This method is for setting **instance variables** only. For setting static variables,
     * use {@link #setOnce(Object, Class)}.
     *
     * @param <T>      The type of the value being set.
     * @param instance The object instance whose field is to be set. The instance's fields
     *                 will be inspected for a field named {@code fieldName} assignable from the type of
     *                 {@code newValue}.
     * @param fieldName The name of the field you want to set.
     * @param newValue The value to set in the {@code instance} and {@code fieldName}. It must match the type of
     *                 {@code fieldName}.
     * @return The value that was successfully set in the instance.
     * @throws IllegalStateException If a suitable field in the instance is already set,
     *                               if no suitable field exists, or if there is an error
     *                               accessing instance fields.
     */
    public static <T> T setOnce(@NotNull Object instance, String fieldName, @NotNull T newValue) {
        try {
            // Step 1: Loop over instance fields for the given object
            for(Field field : instance.getClass().getDeclaredFields()) {
                String thisFieldName = field.getName();
                // Step 2: find the matching field name
                if (fieldName.equals(thisFieldName)) {
                    // Step 3: found the right field, now set it if it has not already been set.
                    // Make the field accessible - which gets past private and final declarations
                    field.setAccessible(true);
                    // Check if field is already set
                    Object currentValue = field.get(instance);
                    if (currentValue != null) {
                        throw new IllegalStateException(String.format("The field '%s' is already set", fieldName));
                    }
                    // If field matches the newValue's type, set it; otherwise tell the user there is bad code.
                    if (field.getType().isAssignableFrom(newValue.getClass())) {
                        field.set(instance, newValue); // Set instance field value
                        return newValue;
                    } else {
                        throw new IllegalArgumentException(String.format("Field '%s' cannot be set to a '%s'",
                                fieldName, newValue.getClass().getName()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Failed to access instance field '%s'",fieldName), e);
        }
        throw new IllegalArgumentException(String.format("No field '%s' found",fieldName));
    }

    /**
     * Sets the value of a static field in the specified class if and only if
     * the field has not been set already. If a static field in the class
     * already has a non-null value, an exception is thrown to prevent overwriting.
     * <p>
     * This method is for setting **static variables** only. For setting instance variables,
     * use {@link #setOnce(Object, String, Object)}.
     *
     * @param <T>      The type of the value to be set.
     * @param newValue The new value to be assigned to the static field.
     * @param clazz    The class containing the static field to be set.
     * @return The value that was set.
     * @throws IllegalStateException If there is already a non-null value
     *                               in the static field, if no suitable static field is found,
     *                               or if the access to the field fails.
     */
    public static <T> T setOnce(T newValue, Class<?> clazz) {
        try {
            // Iterate through all declared fields in the class
            for(Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                // Check if the field is static
                if(Modifier.isStatic(field.getModifiers())) {
                    Object currentValue = field.get(null); // Get static field value (null for static access)

                    // Throw exception if static field is already set
                    if(currentValue != null) {
                        throw new IllegalStateException("You tried to set the static variable \"" + field.getName() + "\" more than once");
                    }

                    // If the static field matches the newValue's type, set it
                    if(newValue != null && field.getType().isAssignableFrom(newValue.getClass())) {
                        field.set(null, newValue); // Set the static field value
                        return newValue;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access static fields", e);
        }

        throw new IllegalStateException("No suitable static variable was found to set.");
    }
}
