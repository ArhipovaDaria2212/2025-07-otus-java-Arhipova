package ru.arhipova;

import ru.arhipova.annotations.Log;

@SuppressWarnings("java:S106")
public class TestLoggingImpl implements TestLogging {
    private static final String LOG_MESSAGE = "The real method was called with the parameters: ";

    @Log
    public void calculation(int iParam) {
        System.out.println(LOG_MESSAGE + iParam);
    }

    @Log
    public void calculation(int iParam, long lParam) {
        System.out.println(LOG_MESSAGE + iParam + " " + lParam);
    }

    public void calculation(int iParam, long lParam, String sParam) {
        System.out.println(LOG_MESSAGE + iParam + " " + lParam + " " + sParam);
    }
}
