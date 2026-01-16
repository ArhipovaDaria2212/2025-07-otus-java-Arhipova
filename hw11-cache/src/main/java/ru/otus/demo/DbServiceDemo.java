package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.CacheImpl;
import ru.otus.cache.Listener;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        long timeWithoutCache = 0;
        long timeWithCache = 0;
        {
            var dbServiceClient = getDbServiceClient(false);
            List<Long> ids = saveClients(dbServiceClient);

            long start = System.currentTimeMillis();
            log.info("Save and get clients from db without cache");
            getClients(ids, dbServiceClient);
            long end = System.currentTimeMillis();

            timeWithoutCache = end - start;
        }
        {
            var dbServiceClient = getDbServiceClient(true);
            List<Long> ids = saveClients(dbServiceClient);

            long start = System.currentTimeMillis();
            log.info("Save and get clients from db with cache");
            getClients(ids, dbServiceClient);
            long end = System.currentTimeMillis();

            timeWithCache = end - start;
        }

        log.info("---------------");
        log.info("Time without cache: {} ms", timeWithoutCache);
        log.info("Time with cache: {} ms", timeWithCache);
    }

    private static List<Long> saveClients(DbServiceClientImpl dbServiceClient) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long id = dbServiceClient
                    .saveClient(new Client("dbService" + i, new Address("Street"), List.of(new Phone("88005553535"))))
                    .getId();
            ids.add(id);
        }

        return ids;
    }

    private static void getClients(List<Long> ids, DbServiceClientImpl dbServiceClient) {
        for (int i = 0; i < 5; i++) {
            ids.forEach(id -> dbServiceClient
                    .getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + id)));
        }
    }

    private static DbServiceClientImpl getDbServiceClient(boolean needCache) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        if (needCache) {
            Listener<String, Client> listener =
                    (key, value, action) -> log.info("key:{}, value:{}, action: {}", key, value, action);
            var cache = new CacheImpl<String, Client>();
            cache.addListener(listener);
            return new DbServiceClientImpl(transactionManager, clientTemplate, cache);
        }

        return new DbServiceClientImpl(transactionManager, clientTemplate);
    }
}
