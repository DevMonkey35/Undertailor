package me.scarlet.undertailor.util;

public interface ProcessTask<T> {
    
    public boolean run(float delta, T param);
    
}
