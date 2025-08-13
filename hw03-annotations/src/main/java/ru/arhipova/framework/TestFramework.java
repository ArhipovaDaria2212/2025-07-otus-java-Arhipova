package ru.arhipova.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arhipova.annotations.After;
import ru.arhipova.annotations.Before;
import ru.arhipova.annotations.Test;
import ru.arhipova.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Кастомный фреймворк для запуска тестов
 */
public class TestFramework {
    private static final Logger logger = LoggerFactory.getLogger(TestFramework.class);

    private TestFramework() {}

    /**
     * Запустить все тестовые методы класса помеченные аннотациями
     * {@link Test}. Перед запуском непосредственно каждого теста будет
     * вызваны методы, помеченные аннотациями {@link Before} и {@link After} соответственно.
     * @param clazz класс с тестовыми методами
     */
    public static <T> void run(Class<T> clazz) {
        int fails = 0;
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(TestFramework::isMethodNotAnnotated)
                .toList();

        List<Method> beforeMethods = filterMethodsByAnnotation(methods, Before.class);
        List<Method> testMethods = filterMethodsByAnnotation(methods, Test.class);
        List<Method> afterMethods = filterMethodsByAnnotation(methods, After.class);

        logger.info("Run tests for: {}", clazz.getName());

        for (Method method : testMethods) {
            T instance = ReflectionUtils.getInstance(clazz);
            logger.info("Running test: {}", method.getName());

            boolean isSuccess = true;

            isSuccess &= runMethods(beforeMethods, instance);
            if (isSuccess) {
                isSuccess = runMethod(method, instance);
            }
            isSuccess &= runMethods(afterMethods, instance);

            if (!isSuccess) {
                fails++;
            }
        }

        logger.info("{} tests completed: {} success, {} failed", testMethods.size(), testMethods.size() - fails, fails);
    }

    private static boolean runMethods(List<Method> methods, Object instance) {
        boolean isSuccess = true;
        for (Method method : methods) {
            if (!runMethod(method, instance)) {
                isSuccess = false;
            }
        }
        return isSuccess;
    }

    private static boolean runMethod(Method method, Object instance) {
        Throwable exception = ReflectionUtils.runMethod(method, instance);
        if (exception != null) {
            logger.error("Test method failed: {}", method.getName(), exception.getCause());
            return false;
        }
        return true;
    }

    private static boolean isMethodNotAnnotated(Method method) {
        return method.isAnnotationPresent(Before.class)
                || method.isAnnotationPresent(Test.class)
                || method.isAnnotationPresent(After.class);
    }

    private static List<Method> filterMethodsByAnnotation(List<Method> methods, Class<? extends Annotation> clazz) {
        return methods.stream()
                .filter(method -> method.isAnnotationPresent(clazz))
                .toList();
    }
}
