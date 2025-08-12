package ru.arhipova.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.arhipova.annotations.After;
import ru.arhipova.annotations.Before;
import ru.arhipova.annotations.Test;

@SuppressWarnings("java:S112")
public class TestFrameworkTest {
    private static final Logger logger = LoggerFactory.getLogger(TestFrameworkTest.class);
    private String message;

    @Before
    public void setUp() {
        logger.info("Set up");
    }

    @Before
    public void anotherSetUp() {
        logger.info("Another set up");
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

    @After
    public void tearDown() {
        logger.info("Tear down");
    }

    @After
    public void anotherTearDown() {
        logger.info("Another tear down");
    }
}
