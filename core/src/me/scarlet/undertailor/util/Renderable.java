package me.scarlet.undertailor.util;

public interface Renderable {
    
    public default void render() {
        render(1.0F);
    }
    
    public default void render(float parentAlpha) {
        render();
    }
    
}
