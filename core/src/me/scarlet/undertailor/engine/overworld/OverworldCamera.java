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

import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;

/**
 * An {@link OrthographicCamera} specifically for use by an
 * {@link OverworldController}.
 */
public class OverworldCamera extends OrthographicCamera {

    private float zoom;
    private boolean fixing;
    private Vector2 offset;
    private Vector2 position;
    private OverworldController parent;

    public OverworldCamera(OverworldController parent) {
        super(OverworldController.RENDER_WIDTH, OverworldController.RENDER_HEIGHT);
        this.fixing = true;
        this.parent = parent;
        this.offset = new Vector2(0, 0);
        this.position = new Vector2(0, 0);
        this.setPosition(0, 0); // ensure internal camera
        this.setZoom(2F);

        this.fixPosition();
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
     * The position originates at the center of the camera.
     * 
     * @param x the x coordinate of the center of the camera
     * @param y the y coordinate of the center of the camera
     */
    public void setPosition(float x, float y) {
        if (this.position.x == x && this.position.y == y) {
            return;
        }

        this.position.set(x, y);
        this.fixPosition();
        // call >this< position's params instead of method params in case fixPos updates them
        super.position.set((this.position.x + this.offset.x) * OverworldController.PIXELS_TO_METERS,
            (this.position.y + this.offset.y) * OverworldController.PIXELS_TO_METERS, 0);
        super.update();
    }

    /**
     * Returns the positional offset of this
     * {@link OverworldCamera}.
     * 
     * <p>Offset values are not considered during camera
     * fixing, making them useful for camera movement
     * effects.</p>
     */
    public Vector2 getOffset() {
        return this.offset;
    }

    /**
     * Sets the positional offset of this
     * {@link OverworldCamera}.
     * 
     * @param x the positional offset on the x axis
     * @param y the positional offset on the y axis
     */
    public void setOffset(float x, float y) {
        if (this.offset.x == x && this.offset.y == y) {
            return;
        }

        this.offset.set(x, y);
        this.setPosition(this.position.x, this.position.y);
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

    /**
     * Returns whether or not this {@link OverworldCamera}
     * will continuously fix its position so as to not show
     * the empty space beyond the current room's boundaries.
     * 
     * @return if this camera will fix its position to stay
     *         in-bounds
     */
    public boolean isFixing() {
        return this.fixing;
    }

    /**
     * Sets whether or not this {@link OverworldCamera} will
     * continuously fix its position so as to not show the
     * empty space beyond the current room's boundaries.
     * 
     * @param fixing if this camera will fix its position to
     *        stay in-bounds
     */
    public void setFixing(boolean fixing) {
        this.fixing = fixing;
        if (this.isFixing()) {
            this.fixPosition();
        }
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Handles camera "fixing."</p>
     */
    void fixPosition() {
        if (!this.isFixing() || this.parent.getRoom() == null
            || this.parent.getRoom().tilemap == null) {
            return;
        }

        Tilemap map = this.parent.getRoom().tilemap;
        float rmX = map.getOccupiedWidth();        // room's width
        float rmY = map.getOccupiedHeight();       // room's height
        float cvX = (this.viewportWidth / Math.abs(this.zoom)) / 2;
        float cvY = (this.viewportHeight / Math.abs(this.zoom)) / 2;
        float xPos = this.position.x;
        float yPos = this.position.y;

        if (cvX * 2 >= rmX) {
            xPos = rmX / 2.0F;
        } else {
            if (xPos < cvX) {
                xPos = cvX;
            } else if (xPos > rmX - cvX) {
                xPos = rmX - cvX;
            }
        }

        if (cvY * 2 >= rmY) {
            yPos = rmY / 2.0F;
        } else {
            if (yPos < cvY) {
                yPos = cvY;
            } else if (yPos > rmY - cvY) {
                yPos = rmY - cvY;
            }
        }

        this.position.set(xPos, yPos);
    }
}
