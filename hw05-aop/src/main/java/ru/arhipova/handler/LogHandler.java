package ru.arhipova.handler;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.arhipova.annotations.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

@UtilityClass
@SuppressWarnings({"unchecked", "java:S106"})
public class LogHandler {
    @SneakyThrows
    public static <T, I> I addLogToClass(Class<T> clazz, Class<I> inter) {
        if (!inter.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not implement " + inter);
        }

        Constructor<T> constructor = clazz.getConstructor();
        InvocationHandler handler = new LogInvocationHandler<>(constructor.newInstance());

        return (I) Proxy.newProxyInstance(LogHandler.class.getClassLoader(), new Class<?>[] {inter}, handler);
    }

    @AllArgsConstructor
    private static class LogInvocationHandler<T> implements InvocationHandler {
        private final T clazz;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method implMethod = clazz.getClass().getMethod(method.getName(), method.getParameterTypes());

            if (implMethod.isAnnotationPresent(Log.class)) {
                System.out.println("executed method: " + method.getName()
                        + Arrays.toString(args).replaceAll("\\[(.*?)]", "($1)"));
            }

            return method.invoke(clazz, args);
        }
    }
}
