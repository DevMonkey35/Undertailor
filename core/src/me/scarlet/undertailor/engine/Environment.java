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

package me.scarlet.undertailor.engine;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.engine.scheduler.Scheduler;
import me.scarlet.undertailor.engine.ui.UIController;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * A primary system running a single game environment.
 */
public class Environment implements Processable, Renderable, Destructible {

    private UIController ui;
    private Scheduler scheduler;
    private OverworldController overworld;

    public Environment(Undertailor tailor) {
        this.scheduler = new Scheduler(this);
        this.overworld =
            new OverworldController(tailor.getRenderer(), this, new FitViewport(640, 480));
        this.ui = new UIController();
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    @Override
    public void draw(float x, float y, Transform transform) {
        this.overworld.draw();
        this.ui.draw();
    }

    @Override
    public boolean process(Object... params) {
        scheduler.process();
        ui.process();
        overworld.process();
        return true;
    }

    @Override
    public void destroy() {
        this.scheduler.destroy();
        this.overworld.destroy();
        this.ui.destroy();
    }

    // ---------------- object ----------------

    /**
     * Returns the {@link Scheduler} ran locally by this
     * {@link Environment}.
     * 
     * @return this Environment's Scheduler
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * Returns the {@link OverworldController} ran locally
     * by this {@link Environment}.
     * 
     * @return this Environment's Scheduler
     */
    public OverworldController getOverworld() {
        return this.overworld;
    }

    /**
     * Returns the {@link UIController} ran locally by this
     * {@link Environment}.
     * 
     * @return this Environment's UIController
     */
    public UIController getUI() {
        return this.ui;
    }

    // ---------------- internal ----------------

    /**
     * Internal method.
     * 
     * <p>Sets the viewport type.</p>
     */
    void setViewport(Class<? extends Viewport> viewportType) {
        Viewport newPort = null;
        if (viewportType == FitViewport.class) {
            newPort = new FitViewport(640, 480);
        } else if (viewportType == StretchViewport.class) {
            newPort = new StretchViewport(640, 480);
        }

        if (newPort != null) {
            this.overworld.setViewport(newPort);
        }
    }

    /**
     * Internal method.
     * 
     * <p>Called when the game window size changes.</p>
     */
    void resize(int width, int height) {
        this.overworld.resize(width, height);
    }
}
