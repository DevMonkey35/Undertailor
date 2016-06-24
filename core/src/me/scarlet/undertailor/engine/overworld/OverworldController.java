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

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.Subsystem;
import me.scarlet.undertailor.engine.scheduler.Scheduler;
import me.scarlet.undertailor.engine.scheduler.Task;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.util.Pair;

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
public class OverworldController implements Processable, Renderable, Subsystem, Destructible {

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
    private boolean playTransitions;
    private Pair<Task> transitions;
    private WorldObject character;
    private WorldRoom room;

    public OverworldController(MultiRenderer renderer, Environment environment, Viewport viewport) {
        this.collision = new CollisionHandler(renderer, true);
        this.camera = new OverworldCamera(this);
        this.transitions = new Pair<>();
        this.environment = environment;
        this.playTransitions = true;
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
        this.renderer.setBatchProjectionMatrix(this.camera.combined);

        if (this.room != null) {
            this.room.draw();
        }

        if (Undertailor.isDebug()) {
            this.renderer.flush();
            // have to flush before rendering collisions,
            // otherwise the b2d debug renderer calls begin()
            // inside another begin() call
            this.getCollisionHandler().render();
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

    @Override
    public void destroy() {
        if (this.room != null) {
            this.room.destroy();
        }

        if (this.character != null) {
            this.character.destroy();
        }

        this.collision.destroy();
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
     * <p>Whether or not to play transitions is decided by
     * {@link #isPlayingTransitions()}.</p>
     * 
     * @param room the new room for the Overworld to use
     */
    public void setRoom(WorldRoom room) {
        this.setRoom(room, this.playTransitions);
    }


    /**
     * Sets the provided {@link WorldRoom} as this
     * {@link OverworldController}'s room.
     * 
     * <p>It is recommended to always play at least an exit
     * transition to hide the previous room from view,
     * otherwise the game may look as if suspended in limbo
     * during room loading.</p>
     * 
     * @param room the new room for the Overworld to use
     * @param transitions whether or not to play transitions
     */
    public void setRoom(WorldRoom room, boolean transitions) {
        Task roomTask = (params) -> {
            if (this.room != null) {
                this.room.release(this);
                this.room = null;
            }

            if (room.claim(this)) {
                this.room = room;
            }

            // update the camera since the room has changed
            this.camera.fixPosition();
            return false;
        };

        Scheduler sched = this.environment.getScheduler();
        if (transitions) {
            sched.registerTask(this.transitions.getB(), true); // exit
            sched.registerTask(roomTask, true);
            sched.registerTask(this.transitions.getA(), true); // entry
        } else {
            sched.registerTask(roomTask, true);
        }
    }

    /**
     * Returns whether or not this
     * {@link OverworldController} will play room
     * transitions, by default.
     * 
     * @return if this Overworld will play room transitions
     *         by default
     */
    public boolean isPlayingTransitions() {
        return this.playTransitions;
    }

    /**
     * Sets whether or not this {@link OverworldController}
     * will play room transitions, by default.
     * 
     * @param playingTransitions if this Overworld should
     *        play room transitions by default
     */
    public void setPlayingTransitions(boolean playingTransitions) {
        this.playTransitions = playingTransitions;
    }

    /**
     * Sets the {@link Task} played upon entering any room.
     * 
     * @param transitionTask the entry transition
     */
    public void setEntryTransition(Task transitionTask) {
        this.transitions.setA(transitionTask);
    }

    /**
     * Sets the {@link Task} played upon exiting any room.
     * 
     * @param transitionTask the exit transition
     */
    public void setExitTransition(Task transitionTask) {
        this.transitions.setB(transitionTask);
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
