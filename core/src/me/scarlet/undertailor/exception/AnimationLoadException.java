package me.scarlet.undertailor.exception;

public class AnimationLoadException extends RuntimeException {

    private static final long serialVersionUID = 7681169246237558135L;
    
    public AnimationLoadException(String message) {
        super(message);
    }
}
