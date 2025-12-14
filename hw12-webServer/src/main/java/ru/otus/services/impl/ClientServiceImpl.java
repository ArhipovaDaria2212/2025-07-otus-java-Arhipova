package ru.otus.services.impl;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import ru.otus.dao.ClientDao;
import ru.otus.model.entity.Address;
import ru.otus.model.entity.Client;
import ru.otus.model.entity.Phone;
import ru.otus.model.enums.ClientRole;
import ru.otus.model.response.AddressResponse;
import ru.otus.model.response.ClientResponse;
import ru.otus.model.response.PhoneResponse;
import ru.otus.services.ClientService;

@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final Gson gson;

    @Override
    public List<ClientResponse> getAllClients() {
        return clientDao.findAll().stream().map(this::clientToClientResponse).toList();
    }

    @Override
    public ClientResponse createClient(HttpServletRequest request) {
        Client client = clientDao.saveClient(createClientFromRequest(request));
        return clientToClientResponse(client);
    }

    @SuppressWarnings("java:S112")
    private Client createClientFromRequest(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            Client client = gson.fromJson(reader, Client.class);
            ClientRole.verify(client.getRole());

            if (client.getPhones() != null) {
                client.getPhones().forEach(phone -> phone.setClient(client));
            }

            return client;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create client from response", e);
        }
    }

    private ClientResponse clientToClientResponse(Client client) {
        if (client == null) {
            return null;
        }
        AddressResponse addressResponse = addressToAddressResponse(client.getAddress());
        List<PhoneResponse> phoneResponses =
                client.getPhones().stream().map(this::phoneToPhoneResponse).toList();

        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .login(client.getLogin())
                .address(addressResponse)
                .phones(phoneResponses)
                .build();
    }

    private AddressResponse addressToAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .build();
    }

    private PhoneResponse phoneToPhoneResponse(Phone phone) {
        if (phone == null) {
            return null;
        }

        return PhoneResponse.builder()
                .id(phone.getId())
                .number(phone.getNumber())
                .build();
    }
}
