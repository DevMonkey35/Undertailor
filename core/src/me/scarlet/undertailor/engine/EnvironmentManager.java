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

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.scarlet.undertailor.LaunchOptions.ViewportType;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.events.EventHelper;
import me.scarlet.undertailor.engine.events.EventListener;
import me.scarlet.undertailor.engine.scheduler.Scheduler;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * Manager class for {@link Environment} instances.
 */
public class EnvironmentManager implements EventListener, Processable, Renderable {

    private Undertailor tailor;
    private EventHelper events;
    private Scheduler globalSched;
    private String activeEnvironment;
    private ObjectMap<String, Environment> environments;
    private Class<? extends Viewport> viewportType;

    public EnvironmentManager(Undertailor tailor) {
        this.tailor = tailor;
        this.activeEnvironment = null;
        this.events = new EventHelper();
        this.environments = new ObjectMap<>();
        this.globalSched = new Scheduler(null);

        this.viewportType = tailor.getLaunchOptions().scaling == ViewportType.FIT
            ? FitViewport.class : StretchViewport.class;
    }

    // ---------------- abstract method implementation ----------------

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    @Override
    public EventHelper getEventHelper() {
        return this.events;
    }

    @Override
    public boolean callEvent(Event event) {
        boolean processed = false;
        if (events.processEvent(event)) {
            processed = true;
        }

        // make sure the active environment gets the event first
        if (this.getActiveEnvironment() != null) {
            return this.getActiveEnvironment().callEvent(event);
        }

        for (Environment env : this.environments.values()) {
            if (!env.equals(this.getActiveEnvironment()) && env.callEvent(event)) {
                processed = true;
            }
        }

        return processed;
    }

    @Override
    public boolean process() {
        this.globalSched.process();
        Environment active = this.getActiveEnvironment();
        if (active != null) {
            return active.process();
        }

        return false;
    }

    // Ignores provided positions.
    // Ignores transform.
    @Override
    public void render(float x, float y, Transform transform) {
        Environment active = this.getActiveEnvironment();
        if (active != null) {
            active.render();
        }
    }

    // ---------------- object ----------------

    /**
     * Returns the global {@link Scheduler}.
     * 
     * @return the global Scheduler
     */
    public Scheduler getGlobalScheduler() {
        return this.globalSched;
    }

    /**
     * Returns the {@link Environment} of the given name,
     * creating it if it has yet to exist.
     * 
     * @param name the name of the Environment
     * 
     * @return the Environment under the given name, a new
     *         one if it didn't exist prior
     */
    public Environment getEnvironment(String name) {
        if (!this.environments.containsKey(name)) {
            this.environments.put(name, new Environment(tailor, name));
            this.environments.get(name).setViewport(this.viewportType);
        }

        return this.environments.get(name);
    }

    /**
     * Destroys the provided {@link Environment}.
     * 
     * @param environment the environment to destroy
     */
    public void destroyEnvironment(Environment environment) {
        environment.destroy();
        this.environments.remove(environment.getName());
    }

    /**
     * Destroys the {@link Environment} of the given name.
     * 
     * @param name the name of the environment
     */
    public void destroyEnvironment(String name) {
        if (this.environments.containsKey(name)) {
            this.environments.get(name).destroy();
            this.environments.remove(name);
        }
    }

    /**
     * Returns the current active {@link Environment}.
     * 
     * @return the curret active Environment
     */
    public Environment getActiveEnvironment() {
        Environment environment = this.environments.get(this.activeEnvironment);
        if (environment != null) {
            if (environment.isDestroyed()) {
                this.environments.remove(activeEnvironment);
            } else {
                return environment;
            }
        }

        return null;
    }

    /**
     * Sets the current active {@link Environment}.
     * 
     * @param envName the name of the new active Environment
     */
    public void setActiveEnvironment(String envName) {
        if (this.environments.containsKey(envName)) {
            this.setActiveEnvironment(this.environments.get(envName));
        } else {
            this.setActiveEnvironment((Environment) null);
        }
    }

    /**
     * Sets the current active {@link Environment}.
     * 
     * @param env the new active Environment
     */
    public void setActiveEnvironment(Environment env) {
        this.activeEnvironment = null;

        for (ObjectMap.Entry<String, Environment> entry : this.environments.entries()) {
            if (entry.value.equals(env)) {
                this.activeEnvironment = entry.key;
                break;
            }
        }
    }

    // ---------------- internal ----------------

    /**
     * Internal method.
     * 
     * <p>Although public, should only be called by the
     * system.</p>
     */
    public void resize(int width, int height) {
        this.environments.values().forEach(env -> env.resize(width, height));
    }
}
