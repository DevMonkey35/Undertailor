package me.scarlet.undertailor.engine.overworld;

import com.badlogic.gdx.physics.box2d.Body;

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.EventListener;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.overworld.map.TileLayer;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A container for entities within an Overworld.
 */
public abstract class WorldRoom
    implements Renderable, Processable, Destructible, EventListener, Modular<OverworldController> {

    static final Set<Layerable> RENDER_ORDER;

    static {
        RENDER_ORDER = new TreeSet<>((Layerable obj1, Layerable obj2) -> {
            // assumed that each entry is an instance of layerable, and worldobjects are instances of positionable
            if (obj1.getLayer() == obj2.getLayer()) {
                if (obj1 instanceof Positionable && obj2 instanceof Positionable) {
                    Positionable wobj1 = (Positionable) obj1;
                    Positionable wobj2 = (Positionable) obj2;

                    if (wobj1.getPosition().y == wobj2.getPosition().y) {
                        return 1; // doesn't matter as long as its not 0
                    } else {
                        return Float.compare(wobj2.getPosition().y, wobj1.getPosition().y);
                    }
                } else {
                    if (obj1 instanceof TileLayer) {
                        return -1;
                    }

                    return 1;
                }
            } else {
                return Short.compare(obj1.getLayer(), obj2.getLayer());
            }
        });
    }

    private Tilemap tilemap;
    private Set<WorldObject> obj;
    private Set<WorldObject> bodyQueue;
    private OverworldController controller;

    public WorldRoom() {
        this.tilemap = null;
        this.obj = new HashSet<>();
        this.bodyQueue = new HashSet<>();
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    @Override
    public void draw(float x, float y, Transform transform) {
        Set<Layerable> rendered = this.getInRenderOrder();
        rendered.forEach(obj -> ((Renderable) obj).draw());
    }

    @Override
    public final boolean process(Object... params) {
        this.processRoom();
        this.obj.forEach(obj -> obj.process(params));
        return true;
    }

    @Override
    public final void destroy() {
        this.tilemap = null;
        this.obj.forEach(obj -> {
            this.removeObject(obj);
            if (!this.controller.isCharacter(obj)) {
                obj.destroy();
            }
        });
    }

    @Override
    public final boolean claim(OverworldController controller) {
        if (this.controller == null) {
            this.controller = controller;
            this.bodyQueue
                .forEach(obj -> obj.createBody(this.controller.getCollisionHandler().getWorld()));
            return true;
        }

        return false;
    }

    @Override
    public final boolean release(OverworldController controller) {
        if (this.controller == controller) {
            this.destroy();
            this.controller = null;
            return true;
        }

        return false;
    }

    // ---------------- object methods ----------------

    /**
     * Returns the {@link OverworldController} owning this
     * {@link WorldRoom}.
     * 
     * @return the OverworldController for this WorldRoom
     */
    public OverworldController getOverworld() {
        return this.controller;
    }

    /**
     * Sets the {@link Tilemap} used by this
     * {@link WorldRoom}.
     * 
     * @param map the map to use
     */
    public void setMap(Tilemap map) {
        this.tilemap = map;
        // TODO collision layers once tilemap reader supports them
    }

    /**
     * Allows a {@link WorldObject} to request a
     * {@link Body} from this {@link WorldRoom}.
     * 
     * @param obj the WorldObject requesting a Body
     */
    public void requestBody(WorldObject obj) {
        if (obj.getBody() != null) {
            return;
        }

        if (this.controller != null) {
            obj.createBody(this.controller.getCollisionHandler().getWorld());
        } else {
            this.bodyQueue.add(obj);
        }
    }

    /**
     * Registers the provided {@link WorldObject} with this
     * {@link WorldRoom}.
     * 
     * @param obj the WorldObject to register
     */
    public void registerObject(WorldObject obj) {
        if (obj.claim(this)) {
            this.obj.add(obj);
        }
    }

    /**
     * Removes the provided {@link WorldObject} from this
     * {@link WorldRoom} and destroys it.
     * 
     * @param obj the object to remove and destroy
     */
    public void removeObject(WorldObject obj) {
        if (obj.release(this)) {
            this.obj.remove(obj);
            obj.destroy();
        }
    }

    /**
     * Removes the {@link WorldObject} matching the provided
     * ID from this {@link WorldRoom} if it contains it, and
     * destroys it.
     * 
     * @param id the ID of the WorldObject to remove
     */
    public void removeObject(long id) {
        for (WorldObject obj : this.obj) {
            if (obj.getId() == id) {
                removeObject(obj);
                break;
            }
        }
    }

    // ---------------- internal methods ----------------

    /**
     * Internal method.
     * 
     * <p>Returns the objects to be rendered by this
     * {@link WorldRoom} when called upon in their proper
     * render order.</p>
     * 
     * <p>The render order properly renders objects so they
     * appear in their proper places when the entire room is
     * rendered. Priority is given to objects on the lowest
     * layers, with {@link Tilemap} {@link TileLayer}s
     * taking precedence over {@link WorldObject}s.
     * TileLayers are expected to only be one per layer,
     * however there can be multiple objects on one layer.
     * Render order for WorldObjects prioritizes objects
     * with a higher Y position, ensuring objects
     * "in the back" appear how they should. For objects
     * with a same Y position, its first come, first
     * serve.</p>
     * 
     * <p>In draw order, the order is:</p>
     * 
     * <pre>
     * * Lower layers come first.
     * * TileLayer first.
     * * WorldObjects with a higher Y position.
     * </pre>
     */
    private Set<Layerable> getInRenderOrder() {
        RENDER_ORDER.clear();

        tilemap.getLayers().forEach(RENDER_ORDER::add);
        obj.forEach(RENDER_ORDER::add);

        return RENDER_ORDER;
    }

    // ---------------- abstract definitions ----------------

    @Override
    public abstract boolean catchEvent(String eventName, Map<String, Object> data);

    /**
     * Called before processing the rest of the room.
     * 
     * <p>Rooms have self-processing alongside executing the
     * process methods of their contained
     * {@link WorldObject}s. This method is called prior to
     * the objects' processing.</p>
     */
    public abstract void processRoom();
}
