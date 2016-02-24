/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.scarlet.undertailor.environment.ui;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.environment.event.EventData;
import me.scarlet.undertailor.environment.event.EventReceiver;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.Positionable;
import me.scarlet.undertailor.util.Renderable;

/**
 * A component of a {@link UIObject}, which may display something on-screen.
 */
public abstract class UIComponent implements Renderable, Positionable, EventReceiver {
    
    /** Holding the parent {@link UIObject} that owns this UIComponent. */
    protected UIObject parent;
    /**
     * Holding the current position of this UIComponent within the positional
     * system of the parent {@link UIObject}.
     */
    private Vector2 position;
    private boolean destroying;
    private boolean isAlwaysActive;
    private boolean renderWhenInactive;
    
    public UIComponent() {
        this(new Vector2(0, 0));
    }
    
    public UIComponent(Vector2 position) {
        this(position, false);
    }
    
    public UIComponent(Vector2 position, boolean isAlwaysActive) {
        this(position, isAlwaysActive, true);
    }
    
    public UIComponent(Vector2 position, boolean isAlwaysActive, boolean renderWhenInactive) {
        this.position = position;
        this.isAlwaysActive = isAlwaysActive;
        this.renderWhenInactive = renderWhenInactive;
        this.destroying = false;
    }
    
    /**
     * Returns the parent {@link UIObject} that owns this {@link UIComponent}.
     * 
     * <p>Applying changes to an unregistered UIObject will not have any adverse
     * effects outside of simply not being cared about by the system.</p>
     */
    public UIObject getParent() {
        return parent;
    }
    
    /**
     * Returns the current position of this {@link UIComponent} within the
     * positional system of its parent {@link UIObject}.
     */
    public Vector2 getPosition() {
        return position;
    }
    
    /**
     * Returns the current position of this {@link UIComponent} within the real
     * positional system used to display any UI objects.
     * 
     * <p>This is simply returned by adding the position of this UIComponent to
     * the position of its parent UIObject. If a parent object is not present,
     * this functions similarly to {@link UIComponent#getPosition()}.</p>
     */
    public Vector2 getRealPosition() {
        if(parent == null) {
            return getPosition();
        } else {
            return new Vector2(parent.getPosition()).add(position);
        }
    }
    
    /**
     * Sets the position of this {@link UIComponent} within the positional
     * system of its parent {@link UIObject}.
     * 
     * @param position the new position for this UIComponent to reside at
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }
    
    /**
     * Returns whether or not this {@link UIComponent} is always active when it
     * exists, regardless of whether or not it is on the top of the processing
     * queue.
     * 
     * <p>Returning false means this UIComponent is only active when its parent
     * {@link UIObject} is ready to process it.</p>
     */
    public boolean isAlwaysActive() {
        return this.isAlwaysActive;
    }
    
    public void setAlwaysActive(boolean flag) {
        this.isAlwaysActive = flag;
    }
    
    public boolean renderWhenInactive() {
        return this.renderWhenInactive;
    }
    
    public void setRenderWhenInactive(boolean flag) {
        this.renderWhenInactive = flag;
    }
    
    public void destroy() {
        if(destroying) {
            throw new IllegalStateException("Cannot call destroy within onDestroy");
        }
        
        this.destroying = true;
        if(this.parent != null) {
            parent.destroyChild(this);
        }
        
        this.destroying = false;
    }
    
    public void destroyObject() {
        if(this.parent != null) {
            this.parent.destroy();
        }
    }
    
    public void onDestroy(boolean object) {}
    
    /**
     * Called once every frame, if this {@link UIComponent} is considered
     * active.
     * 
     * <p>This method is meant to be implemented by any UIComponent that wishes
     * to perform action any time it is active on screen. This may be useful for
     * any UIComponent that requires constant knowledge of its environment, for
     * example if a UIComponent requires knowledge of current input by the
     * player in order to process one of its child internal components.</p>
     * 
     * @param delta the time, in seconds, since the last frame
     */
    public void process(float delta, InputData input) {}
    
    @Override
    public void pushEvent(EventData data) {}
    
    /**
     * Renders this {@link UIComponent}.
     * 
     * @param parentAlpha the alpha of the parent UIObject, which should be
     *            multiplied by this object's current alpha
     */
    public void render(float parentAlpha) {}
    
    public abstract String getComponentTypeName();
}
