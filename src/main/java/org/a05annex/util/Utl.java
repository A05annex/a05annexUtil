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
     * use {@link #setOnce(Class, String, Object)}.
     *
     * @param <T>      The type of the value being set.
     * @param instance The object instance whose field is to be set. The instance's fields
     *                 will be inspected for a field named {@code fieldName} assignable from the type of
     *                 {@code newValue}.
     * @param fieldName The name of the field you want to set.
     * @param newValue The value to set in the {@code instance} and {@code fieldName}. It must match the type of
     *                 {@code fieldName}.
     * @return The value that was successfully set in the instance.
     * @throws IllegalArgumentException Thrown if a field named {@code fieldName} was not found, or if the
     *                                  {@code newValue} type could not be used as the value for {@code fieldName}.
     * @throws IllegalStateException Thrown if {@code fieldName} has already been set.
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
     * Sets the value of a static field in the given class exactly once. If a matching static field
     * is already set, or if no suitable field exists to set, an exception is thrown.
     * <p>
     * This method is for setting **static variables** only. For setting instance variables,
     * use {@link #setOnce(Object, String, Object)}.
     *
     * @param <T>       The type of the value being set.
     * @param clazz     The class whose static field is to be set. The class's static fields
     *                  will be inspected for a field named {@code fieldName} assignable from the type of
     *                  {@code newValue}.
     * @param fieldName The name of the static field you want to set.
     * @param newValue  The value to set in the {@code clazz} and {@code fieldName}. It must match the type of
     *                  {@code fieldName}.
     * @return The value that was successfully set in the static field.
     * @throws IllegalArgumentException Thrown if a static field named {@code fieldName} was not found, or if the
     *                                  {@code newValue} type could not be used as the value for {@code fieldName}.
     * @throws IllegalStateException    Thrown if the static field {@code fieldName} has already been set.
     */
    public static <T> T setOnce(@NotNull Class<?> clazz, String fieldName, @NotNull T newValue) {
        try {
            // Step 1: Loop over declared fields in the given class
            for (Field field : clazz.getDeclaredFields()) {
                String thisFieldName = field.getName();
                // Step 2: Check if the field name matches
                if (fieldName.equals(thisFieldName)) {
                    // Step 3: Ensure the field is static
                    if (!Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalArgumentException(String.format("Field '%s' is not static", fieldName));
                    }
                    // Step 4: Make the field accessible and check if it is already set
                    field.setAccessible(true);
                    Object currentValue = field.get(null); // Static fields use null for the instance
                    if (currentValue != null) {
                        throw new IllegalStateException(String.format("The static field '%s' is already set", fieldName));
                    }
                    // Step 5: Check if the type matches and set the value
                    if (field.getType().isAssignableFrom(newValue.getClass())) {
                        field.set(null, newValue); // Set static field value
                        return newValue;
                    } else {
                        throw new IllegalArgumentException(String.format("Static field '%s' cannot be set to a '%s'",
                                fieldName, newValue.getClass().getName()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Failed to access static field '%s'", fieldName), e);
        }
        throw new IllegalArgumentException(String.format("No static field '%s' found in class '%s'", fieldName, clazz.getName()));
    }


    // -----------------------------------------------------------------------------------------------------------------
    // Instantiation by reflection - we are finding increasingly more cases where we are writing library code that
    // implements extensibility through reflection. For example, our path planning software lets the user specify
    // commands to be run by command class name and arguments - but it has no access to the actual robot code it only
    // knows the command class and arguments. In the robot library, the command that runs the path knows about the wpi
    // library code structure - but doesn't know what has been programmed for this year's robot, so it needs to make
    // sure and instantiated object is (perhaps) an instance of a library type, or, implements some library interfaces.
    //
    // The next couple methods implement reflection instantiation of both a no-argument constructor, and a constructor
    // with arguments, with checks that the constructed object is of the expected type and implements the
    // expected interfaces.
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Instantiate an object using a constructor with a specified argument signature and initial values.
     * @param <T>       The type object we are expecting to instantiate. Note, this may be a base class extended by
     *                  {@code clazzName} or an interface implemented by {@code clazzName}
     * @param returnClazz The class of type {@code <T>}.
     * @param clazzName The fully qualified object class name.
     * @param parameterTypes An array of the object classes for constructor arguments.
     * @param instArgs An array of the values for constructor arguments.
     * @return Returns the instantiated object, or {@code null} if the object could not be instantiated.
     */
    public static <T> T instantiateObjectFromName(@NotNull Class<T> returnClazz, @NotNull String clazzName,
                                                  @NotNull Class<?>[] parameterTypes, @NotNull Object[] instArgs) {
        try {
            Object obj = null;
            Class clazz = Class.forName(clazzName);
            if (parameterTypes.length != instArgs.length) {
                System.out.printf("Could not instantiate object: class='%s':\n", clazzName);
                System.out.printf("  'parameterTypes' list length (%d) is not equal to 'instArgs' length (%d)\n",
                        parameterTypes.length, instArgs.length);
                return null;
            } else if (0 == parameterTypes.length) {
                obj = clazz.getDeclaredConstructor().newInstance();
            } else {
                obj = clazz.getDeclaredConstructor(parameterTypes).newInstance(instArgs);
            }
            return returnClazz.cast(obj);
        } catch (final ClassCastException t) {
            System.out.printf("Could not instantiate object: class='%s' as a '%s'\n",
                    clazzName, returnClazz.getCanonicalName());
        } catch (final ClassNotFoundException t) {
            System.out.printf("Could not instantiate object: class='%s'; class not found.\n", clazzName);
        } catch (final NoSuchMethodException t) {
            System.out.printf(
                    "Could not instantiate object: class='%s'; no constructor matching the 'parameterTypes'.\n",
                    clazzName);
        } catch (final IllegalArgumentException t) {
            System.out.printf(
                    "Could not instantiate object: class='%s'; the 'instArgs' types do not match the 'parameterTypes'.\n",
                    clazzName);
        } catch (final Exception t) {
            System.out.printf("Could not instantiate object: class='%s' - no details.\n", clazzName);
        }
        return null;
    }
    /**
     * Instantiate an object using the default no argument constructor
     * @param <T>       The type object we are expecting to instantiate. Note, this may be a base class extended by
     *                  {@code clazzName} or an interface implemented by {@code clazzName}
     * @param returnClazz The class of type {@code <T>}.
     * @param clazzName The fully qualified object class name.
     * @return Returns the instantiated object, or {@code null} if the object could not be instantiated.
     */
    public static <T> T instantiateObjectFromName(@NotNull Class<T> returnClazz, @NotNull String clazzName) {
        return instantiateObjectFromName(returnClazz, clazzName, new Class<?>[] {}, new Object[] {});
    }
}
