package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.LoggerProcessor;
import ru.otus.processor.homework.ProcessorSwapFields;
import ru.otus.processor.homework.ProcessorThrowsExceptionAtEvenSecond;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
*/
public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var processors = List.of(
                new ProcessorThrowsExceptionAtEvenSecond(LocalDateTime::now),
                new LoggerProcessor(new ProcessorSwapFields()));

        var complexProcessor = new ComplexProcessor(processors, ex -> logger.error(ex.getMessage()));
        var historyListener = new HistoryListener();
        complexProcessor.addListener(historyListener);

        var message = new Message.Builder(1L)
                .field11("field11")
                .field12("field12")
                .field13(new ObjectForMessage(List.of("Element in ObjectForMessage")))
                .build();

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        message.toBuilder()
                .field1("field1 after update")
                .field13(new ObjectForMessage(List.of("Updated element in ObjectForMessage")))
                .build();

        Optional<Message> savedMessage = historyListener.findMessageById(message.getId());
        logger.info("savedMessage:{}", savedMessage);

        complexProcessor.removeListener(historyListener);
    }
}
