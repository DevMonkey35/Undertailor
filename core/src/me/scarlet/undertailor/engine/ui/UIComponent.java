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

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.events.EventHelper;
import me.scarlet.undertailor.engine.events.EventListener;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * Child object of a {@link UIObject}, of which handles most
 * of the UI busywork.
 */
public abstract class UIComponent implements Positionable, Renderable, Processable, EventListener,
    Modular<UIObject>, Destructible {

    private boolean destroyed;
    private EventHelper events;
    private Transform transform;
    private Vector2 screenProxy;
    private Vector2 position;
    private UIObject parent;

    protected UIComponent() {
        this.screenProxy = new Vector2();
        this.transform = new Transform();
        this.position = new Vector2(0, 0);
        this.events = new EventHelper();
        this.destroyed = false;
        this.parent = null;
    }

    @Override
    public EventHelper getEventHelper() {
        return this.events;
    }

    @Override
    public boolean callEvent(Event event) {
        if(this.destroyed) {
            return false;
        }

        return this.events.processEvent(event);
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
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public void setTransform(Transform transform) {
        Transform.setOrDefault(this.transform, transform);
    }

    @Override
    public boolean claim(UIObject parent) {
        if(this.parent == null) {
            this.parent = parent;
            this.onClaim(parent);
            return true;
        }

        return false;
    }

    @Override
    public boolean release(UIObject parent) {
        if(this.parent == parent) {
            this.parent = null;
            this.destroy();
            return true;
        }

        return false;
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

        this.destroyed = true;
    }

    @Override
    public final boolean process(Object... params) {
        if (!this.destroyed) {
            return this.processComponent(params);
        }

        return false;
    }

    // ---------------- abstract definition ----------------

    /**
     * Executes the routine processing of this
     * {@link UIComponent}.
     * 
     * @param params generic parameters
     * 
     * @return generic return value
     */
    protected abstract boolean processComponent(Object... params);

    /**
     * Called when this {@link UIComponent} gets registered
     * into a {@link UIObject}.
     * 
     * @param parent the parent UIObject
     */
    public abstract void onClaim(UIObject parent);

    @Override
    public abstract void render(float x, float y, Transform transform);

    // ---------------- object ----------------

    /**
     * Returns the parent {@link UIObject} of this
     * {@link UIComponent}.
     * 
     * @return the parent UIObject
     */
    public UIObject getParent() {
        return this.parent;
    }

    /**
     * Returns the real screen position of this
     * {@link UIComponent}.
     * 
     * @return the screen position of this UIComponent
     */
    public Vector2 getScreenPosition() {
        if (this.parent == null) {
            return this.position;
        }

        this.screenProxy.set(this.parent.getPosition());
        this.screenProxy.add(this.position);
        return this.screenProxy;
    }

    // ---------------- don't care about these ----------------

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void setHeight(float height) {}
}
