/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

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
