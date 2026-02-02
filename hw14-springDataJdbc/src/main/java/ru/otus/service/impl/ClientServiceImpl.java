package ru.otus.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.model.entity.Address;
import ru.otus.model.entity.Client;
import ru.otus.model.entity.Phone;
import ru.otus.model.response.AddressResponse;
import ru.otus.model.response.ClientResponse;
import ru.otus.model.response.PhoneResponse;
import ru.otus.repository.ClientRepository;
import ru.otus.service.ClientService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream().map(this::clientToClientResponse).toList();
    }

    @Override
    public ClientResponse createClient(Client client) {
        return clientToClientResponse(clientRepository.save(client));
    }

    private ClientResponse clientToClientResponse(Client client) {
        if (client == null) {
            return null;
        }
        AddressResponse addressResponse = addressToAddressResponse(client.getAddress());
        List<PhoneResponse> phoneResponses = new ArrayList<>();
        if (client.getPhones() != null) {
            phoneResponses = client.getPhones().stream()
                    .map(this::phoneToPhoneResponse)
                    .toList();
        }

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
                .id(address.getClientId())
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
