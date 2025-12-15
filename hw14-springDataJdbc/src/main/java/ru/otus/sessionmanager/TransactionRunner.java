package ru.otus.sessionmanager;

public interface TransactionRunner {

    <T> T doInTransaction(TransactionAction<T> action);

    <T> T doInReadOnlyTransaction(TransactionAction<T> action);
}
