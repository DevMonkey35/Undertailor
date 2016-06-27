package me.scarlet.undertailor.engine.ui;

import com.badlogic.gdx.math.Vector2;

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.EventListener;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

/**
 * Child object of a {@link UIObject}, of which handles most
 * of the UI busywork.
 */
public abstract class UIComponent implements Positionable, Renderable, Processable, EventListener,
    Modular<UIObject>, Destructible {

    private boolean destroyed;
    private Transform transform;
    private Vector2 screenProxy;
    private Vector2 position;
    private UIObject parent;

    protected UIComponent() {
        this.screenProxy = new Vector2();
        this.transform = new Transform();
        this.position = new Vector2(0, 0);
        this.destroyed = false;
        this.parent = null;
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
    public abstract boolean catchEvent(String eventName, Object... data);

    @Override
    public abstract void draw(float x, float y, Transform transform);

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
