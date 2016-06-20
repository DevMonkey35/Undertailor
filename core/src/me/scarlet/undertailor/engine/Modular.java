package me.scarlet.undertailor.engine;

public interface Modular<T> {

    boolean claim(T parent);

    boolean release(T parent);

}
