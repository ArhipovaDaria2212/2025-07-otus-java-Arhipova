package ru.otus.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.model.entity.Client;
import ru.otus.model.response.ClientResponse;
import ru.otus.service.ClientService;

import java.util.List;

@Controller
@AllArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public String clientsListView(Model model) {
        List<ClientResponse> clients = clientService.getAllClients();
        model.addAttribute("client", new Client());
        model.addAttribute("clients", clients);
        return "clients";
    }

    @PostMapping
    public String createClient(@ModelAttribute Client client) {
        clientService.createClient(client);
        return "redirect:/";
    }
}
