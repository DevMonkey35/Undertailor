package me.scarlet.undertailor.util;

public class Wrapper<T> {

    private T obj;

    public Wrapper() {
        this.obj = null;
    }

    public Wrapper(T obj) {
        this.obj = obj;
    }

    public T get() {
        return this.obj;
    }

    public void set(T obj) {
        this.obj = obj;
    }
}
