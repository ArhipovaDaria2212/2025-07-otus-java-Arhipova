package ru.otus.processor.homework;

import java.time.DateTimeException;
import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorThrowsExceptionAtEvenSecond implements Processor {

    private final DateTimeProvider dateTimeProvider;

    public ProcessorThrowsExceptionAtEvenSecond(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        if (isEven(dateTimeProvider.getDate().getSecond())) {
            throw new DateTimeException("Метод вызван в четную секунду");
        }

        return message;
    }

    private boolean isEven(int number) {
        return number % 2 == 0;
    }
}
