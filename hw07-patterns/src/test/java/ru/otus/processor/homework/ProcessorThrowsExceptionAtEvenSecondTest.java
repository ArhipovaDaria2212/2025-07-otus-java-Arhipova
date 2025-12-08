package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.model.Message;

@ExtendWith(MockitoExtension.class)
class ProcessorThrowsExceptionAtEvenSecondTest {
    @Mock
    DateTimeProvider dateTimeProvider;

    @InjectMocks
    private ProcessorThrowsExceptionAtEvenSecond processor;

    @Test
    @DisplayName("Процессор выбрасывает исключение, так как вызывается в четную секунду времени")
    void processTest() {
        Message message = new Message.Builder(1L).build();

        when(dateTimeProvider.getDate()).thenReturn(LocalDateTime.of(2025, 12, 31, 23, 58));

        assertThrows(DateTimeException.class, () -> processor.process(message));
    }
}
