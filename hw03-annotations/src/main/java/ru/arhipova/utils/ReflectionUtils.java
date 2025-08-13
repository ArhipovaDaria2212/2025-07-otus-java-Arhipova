package ru.arhipova.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("java:S3011")
public class ReflectionUtils {
    private ReflectionUtils() {}

    public static <T> T getInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    public static Throwable runMethod(Method method, Object instance) {
        try {
            method.setAccessible(true);
            method.invoke(instance);
            return null;
        } catch (InvocationTargetException e) {
            return e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to invoke method " + method.getName(), e);
        }
    }
}
