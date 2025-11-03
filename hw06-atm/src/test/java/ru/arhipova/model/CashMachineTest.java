package ru.arhipova.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.arhipova.model.enums.NominalValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CashMachineTest {
    private ATM cashMachine;
    private static final long EXPECTED_TOTAL = 62900L;

    @BeforeEach
    void setUp() {
        cashMachine = new CashMachine(Map.of(
                NominalValue.FIFTY_RUBLES, 2L,
                NominalValue.ONE_HUNDRED_RUBLES, 3L,
                NominalValue.FIVE_HUNDRED_RUBLES, 5L,
                NominalValue.ONE_THOUSAND_RUBLES, 6L,
                NominalValue.TWO_THOUSAND_RUBLES, 7L,
                NominalValue.FIVE_THOUSAND_RUBLES, 8L));
    }

    @Test
    @DisplayName("Создание банкомата только с номиналами без начального наполнения")
    void create_with_only_nominals_success() {
        ATM emptyATM = new CashMachine(
                List.of(NominalValue.FIFTY_RUBLES, NominalValue.ONE_HUNDRED_RUBLES, NominalValue.FIVE_HUNDRED_RUBLES));

        Map<NominalValue, Long> restNominals = emptyATM.getRestNominalsCounts();

        assertEquals(0L, emptyATM.getRestCount());
        assertEquals(0L, restNominals.get(NominalValue.FIFTY_RUBLES));
        assertEquals(0L, restNominals.get(NominalValue.ONE_HUNDRED_RUBLES));
        assertEquals(0L, restNominals.get(NominalValue.FIVE_HUNDRED_RUBLES));
        assertEquals(3, restNominals.size());
    }

    @Test
    @DisplayName("Создание банкомата с отрицательным количеством банкнот")
    void create_with_negative_count_fail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cashMachine = new CashMachine(Map.of(
                        NominalValue.FIFTY_RUBLES, 2L,
                        NominalValue.ONE_HUNDRED_RUBLES, -3L)));
    }

    @Test
    @DisplayName("Успешное внесение денег в банкомат")
    void deposit_success() {
        long expectedFiftyBanknotes = 3;
        long expectedOneHundredBanknotes = 8;
        long total = EXPECTED_TOTAL + 50 + 500;
        Map<NominalValue, Long> moneyStack = Map.of(
                NominalValue.FIFTY_RUBLES, 1L,
                NominalValue.ONE_HUNDRED_RUBLES, 5L);

        cashMachine.deposit(moneyStack);

        assertEquals(expectedFiftyBanknotes, cashMachine.getRestNominalsCounts().get(NominalValue.FIFTY_RUBLES));
        assertEquals(
                expectedOneHundredBanknotes, cashMachine.getRestNominalsCounts().get(NominalValue.ONE_HUNDRED_RUBLES));
        assertEquals(total, cashMachine.getRestCount());
    }

    @Test
    @DisplayName("Неуспешное внесение денег в банкомат, попытка внести отрицательное количество банкнот")
    void deposit_with_negative_count_fail() {
        Map<NominalValue, Long> moneyStack = Map.of(NominalValue.FIFTY_RUBLES, -1L);

        assertThrows(IllegalArgumentException.class, () -> cashMachine.deposit(moneyStack));
        assertEquals(EXPECTED_TOTAL, cashMachine.getRestCount());
    }

    @Test
    @DisplayName("Успешное снятие денег")
    void withdraw_success() {
        Map<NominalValue, Long> expectedResult = Map.of(
                NominalValue.FIFTY_RUBLES, 1L,
                NominalValue.ONE_HUNDRED_RUBLES, 1L,
                NominalValue.FIVE_HUNDRED_RUBLES, 1L,
                NominalValue.ONE_THOUSAND_RUBLES, 1L,
                NominalValue.TWO_THOUSAND_RUBLES, 1L,
                NominalValue.FIVE_THOUSAND_RUBLES, 2L);
        long countToWithdraw = 13_650L;
        long expectedCount = EXPECTED_TOTAL - countToWithdraw;

        Map<NominalValue, Long> result = cashMachine.withdraw(countToWithdraw);

        assertEquals(expectedResult, result);
        assertEquals(expectedCount, cashMachine.getRestCount());
    }

    @Test
    @DisplayName("Не успешное снятие, недостаточно денег в банкомате")
    void withdraw_too_less_money_in_atm_fail() {
        assertThrows(IllegalArgumentException.class, () -> cashMachine.withdraw(100_000L));
        assertEquals(EXPECTED_TOTAL, cashMachine.getRestCount());
    }

    @Test
    @DisplayName("Не успешное снятие, недостаточно денег в банкомате")
    void withdraw_too_less_banknotes_fail() {
        assertThrows(IllegalArgumentException.class, () -> cashMachine.withdraw(1012L));
        assertEquals(EXPECTED_TOTAL, cashMachine.getRestCount());
    }

    @Test
    @DisplayName("Попытка внести номинал, который банкомат не поддерживает")
    void deposit_unsupported_nominal_fail() {
        Map<NominalValue, Long> moneyStack = Map.of(
                NominalValue.ONE_HUNDRED_RUBLES, 2L,
                NominalValue.TWO_HUNDRED_RUBLES, 1L);

        assertThrows(IllegalArgumentException.class, () -> cashMachine.deposit(moneyStack));
    }

    @Test
    @DisplayName("Получение общей суммы средств в банкомате")
    void getRestCount() {
        Map<NominalValue, Long> depositMoney = Map.of(NominalValue.ONE_THOUSAND_RUBLES, 2L);
        long total = EXPECTED_TOTAL + 2000;

        cashMachine.deposit(depositMoney);
        assertEquals(total, cashMachine.getRestCount());

        cashMachine.withdraw(5000L);
        assertEquals(total - 5000, cashMachine.getRestCount());
    }

    @Test
    @DisplayName("Получение остатка банкнот по номиналам")
    void getRestNominalsCounts() {
        Map<NominalValue, Long> restNominals = cashMachine.getRestNominalsCounts();

        assertEquals(2L, restNominals.get(NominalValue.FIFTY_RUBLES));
        assertEquals(3L, restNominals.get(NominalValue.ONE_HUNDRED_RUBLES));
        assertEquals(5L, restNominals.get(NominalValue.FIVE_HUNDRED_RUBLES));
        assertEquals(6L, restNominals.get(NominalValue.ONE_THOUSAND_RUBLES));
        assertEquals(7L, restNominals.get(NominalValue.TWO_THOUSAND_RUBLES));
        assertEquals(8L, restNominals.get(NominalValue.FIVE_THOUSAND_RUBLES));

        restNominals.put(NominalValue.FIFTY_RUBLES, 999L);
        assertEquals(2L, cashMachine.getRestNominalsCounts().get(NominalValue.FIFTY_RUBLES));

        List<NominalValue> expectedOrder = List.of(
                NominalValue.FIVE_THOUSAND_RUBLES,
                NominalValue.TWO_THOUSAND_RUBLES,
                NominalValue.ONE_THOUSAND_RUBLES,
                NominalValue.FIVE_HUNDRED_RUBLES,
                NominalValue.ONE_HUNDRED_RUBLES,
                NominalValue.FIFTY_RUBLES);

        List<NominalValue> actualOrder =
                new ArrayList<>(cashMachine.getRestNominalsCounts().keySet());
        assertEquals(expectedOrder, actualOrder);
    }
}
