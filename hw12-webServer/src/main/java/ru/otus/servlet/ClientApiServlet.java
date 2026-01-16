package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import ru.otus.model.response.ClientResponse;
import ru.otus.services.ClientService;

@SuppressWarnings({"java:S1989"})
public class ClientApiServlet extends HttpServlet {
    private final transient ClientService clientService;
    private final transient Gson gson;

    public ClientApiServlet(ClientService clientService, Gson gson) {
        this.clientService = clientService;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClientResponse client = clientService.createClient(request);

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(client));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ClientResponse> clients = clientService.getAllClients();

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(clients));
    }
}
