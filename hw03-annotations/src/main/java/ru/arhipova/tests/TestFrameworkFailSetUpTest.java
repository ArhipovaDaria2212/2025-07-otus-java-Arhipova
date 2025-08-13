package ru.arhipova.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arhipova.annotations.Before;
import ru.arhipova.annotations.Test;

@SuppressWarnings("java:S112")
public class TestFrameworkFailSetUpTest {
    private static final Logger logger = LoggerFactory.getLogger(TestFrameworkFailSetUpTest.class);
    private String message;

    @Before
    public void setUp() {
        throw new RuntimeException("Exception while set up");
    }

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
}
