package ru.otus.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.model.entity.Client;

@Repository
public interface ClientRepository extends ListCrudRepository<Client, Long> {
}
