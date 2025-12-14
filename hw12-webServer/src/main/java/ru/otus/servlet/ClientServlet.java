package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import ru.otus.model.response.ClientResponse;
import ru.otus.services.ClientService;
import ru.otus.services.TemplateProcessor;

@SuppressWarnings({"java:S1989"})
public class ClientServlet extends HttpServlet {

    private static final String CLIENT_PAGE_TEMPLATE = "clients.html";
    private static final String TEMPLATE_ATTR_ALL_CLIENTS = "clients";

    private final transient ClientService clientService;
    private final transient TemplateProcessor templateProcessor;

    public ClientServlet(TemplateProcessor templateProcessor, ClientService clientService) {
        this.templateProcessor = templateProcessor;
        this.clientService = clientService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        List<ClientResponse> clients = clientService.getAllClients();

        response.setContentType("text/html");
        response.getWriter()
                .println(templateProcessor.getPage(CLIENT_PAGE_TEMPLATE, Map.of(TEMPLATE_ATTR_ALL_CLIENTS, clients)));
    }
}
