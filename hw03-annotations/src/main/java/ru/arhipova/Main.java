package ru.arhipova;

import ru.arhipova.framework.TestFramework;
import ru.arhipova.tests.TestFrameworkFailSetUpTest;
import ru.arhipova.tests.TestFrameworkFailTearDownTest;
import ru.arhipova.tests.TestFrameworkTest;

public class Main {
    public static void main(String[] args) {
        TestFramework.run(TestFrameworkTest.class);
        TestFramework.run(TestFrameworkFailSetUpTest.class);
        TestFramework.run(TestFrameworkFailTearDownTest.class);
    }
}
