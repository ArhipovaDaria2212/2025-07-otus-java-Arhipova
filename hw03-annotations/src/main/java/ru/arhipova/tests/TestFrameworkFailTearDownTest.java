package ru.arhipova.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arhipova.annotations.After;
import ru.arhipova.annotations.Test;

@SuppressWarnings("java:S112")
public class TestFrameworkFailTearDownTest {
    private static final Logger logger = LoggerFactory.getLogger(TestFrameworkFailTearDownTest.class);
    private String message;

    @Test
    public void successfulTest() {
        message = "Success";
        logger.info("message: {}", message);
    }

    @Test
    public void failTest() {
        logger.info("message: {}", message);
        throw new RuntimeException("Fail");
    }

    @After
    public void tearDown() {
        throw new RuntimeException("Exception while tear down");
    }
}
