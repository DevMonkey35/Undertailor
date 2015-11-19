package me.scarlet.undertailor.ui.event;

public class UIKeyReleaseEvent implements UIEvent {

    public static final String NAME = "uikeyrelease";
    
    private int keyId;
    public UIKeyReleaseEvent(int keyId) {
        this.keyId = keyId;
    }
    
    @Override
    public String getName() {
        return UIKeyPressEvent.NAME;
    }
    
    public int getReleasedKey() {
        return keyId;
    }
}
