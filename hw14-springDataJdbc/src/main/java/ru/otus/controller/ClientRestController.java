package ru.otus.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.model.entity.Client;
import ru.otus.model.response.ClientResponse;
import ru.otus.service.ClientService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ClientRestController {

    private final ClientService clientService;

    @PostMapping("/api/client")
    public ClientResponse saveClient(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    @GetMapping("/api/client")
    public List<ClientResponse> saveClient() {
        return clientService.getAllClients();
    }
}
