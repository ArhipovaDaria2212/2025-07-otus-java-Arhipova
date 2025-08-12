package ru.arhipova.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699")
@Disabled
class TestFrameworkFailSetUpTest {
    private String message;

    @BeforeEach
    void setUp() {
        throw new RuntimeException("Exception while set up");
    }

    @Test
    void successfulTest() {
        message = "Success";
        System.out.println("message: " + message);
    }

    @Test
    void failTest() {
        System.out.println("message: " + message);
        throw new RuntimeException("Fail test");
    }
}
