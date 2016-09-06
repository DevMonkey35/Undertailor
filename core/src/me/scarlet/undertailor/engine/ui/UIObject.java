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

package me.scarlet.undertailor.engine.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterable;
import com.badlogic.gdx.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.events.EventHelper;
import me.scarlet.undertailor.engine.events.EventListener;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

import java.util.Iterator;

/**
 * Container class for a set of {@link UIComponent}s working
 * together.
 */
public class UIObject implements Identifiable, Processable, Renderable, EventListener, Positionable,
    Destructible, Modular<UIController> {

    static long objId;
    static final Logger log = LoggerFactory.getLogger(UIObject.class);

    static {
        objId = 0;
    }

    private long id;
    private long lifetime;
    private long lifestart;
    private boolean active;
    private Vector2 position;
    private boolean destroyed;
    private EventHelper events;
    private UIController parent;
    private Array<UIComponent> components;
    // we're nested under UIController's iterators, so we need this
    private Array.ArrayIterable<UIComponent> compIterator;

    public UIObject(boolean active) {
        this.id = objId++;
        this.lifetime = -1;
        this.lifestart = -1;
        this.active = active;

        this.parent = null;
        this.destroyed = false;
        this.events = new EventHelper(this);
        this.position = new Vector2(0, 0);
        this.components = new Array<>(true, 8);
        this.compIterator = new ArrayIterable<>(this.components, true);
    }

    @Override
    public EventHelper getEventHelper() {
        return this.events;
    }

    @Override
    public boolean callEvent(Event event) {
        if (this.destroyed) {
            return false;
        }

        boolean processed = false;
        if (this.events.processEvent(event)) {
            processed = true;
        }

        for (UIComponent comp : this.components) {
            if (comp.callEvent(event)) {
                processed = true;
            }
        }

        return processed;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Vector2 getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void destroy() {
        if (this.destroyed) {
            return;
        }

        this.parent = null;
        this.destroyed = true;
        this.components.forEach(comp -> comp.release(this));
        this.components.clear();
        this.components = null;
    }

    @Override
    public UIController getParent() {
        return this.parent;
    }

    @Override
    public boolean claim(UIController parent) {
        if (this.parent == null) {
            this.parent = parent;
            this.callEvent(new Event(Event.EVT_CLAIM));
            return true;
        }

        return false;
    }

    @Override
    public boolean release(UIController parent) {
        if (this.parent == parent) {
            this.parent = null;
            return true;
        }

        return false;
    }

    // Ignores provided position.
    // Ignores own transform.
    @Override
    public void render(float x, float y, Transform transform) {
        if (this.destroyed) {
            return;
        }

        this.components.forEach(comp -> {
            comp.render(comp.getScreenPosition());
        });
    }

    @Override
    public boolean process() {
        if (this.destroyed) {
            return false;
        }

        if (this.lifestart < 0) {
            this.lifestart = TimeUtils.millis();
        }

        if (this.isPastLifetime()) {
            this.destroy();
            return false;
        }

        Iterator<UIComponent> iter = this.compIterator.iterator();
        while (iter.hasNext()) {
            UIComponent next = iter.next();
            if (next.isDestroyed() || next.getParent() != this) {
                iter.remove();
            } else {
                next.process();
            }
        }

        return false;
    }

    // ---------------- object ----------------

    /**
     * Returns whether or not this {@link UIObject} is
     * considered "active."
     * 
     * @return if this UIObject is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Returns the maximum lifetime, in milliseconds for
     * this {@link UIObject} before it gets automagically
     * destroyed. A value <=0 means it will live until its
     * {@link #destroy()} method is called.
     * 
     * @return the maximum lifetime for this UIObject
     */
    public long getMaxLifetime() {
        if (this.lifetime < 0) {
            return 0;
        }

        return this.lifetime;
    }

    /**
     * Sets the maximum lifetime, in milliseconds, for this
     * {@link UIObject}, or <=0 for an infinite lifetime.
     * 
     * @param lifetime the new max lifetime for this
     *        UIObject
     */
    public void setMaxLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    /**
     * Returns whether or not this {@link UIObject} is past
     * its maximum lifetime, as defined by
     * {@link #getMaxLifetime()}.
     * 
     * @return if this UIObject is past its max lifetime
     */
    public boolean isPastLifetime() {
        if (this.lifetime <= 0) {
            return false;
        }

        if (this.lifestart <= 0) {
            return false;
        }

        return TimeUtils.timeSinceMillis(this.lifestart) > this.lifetime;
    }

    /**
     * Returns the length of time since this
     * {@link UIObject} has been registered.
     * 
     * <p>Note that this value may not always represent the
     * actual registration time, as anything may call
     * {@link #resetLifetime()} to reset this value.</p>
     * 
     * @return the current lifetime of this UIObject
     */
    public long getLifetime() {
        if (this.lifestart < 0) {
            return 0;
        }

        return TimeUtils.timeSinceMillis(this.lifestart);
    }

    /**
     * Resets this {@link UIObject}'s lifetime tracker.
     */
    public void resetLifetime() {
        this.lifestart = -1;
    }

    /**
     * Registers a {@link UIComponent} to be processed by
     * this {@link UIObject}.
     * 
     * @param component a UIComponent to process with this
     *        UIObject
     */
    public void registerComponent(UIComponent component) {
        if (this.destroyed) {
            return;
        }

        if (component.isDestroyed()) {
            log.warn(
                "attempted to provide destroyed component to uiobject; cannot accept destroyed uiobject");
            return;
        }

        if (component.claim(this)) {
            this.components.add(component);
        }
    }

    // ---------------- don't care about these ----------------

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void setHeight(float height) {}

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}
}
