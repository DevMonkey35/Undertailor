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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.Subsystem;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * Subsystem within an {@link Environment} running the
 * processes of an overworld.
 * 
 * <p>Positional values within the OverworldController
 * internally differ to that of the pixel-based positions.
 * All objects relevant to the Overworld will always convert
 * to the internal measure of values to keep the physics
 * engine from going insane.</p>
 */
public class OverworldController implements Processable, Renderable, Subsystem {

    public static final float PIXELS_TO_METERS = 0.025F;
    public static final float METERS_TO_PIXELS = 40.0F;
    public static final float RENDER_WIDTH = 640.0F;
    public static final float RENDER_HEIGHT = 480.0F;

    // generic
    private MultiRenderer renderer;
    private Environment environment;
    private OverworldCamera camera;
    private Viewport viewport;

    // overworld-related
    private CollisionHandler collision;
    private WorldObject character;
    private WorldRoom room;

    public OverworldController(MultiRenderer renderer, Environment environment, Viewport viewport) {
        this.collision = new CollisionHandler(renderer, true);
        this.camera = new OverworldCamera();
        this.environment = environment;
        this.renderer = renderer;
        this.character = null;

        this.setViewport(viewport);
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {} // Cannot transform the rendering of the Overworld.

    @Override
    public void draw(float x, float y, Transform transform) {
        if (this.room != null) {
            this.renderer.setBatchProjectionMatrix(this.camera.combined);
            this.room.draw();
        }
    }

    @Override
    public boolean process(Object... params) {
        this.collision.step(Gdx.graphics.getRawDeltaTime());
        if (this.room != null) {
            this.room.process(params);
        }

        return true;
    }

    // ---------------- object methods ----------------
    /**
     * Returns the {@link OverworldCamera} controlling the
     * viewpoint of the overworld.
     * 
     * @return this {@link OverworldController}'s camera
     */
    public OverworldCamera getCamera() {
        return this.camera;
    }

    /**
     * Returns whether or not the provided
     * {@link WorldObject} is currently the "character"
     * object.
     * 
     * @param obj the object to query
     * 
     * @return if obj is the character object
     */
    public boolean isCharacter(WorldObject obj) {
        if (this.character != null) {
            return obj.getId() == this.character.getId();
        }

        return false;
    }

    /**
     * Sets the provided {@link WorldObject} as the
     * "character" object.
     * 
     * @param obj the object to deem as the character
     */
    public void setCharacter(WorldObject obj) {
        this.character = obj;
    }

    /**
     * Returns the current {@link WorldRoom} held and
     * processed by this {@link OverworldController}.
     * 
     * @return the Overworld's current room
     */
    public WorldRoom getRoom() {
        return this.room;
    }

    /**
     * Sets the provided {@link WorldRoom} as this
     * {@link OverworldController}'s room.
     * 
     * @param room the new room for the Overworld to use
     */
    public void setRoom(WorldRoom room) {
        if (this.room != null) {
            this.room.release(this);
            this.room = null;
        }

        if (room.claim(this)) {
            this.room = room;
        }
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Returns the collision handler.</p>
     */
    CollisionHandler getCollisionHandler() {
        return this.collision;
    }

    /**
     * Internal method.
     * 
     * <p>While public, should only be called by a parent
     * {@link Environment} who also had their viewport
     * changed.</p>
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
        this.viewport.setWorldSize(RENDER_WIDTH, RENDER_HEIGHT);
        this.viewport.setCamera(this.camera);
        this.camera.update();
    }

    /**
     * Internal method.
     * 
     * <p>While public, should only be called by a parent
     * {@link Environment} who also had their resize method
     * called.</p>
     */
    public void resize(int width, int height) {
        this.viewport.update(width, height, false);
    }
}
