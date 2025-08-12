package ru.arhipova.framework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699")
@Disabled
class TestFrameworkTest {
    private String message;

    @BeforeEach
    void setUp() {
        System.out.println("Set up");
    }

    @BeforeEach
    void anotherSetUp() {
        System.out.println("Another set up");
    }

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
        System.out.println("Tear down");
    }

    @AfterEach
    void anotherTearDown() {
        System.out.println("Another tear down");
    }
}
