package me.scarlet.undertailor.ui.event;

public class UIKeyPressEvent implements UIEvent {

    public static final String NAME = "uikeypress";
    
    private int keyId;
    public UIKeyPressEvent(int keyId) {
        this.keyId = keyId;
    }
    
    @Override
    public String getName() {
        return UIKeyPressEvent.NAME;
    }
    
    public int getPressedKey() {
        return keyId;
    }
}
