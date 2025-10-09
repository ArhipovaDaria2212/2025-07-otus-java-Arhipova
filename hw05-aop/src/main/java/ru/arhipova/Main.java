package ru.arhipova;

import lombok.extern.slf4j.Slf4j;
import ru.arhipova.handler.LogHandler;

/**
 * java -javaagent:testLogger.jar -jar testLogger.jar
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Inserting a log with the name of the annotated @Log method and its parameters using javaagent:\n");
        TestLogging testLogging = new TestLoggingImpl();
        testLogging.calculation(123);
        testLogging.calculation(123, 321L);
        testLogging.calculation(123, 321L, "text");

        log.info(
                "Inserting a log with the name of the annotated @Log method and its parameters using dynamic proxy:\n");
        TestLogging dynamicProxy = LogHandler.addLogToClass(TestLoggingImpl.class, TestLogging.class);
        dynamicProxy.calculation(123);
        dynamicProxy.calculation(123, 321L);
        dynamicProxy.calculation(123, 321L, "text");
    }
}
