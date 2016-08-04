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

import com.badlogic.gdx.graphics.OrthographicCamera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.Subsystem;
import me.scarlet.undertailor.engine.overworld.OverworldController;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * Subsystem within an {@link Environment} running the
 * processes of a user interface.
 */
public class UIController implements Processable, Renderable, Destructible, Subsystem {

    static final Logger log = LoggerFactory.getLogger(UIController.class);

    private boolean destroyed;

    private Set<Long> removed;
    private MultiRenderer renderer;
    private Environment environment;
    private OrthographicCamera camera;
    private TreeMap<Long, UIObject> aObj; // active
    private TreeMap<Long, UIObject> bObj; // background

    public UIController(Environment parent, MultiRenderer renderer) {
        this.destroyed = false;
        this.renderer = renderer;
        this.environment = parent;
        this.removed = new HashSet<>();
        this.camera = new OrthographicCamera(OverworldController.RENDER_WIDTH,
            OverworldController.RENDER_HEIGHT);
        this.camera.position.x += OverworldController.RENDER_WIDTH / 2F;
        this.camera.position.y += OverworldController.RENDER_HEIGHT / 2F;
        this.camera.update();

        this.aObj = new TreeMap<>(Long::compare);
        this.bObj = new TreeMap<>(Long::compare);
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public boolean process(Object... params) {
        if (!this.destroyed) {
            Iterator<UIObject> iter = bObj.values().iterator();
            while (iter.hasNext()) {
                UIObject next = iter.next();
                if (next.isDestroyed() || this.removed.contains(next.getId())) {
                    iter.remove();
                } else {
                    next.process(params);
                }
            }

            iter = aObj.values().iterator();
            UIObject processed = null;
            while (iter.hasNext()) {
                UIObject next = iter.next();
                if (next.isDestroyed() || this.removed.contains(next.getId())) {
                    iter.remove();
                } else {
                    processed = next;
                }
            }

            if (processed != null) {
                processed.process(params);
            }

            return true;
        }

        return false;
    }

    @Override
    public void render(float x, float y, Transform transform) {
        if (!destroyed) {
            this.renderer.setProjectionMatrix(this.camera.combined);
            this.bObj.values().forEach(UIObject::render);
            this.aObj.values().forEach(UIObject::render);
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void destroy() {
        this.destroyed = true;
    }

    // ---------------- object ----------------

    /**
     * Returns the UI object of the provided ID, if
     * registered with this {@link UIController}.
     * 
     * @param id the ID of the target UIObject
     * 
     * @return the UIObject with the assigned ID, or null if
     *         not found
     */
    public UIObject getUIObject(long id) {
        if (this.aObj.containsKey(id)) {
            return this.aObj.get(id);
        }

        if (this.bObj.containsKey(id)) {
            return this.bObj.get(id);
        }

        return null;
    }

    /**
     * Registers a {@link UIObject} with this
     * {@link UIController}.
     * 
     * @param obj the UIObject to register
     */
    public long registerUIObject(UIObject obj) {
        if (this.destroyed) {
            return -1;
        }

        if (obj.isDestroyed()) {
            log.warn(
                "attempted to register destroyed uiobject to ui; cannot accept destroyed object");
            return -1;
        }

        if (obj.isActive()) {
            this.aObj.put(obj.getId(), obj);
        } else {
            this.bObj.put(obj.getId(), obj);
        }

        return obj.getId();
    }

    /**
     * Removes the {@link UIObject} of the provided ID from
     * this {@link UIController}.
     * 
     * <p>Using this instead of {@link UIObject#destroy()}
     * will allow the object to be reusable.</p>
     * 
     * @param id the ID of the UIObject to remove
     */
    public void removeUIObject(long id) {
        UIObject removed = this.getUIObject(id);
        if (removed != null) {
            this.removeUIObject(removed);
        }
    }

    /**
     * Removes the provided {@link UIObject} from this
     * {@link UIController}.
     * 
     * <p>Using this instead of {@link UIObject#destroy()}
     * will allow the object to be reusable.</p>
     * 
     * @param obj the UIObject to remove
     */
    public void removeUIObject(UIObject obj) {
        if (!this.removed.contains(obj.getId())) {
            this.removed.add(obj.getId());
        }
    }

    // ---------------- ignored ----------------

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}
}
