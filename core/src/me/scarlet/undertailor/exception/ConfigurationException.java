package me.scarlet.undertailor.exception;

public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = -2576303237596833210L;

    public ConfigurationException(String message) {
        super(message);
    }
}
