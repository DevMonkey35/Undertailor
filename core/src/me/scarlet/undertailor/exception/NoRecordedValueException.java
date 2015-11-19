package me.scarlet.undertailor.exception;

public class NoRecordedValueException extends ConfigurationException {
    private static final long serialVersionUID = 1157682385593829575L;

    public NoRecordedValueException(String message) {
        super(message);
    }
}
