package ru.otus.sessionmanager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionRunnerSpring implements TransactionRunner {

    @Override
    @Transactional
    public <T> T doInTransaction(TransactionAction<T> action) {
        return action.get();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T doInReadOnlyTransaction(TransactionAction<T> action) {
        return action.get();
    }
}
