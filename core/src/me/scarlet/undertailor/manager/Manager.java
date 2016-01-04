package me.scarlet.undertailor.manager;

import java.io.File;

public abstract class Manager<T> {
    
    public abstract void loadObjects(File dir);
    public abstract T getRoomObject(String name);
    
}
