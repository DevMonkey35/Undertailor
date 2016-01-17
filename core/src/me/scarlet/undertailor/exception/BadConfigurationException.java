package me.scarlet.undertailor.exception;

public class BadConfigurationException extends ConfigurationException {
    
    private static final long serialVersionUID = -5555529653460532016L;
    
    public BadConfigurationException(String message) {
        super(message);
    }
}
