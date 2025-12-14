package ru.otus.server;

import static ru.otus.model.enums.ClientRole.ADMIN;
import static ru.otus.utils.CommonConstants.API_BASE_URL;
import static ru.otus.utils.CommonConstants.COMMON_RESOURCES_DIR;
import static ru.otus.utils.CommonConstants.PAGE_BASE_URL;
import static ru.otus.utils.CommonConstants.START_PAGE_NAME;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import ru.otus.helpers.FileSystemHelper;
import ru.otus.services.ClientService;
import ru.otus.services.TemplateProcessor;
import ru.otus.servlet.ClientApiServlet;
import ru.otus.servlet.ClientServlet;

public class WebServerWithBasicSecurity implements WebServer {
    protected final TemplateProcessor templateProcessor;
    private final LoginService loginService;
    private final ClientService clientService;
    private final Server server;
    private final Gson gson;

    public WebServerWithBasicSecurity(
            int port,
            LoginService loginService,
            ClientService clientService,
            Gson gson,
            TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
        this.loginService = loginService;
        this.clientService = clientService;
        this.gson = gson;
        server = new Server(port);
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    private void initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        Handler.Sequence sequence = new Handler.Sequence();
        sequence.addHandler(resourceHandler);
        sequence.addHandler(applySecurity(servletContextHandler, PAGE_BASE_URL));

        server.setHandler(sequence);
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles(START_PAGE_NAME);
        resourceHandler.setBaseResourceAsString(
                FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(
                new ServletHolder(new ClientServlet(templateProcessor, clientService)), PAGE_BASE_URL);
        servletContextHandler.addServlet(new ServletHolder(new ClientApiServlet(clientService, gson)), API_BASE_URL);
        return servletContextHandler;
    }

    private Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        Constraint constraint = Constraint.from(ADMIN.getName());

        List<ConstraintMapping> constraintMappings = new ArrayList<>();
        Arrays.stream(paths).forEachOrdered(path -> {
            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec(path);
            mapping.setConstraint(constraint);
            constraintMappings.add(mapping);
        });

        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        // как декодировать стороку с юзером:паролем https://www.base64decode.org/
        security.setAuthenticator(new BasicAuthenticator());

        security.setLoginService(loginService);
        security.setConstraintMappings(constraintMappings);
        security.setHandler(new Handler.Wrapper(servletContextHandler));

        return security;
    }
}
