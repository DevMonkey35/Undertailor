package me.scarlet.undertailor.environment.event;

public interface EventListener extends EventReceiver {
    
    void addListener(String id, EventReceiver receiver);
    EventReceiver getListener(String id);
    void removeListener(String id);
    void clearListeners();
    
}
