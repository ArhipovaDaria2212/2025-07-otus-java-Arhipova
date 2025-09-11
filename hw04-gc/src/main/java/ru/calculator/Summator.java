package ru.calculator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Summator {
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;
    // !!! эта коллекция должна остаться. Заменять ее на счетчик нельзя.
    private final List<Data> listValues = new ArrayList<>();
    private final SecureRandom random = new SecureRandom();

    // !!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        final int value = data.getValue();
        listValues.add(data);

        final int currentSize = listValues.size();
        if (currentSize % 100_000 == 0) {
            listValues.clear();
        }
        sum += value + random.nextInt();

        sumLastThreeValues = value + prevValue + prevPrevValue;

        prevPrevValue = prevValue;
        prevValue = value;

        int valueToSum = sumLastThreeValues * sumLastThreeValues / (value + 1) - sum;
        calcSomeValue(valueToSum, currentSize);
        calcSomeValue(valueToSum, currentSize);
        calcSomeValue(valueToSum, currentSize);
    }

    private void calcSomeValue(int valueToSum, int currentSize) {
        someValue += valueToSum;
        someValue = (someValue < 0 ? -someValue : someValue) + currentSize;
    }

    public Integer getSum() {
        return sum;
    }

    public Integer getPrevValue() {
        return prevValue;
    }

    public Integer getPrevPrevValue() {
        return prevPrevValue;
    }

    public Integer getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public Integer getSomeValue() {
        return someValue;
    }
}
