package ru.arhipova.model;

import java.util.Map;
import ru.arhipova.model.enums.NominalValue;

public interface ATM {
    void deposit(Map<NominalValue, Long> money);

    Map<NominalValue, Long> withdraw(Long amount);

    long getRestCount();

    Map<NominalValue, Long> getRestNominalsCounts();
}
