package ru.arhipova.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.arhipova.annotations.Log;

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

    private static class LogInvocationHandler<T> implements InvocationHandler {
        private final T clazz;
        private final List<Method> methodsToLog;

        public LogInvocationHandler(final T clazz) {
            this.clazz = clazz;
            methodsToLog = Arrays.stream(clazz.getClass().getMethods())
                    .filter(m -> m.isAnnotationPresent(Log.class))
                    .toList();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method implMethod = clazz.getClass().getMethod(method.getName(), method.getParameterTypes());

            if (methodsToLog.contains(implMethod)) {
                System.out.println("executed method: " + method.getName()
                        + Arrays.toString(args).replaceAll("\\[(.*?)]", "($1)"));
            }

            return method.invoke(clazz, args);
        }
    }
}
