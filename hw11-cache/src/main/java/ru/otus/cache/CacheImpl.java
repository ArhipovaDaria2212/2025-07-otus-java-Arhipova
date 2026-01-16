package ru.otus.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheImpl<K, V> implements Cache<K, V> {
    private final WeakHashMap<K, V> cache;
    private final List<Listener<K, V>> listeners;

    public CacheImpl() {
        cache = new WeakHashMap<>();
        listeners = new ArrayList<>();
    }

    @Override
    public void put(K key, V value) {
        Action action = cache.put(key, value) == null ? Action.CREATE : Action.EDIT;
        notifyAll(key, value, action);
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        notifyAll(key, value, Action.REMOVE);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            notifyAll(key, value, Action.GET);
        }
        return value;
    }

    @Override
    public void addListener(Listener<K, V> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener<K, V> listener) {
        this.listeners.remove(listener);
    }

    public enum Action {
        CREATE,
        EDIT,
        REMOVE,
        GET;
    }

    private void notifyAll(K key, V value, Action action) {
        listeners.forEach(listener -> {
            try {
                listener.notify(key, value, String.valueOf(action));
            } catch (Exception e) {
                log.error("Exception was thrown while notify listener: {}", e.getMessage());
            }
        });
    }
}
