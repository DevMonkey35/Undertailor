package me.scarlet.undertailor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import me.scarlet.undertailor.ui.event.UIEvent;

/**
 * A component of a {@link UIObject}, which may display something on-screen.
 */
public abstract class UIComponent {
    
    /** Holding the parent {@link UIObject} that owns this UIComponent. */
    private UIObject parent;
    /**
     * Holding the current position of this UIComponent within the positional
     * system of the parent {@link UIObject}.
     */
    private Vector3 position;
    private boolean isAlwaysActive;
    
    public UIComponent() {
        this(new Vector3(0, 0, 0));
    }
    
    public UIComponent(Vector3 position) {
        this.position = position;
        this.isAlwaysActive = false;
    }
    
    /**
     * Returns the parent {@link UIObject} that owns this {@link UIComponent}.
     * 
     * <p>Applying changes to an unowned UIObject will not have any adverse
     * effects outside of simply not being cared about by the system.</p>
     */
    public UIObject getParent() {
        return parent;
    }
    
    /**
     * Returns the current position of this {@link UIComponent} within the
     * positional system of its parent {@link UIObject}.
     * 
     * <p>If this has no parent, it simply returns the position itself.</p>
     */
    public Vector3 getPosition() {
        return position;
    }
    
    /**
     * Returns the current position of this {@link UIComponent} within the real
     * positional system used to display any UI objects.
     * 
     * <p>This is simply returned by adding the position of this UIComponent to
     * the position of its parent UIObject.</p>
     */
    public Vector3 getRealPosition() {
        return parent.getPosition().add(position);
    }
    
    /**
     * Sets the position of this {@link UIComponent} within the positional
     * system of its parent {@link UIObject}.
     * 
     * @param position the new position for this UIComponent to reside at
     */
    public void setPosition(Vector3 position) {
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
    
    public void destroy() {
        if(this.parent != null) {
            parent.destroyChild(this);
        }
    }
    
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
    public void process(float delta) {}
    
    /**
     * Called whenever this {@link UIComponent} is passed any {@link UIEvent}s.
     * 
     * @param event the UIEvent to process
     */
    public void onEvent(UIEvent event) {}
    
    /**
     * Renders this {@link UIComponent}.
     * 
     * @param batch the {@link Batch} used to draw this UIComponent
     * @param parentAlpha the alpha of the parent UIObject, which should be
     *            multiplied by this object's current alpha
     */
    public abstract void render(Batch batch, float parentAlpha);
}
