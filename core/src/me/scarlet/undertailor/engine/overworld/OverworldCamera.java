package me.scarlet.undertailor.engine.overworld;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class OverworldCamera extends OrthographicCamera {

    private float zoom;
    private Vector2 position;

    public OverworldCamera() {
        super(OverworldController.RENDER_WIDTH, OverworldController.RENDER_HEIGHT);
        this.position = new Vector2(0, 0);
        this.setPosition(0, 0); // ensure internal camera
        this.setZoom(2F);
    }

    /**
     * Returns the position of this {@link OverworldCamera}.
     * The position originates at the center of the camera.
     * 
     * <p>Directly modifying the returned {@link Vector2}
     * object will not change the camera position, as the
     * provided Vector2 object is a proxy representing the
     * pixel-based position of the camera.</p>
     * 
     * @return the position of this OverworldCamera
     */
    public Vector2 getPosition() {
        return this.position;
    }

    /**
     * Sets the position of this {@link OverworldCamera}.
     * 
     * @param x the x coordinate of the center of the camera
     * @param y the y coordinate of the center of the camera
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        super.position.set(x * OverworldController.PIXELS_TO_METERS,
            y * OverworldController.PIXELS_TO_METERS, 0);
        super.update();
    }

    /**
     * Returns the zoom level of this
     * {@link OverworldCamera}.
     * 
     * <p>Zoom level represents the scale the overworld is
     * rendered at. By default, the zoom level is 2.</p>
     * 
     * @return the zoom level of this camera
     */
    public float getZoom() {
        return this.zoom;
    }

    /**
     * Sets the zoom level of this {@link OverworldCamera}.
     * 
     * @param zoom the new zoom level
     */
    public void setZoom(float zoom) {
        super.zoom = OverworldController.PIXELS_TO_METERS / zoom;
        super.update();
        this.zoom = zoom;
    }
}
