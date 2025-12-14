package ru.otus.services;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import ru.otus.model.response.ClientResponse;

public interface ClientService {
    List<ClientResponse> getAllClients();

    ClientResponse createClient(HttpServletRequest request);
}
