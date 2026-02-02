package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница клиентов
    http://localhost:8080/clients

    // REST сервис
    http://localhost:8080/api/client/
*/
@EnableJdbcRepositories
@SpringBootApplication
public class SpringDataJdbcDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringDataJdbcDemo.class, args);
    }
}
