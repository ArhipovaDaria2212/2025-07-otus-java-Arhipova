package ru.arhipova;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.List;

@SuppressWarnings("java:S106")
public class HelloOtus {
    public static void main(String[] args) {
        List<String> names = Lists.newArrayList("Gradle", "Guava", "ShadowJar", "Java");

        String joined = Joiner.on(", ").join(names);

        System.out.println("Hello, OTUS!");
        System.out.println("Список технологий: " + joined);
    }
}
