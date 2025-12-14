package ru.otus;

import static ru.otus.utils.CommonConstants.HIBERNATE_CFG_FILE;
import static ru.otus.utils.CommonConstants.TEMPLATES_DIR;
import static ru.otus.utils.CommonConstants.WEB_SERVER_PORT;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.security.LoginService;
import org.hibernate.cfg.Configuration;
import ru.otus.core.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.dao.ClientDao;
import ru.otus.dao.ClientDaoPostgres;
import ru.otus.model.entity.Address;
import ru.otus.model.entity.Client;
import ru.otus.model.entity.Phone;
import ru.otus.server.WebServer;
import ru.otus.server.WebServerWithBasicSecurity;
import ru.otus.services.ClientService;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.impl.ClientServiceImpl;
import ru.otus.services.impl.LoginServiceImpl;
import ru.otus.services.impl.TemplateProcessorImpl;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница клиентов
    http://localhost:8080/clients

    // REST сервис
    http://localhost:8080/api/client/
*/
public class WebServerWithBasicSecurityDemo {

    public static void main(String[] args) throws Exception {
        ClientDao clientDao = getClientDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        ClientService clientService = new ClientServiceImpl(clientDao, gson);
        LoginService loginService = new LoginServiceImpl(clientDao);

        WebServer webServer =
                new WebServerWithBasicSecurity(WEB_SERVER_PORT, loginService, clientService, gson, templateProcessor);

        webServer.start();
        webServer.join();
    }

    private static ClientDao getClientDao() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        return new ClientDaoPostgres(transactionManager, clientTemplate);
    }
}
