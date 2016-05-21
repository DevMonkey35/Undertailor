package me.scarlet.undertailor.exception;

public class BadAssetException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6802714225399589649L;
    
    public BadAssetException() {}
    
    public BadAssetException(String message) {
        super(message);
    }
}
