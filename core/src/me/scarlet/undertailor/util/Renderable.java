package me.scarlet.undertailor.util;

public interface Renderable {
    
    default void render() {
        render(1.0F);
    }
    
    default void render(float parentAlpha) {
        render();
    }
    
}
