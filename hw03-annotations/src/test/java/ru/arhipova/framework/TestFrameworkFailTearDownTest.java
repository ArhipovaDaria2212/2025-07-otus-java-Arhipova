package ru.arhipova.framework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699")
@Disabled
class TestFrameworkFailTearDownTest {
    String message;

    @Test
    void successfulTest() {
        message = "Success";
        System.out.println("message: " + message);
    }

    @Test
    void failTest() {
        System.out.println("message: " + message);
        throw new RuntimeException("Fail");
    }

    @AfterEach
    void tearDown() {
        throw new RuntimeException("Exception while tear down");
    }
}
