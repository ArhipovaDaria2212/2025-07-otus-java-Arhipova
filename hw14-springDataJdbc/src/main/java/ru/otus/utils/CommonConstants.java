package ru.otus.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConstants {
    public static final String API_BASE_URL = "/api/client/*";
    public static final String PAGE_BASE_URL = "/clients";
    public static final String START_PAGE_NAME = "index.html";
    public static final String COMMON_RESOURCES_DIR = "static";
    public static final int WEB_SERVER_PORT = 8080;
    public static final String TEMPLATES_DIR = "/templates/";
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
}
