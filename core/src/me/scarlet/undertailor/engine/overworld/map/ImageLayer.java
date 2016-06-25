package me.scarlet.undertailor.engine.overworld.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

public class ImageLayer implements Layerable, Renderable, Positionable, Identifiable {

    short id;
    short layer;
    Texture image;
    float threshold;
    Vector2 position;
    Vector2 proxyPosition;
    boolean layerSet;
    MultiRenderer renderer;

    ImageLayer() {
        this.threshold = 0F;
        this.layerSet = false;
        this.position = new Vector2(0, 0);
        this.proxyPosition = new Vector2(0, 0);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public short getLayer() {
        return this.layer;
    }

    @Override
    public void setLayer(short layer) {}

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void setHeight(float height) {}

    @Override
    public Vector2 getPosition() {
        this.proxyPosition.set(this.position.x, this.position.y);
        this.proxyPosition.y +=
            (this.image.getHeight() * threshold);
        return this.proxyPosition;
    }

    @Override
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    // Ignores position.
    // Ignores transform.
    // Intended to only render on the overworld.
    @Override
    public void draw(float x, float y, Transform transform) {
        float ptm = OverworldController.PIXELS_TO_METERS;
        renderer.getSpriteBatch().draw(image, this.position.x * ptm, this.position.y * ptm,
            this.image.getWidth() * ptm, this.image.getHeight() * ptm);
    }
}
