package ru.otus.service;

import ru.otus.model.entity.Client;
import ru.otus.model.response.ClientResponse;

import java.util.List;

public interface ClientService {
    List<ClientResponse> getAllClients();

    ClientResponse createClient(Client client);
}
