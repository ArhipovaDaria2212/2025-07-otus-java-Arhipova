package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import ru.otus.model.entity.Client;

public interface ClientDao {

    Client saveClient(Client client);

    Optional<Client> getClient(long id);

    List<Client> findAll();

    Optional<Client> findByLogin(String login);
}
