package ru.arhipova.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NominalValue {
    FIFTY_RUBLES(50),
    ONE_HUNDRED_RUBLES(100),
    TWO_HUNDRED_RUBLES(200),
    FIVE_HUNDRED_RUBLES(500),
    ONE_THOUSAND_RUBLES(1_000),
    TWO_THOUSAND_RUBLES(2_000),
    FIVE_THOUSAND_RUBLES(5_000);

    private final long value;
}
