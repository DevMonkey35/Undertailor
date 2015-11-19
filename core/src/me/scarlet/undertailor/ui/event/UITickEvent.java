package me.scarlet.undertailor.ui.event;

public class UITickEvent implements UIEvent {

    public static final String NAME = "uitick";
    
    @Override
    public String getName() {
        return UITickEvent.NAME;
    }
}
