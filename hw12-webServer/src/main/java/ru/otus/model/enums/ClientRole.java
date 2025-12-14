package ru.otus.model.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientRole {
    USER("user"),
    ADMIN("admin");

    private final String name;

    public static void verify(String role) {
        Arrays.stream(values())
                .filter(value -> value.getName().equals(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Role with name %s not found", role)));
    }
}
