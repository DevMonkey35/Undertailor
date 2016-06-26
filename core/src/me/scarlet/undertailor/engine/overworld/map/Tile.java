package me.scarlet.undertailor.engine.overworld.map;

import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

public class Tile implements Renderable {

    int width;
    int height;

    Renderable renderable;

    Tile() {}

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public Transform getTransform() {
        return this.renderable.getTransform();
    }

    @Override
    public void setTransform(Transform transform) {
        this.renderable.setTransform(transform);
    }

    @Override
    public void draw(float x, float y, Transform transform) {
        this.renderable.draw(x, y, transform);
    }
}
