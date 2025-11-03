package ru.arhipova.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import ru.arhipova.model.enums.NominalValue;

/**
 * Реализация простого банкомата.
 */
public class CashMachine implements ATM {
    private long total;
    private SortedMap<NominalValue, Long> cells;
    private static final long INITIAL_COUNT_IN_CELL = 0;

    /**
     * Создать банкомат с наполненными ячейками и банкнотами,
     * которые банкомат может принимать.
     *
     * @param initialState начальное состояние банкомата.
     */
    public CashMachine(Map<NominalValue, Long> initialState) {
        validate(initialState);

        cells = new TreeMap<>(Comparator.comparingLong(NominalValue::getValue).reversed());
        cells.putAll(initialState);

        updateTotal(initialState);
    }

    /**
     * Создать банкомат.
     *
     * @param nominals номиналы, которые может принимать банкомат.
     */
    public CashMachine(List<NominalValue> nominals) {
        cells = new TreeMap<>(Comparator.comparing(NominalValue::getValue));
        nominals.forEach(nominal -> cells.put(nominal, INITIAL_COUNT_IN_CELL));
    }

    /**
     * Внести деньги.
     *
     * @param money список банкнот и их количество для внесения в банкомат.
     */
    @Override
    public void deposit(Map<NominalValue, Long> money) {
        validate(money);

        money.forEach((nominal, count) -> {
            long newCount = cells.get(nominal) + count;
            cells.replace(nominal, newCount);
        });

        updateTotal(money);
    }

    /**
     * Снять деньги.
     *
     * @param amount сумма для снятия
     * @return список банкнот и их количество.
     */
    @Override
    public Map<NominalValue, Long> withdraw(Long amount) {
        if (amount > total) {
            throw new IllegalArgumentException("Запрошенная сумма больше, чем есть в банкомате");
        }

        long tempAmount = amount;
        SortedMap<NominalValue, Long> tempCells = new TreeMap<>(cells);
        Map<NominalValue, Long> result = new HashMap<>();

        for (Map.Entry<NominalValue, Long> entry : tempCells.entrySet()) {
            if (tempAmount == 0) {
                break;
            }

            NominalValue nominal = entry.getKey();
            Long availableCount = entry.getValue();

            if (tempAmount >= nominal.getValue() && availableCount > 0) {
                long needed = tempAmount / nominal.getValue();
                long countToDelivered = Math.min(needed, availableCount);

                if (needed > 0) {
                    result.put(nominal, countToDelivered);
                    tempCells.replace(nominal, tempCells.get(nominal) - countToDelivered);
                    tempAmount -= nominal.getValue() * countToDelivered;
                }
            }
        }

        if (tempAmount > 0) {
            throw new IllegalArgumentException("Невозможно выдать запрошенную сумму, не хватает купюр");
        }

        cells = tempCells;
        total = total - amount;

        return result;
    }

    /**
     * Получение остатка денежных средств в банкомате.
     *
     * @return общая сумма средств в банкомате
     */
    @Override
    public long getRestCount() {
        return total;
    }

    /**
     * Получение остатка денежных средств в банкомате.
     *
     * @return список оставшихся в банкомате банкнот и их количество.
     */
    @Override
    public Map<NominalValue, Long> getRestNominalsCounts() {
        return new TreeMap<>(cells);
    }

    private void updateTotal(Map<NominalValue, Long> money) {
        money.forEach((nominal, count) -> total += count * nominal.getValue());
    }

    private void validate(Map<NominalValue, Long> money) {
        money.forEach(((nominal, count) -> {
            if (count < 0) {
                throw new IllegalArgumentException("Количество не может быть меньше нуля");
            }
            if (cells != null && !cells.containsKey(nominal)) {
                throw new IllegalArgumentException("Банкомат не поддерживает номинал: " + nominal);
            }
        }));
    }
}
