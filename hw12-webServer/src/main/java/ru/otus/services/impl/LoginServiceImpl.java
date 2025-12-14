package ru.otus.services.impl;

import java.util.List;
import java.util.Optional;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Password;
import ru.otus.dao.ClientDao;
import ru.otus.model.entity.Client;

public class LoginServiceImpl extends AbstractLoginService {

    private final ClientDao clientDao;

    public LoginServiceImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    protected List<RolePrincipal> loadRoleInfo(UserPrincipal userPrincipal) {
        String role = clientDao
                .findByLogin(userPrincipal.getName())
                .map(Client::getRole)
                .orElse(null);
        return List.of(new RolePrincipal(role));
    }

    @Override
    protected UserPrincipal loadUserInfo(String login) {
        Optional<Client> optionalClient = clientDao.findByLogin(login);
        return optionalClient
                .map(client -> new UserPrincipal(client.getLogin(), new Password(client.getPassword())))
                .orElse(null);
    }
}
