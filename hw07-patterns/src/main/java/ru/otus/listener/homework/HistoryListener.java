package ru.otus.listener.homework;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {
    private final ConcurrentMap<Long, Message> history = new ConcurrentHashMap<>();

    @Override
    public void onUpdated(Message msg) {
        history.put(msg.getId(), new Message(msg));
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        Message message = history.get(id);
        return message != null ? Optional.of(new Message(message)) : Optional.empty();
    }
}
