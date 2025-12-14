package ru.otus.server;

@SuppressWarnings({"squid:S112"})
public interface WebServer {
    void start() throws Exception;

    void join() throws Exception;

    void stop() throws Exception;
}
