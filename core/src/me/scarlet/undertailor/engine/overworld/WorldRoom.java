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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.PotentialDelay;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.events.Event;
import me.scarlet.undertailor.engine.events.EventHelper;
import me.scarlet.undertailor.engine.events.EventListener;
import me.scarlet.undertailor.engine.overworld.map.TileLayer;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A container for entities within an Overworld.
 */
public abstract class WorldRoom implements Renderable, Processable, Destructible, EventListener,
    Modular<OverworldController>, PotentialDelay {

    static final Comparator<Layerable> RENDER_COMPARATOR;
    static final Logger log = LoggerFactory.getLogger(WorldRoom.class);

    static {
        RENDER_COMPARATOR = (Layerable obj1, Layerable obj2) -> {
            // World Objects and Image Layers MUST come on top of a Tile Layer.
            // Positionable identifies both World Objects and Image Layers.

            if (obj1.getLayer() == obj2.getLayer()) {
                if (obj1 instanceof Positionable && obj2 instanceof Positionable) {
                    float y1 = ((Positionable) obj1).getPosition().y;
                    float y2 = ((Positionable) obj2).getPosition().y;

                    if (y1 == y2) {
                        return 1;
                    }

                    return Float.compare(y2, y1);
                }

                if (obj1 instanceof TileLayer && obj2 instanceof TileLayer) {
                    long i1 = ((Identifiable) obj1).getId();
                    long i2 = ((Identifiable) obj2).getId();

                    return Long.compare(i1, i2);
                }

                return obj1 instanceof TileLayer ? -1 : 1;
            }

            return Short.compare(obj1.getLayer(), obj2.getLayer());
        };
    }

    // while delayed
    private boolean prepared;
    private ObjectSet<WorldObject> bodyQueue;
    private ObjectSet<Entrypoint> entrypointQueue;

    // primary
    private boolean destroyed;
    protected Tilemap tilemap;
    private EventHelper events;
    private ObjectSet<WorldObject> obj;
    private OverworldController controller;
    private ObjectMap<String, Entrypoint> entrypoints;
    private ObjectMap<String, ObjectSet<Body>> collisionLayers;
    private IntFloatMap opacityMapping;
    private Array<Layerable> renderOrder;
    private ObjectSet<String> disabledCollision;

    public WorldRoom(Tilemap map) {
        this.destroyed = false;
        this.prepared = false;
        this.events = new EventHelper(this);
        this.bodyQueue = new ObjectSet<>();
        this.entrypointQueue = new ObjectSet<>();

        this.tilemap = map;
        this.controller = null;
        this.obj = new ObjectSet<>();
        this.entrypoints = new ObjectMap<>();
        this.opacityMapping = new IntFloatMap();
        this.collisionLayers = new ObjectMap<>();
        this.renderOrder = new Array<>(true, 16);
        this.disabledCollision = new ObjectSet<>();
    }

    // ---------------- abstract method implementation ----------------

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

        for (WorldObject obj : this.obj) {
            if (obj.callEvent(event)) {
                processed = true;
            }
        }

        return processed;
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public void setTransform(Transform transform) {}

    @Override
    public void render(float x, float y, Transform transform) {
        Array<Layerable> rendered = this.getInRenderOrder();
        MultiRenderer renderer = this.controller.getRenderer();
        rendered.forEach(obj -> {
            renderer.setBatchColor(renderer.getBatchColor(), this.getLayerOpacity(obj.getLayer()));
            ((Renderable) obj).render();
        });
    }

    @Override
    public final boolean process() {
        this.processRoom();
        Iterator<WorldObject> iter = obj.iterator();
        while (iter.hasNext()) {
            WorldObject next = iter.next();
            if (next.isDestroyed()) {
                iter.remove();
            } else {
                next.process();
            }
        }

        return true;
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public final void destroy() {
        if (this.destroyed) {
            return;
        }

        this.tilemap = null;
        Iterator<WorldObject> iterator = this.obj.iterator();
        while (iterator.hasNext()) {
            WorldObject next = iterator.next();
            iterator.remove();
            if (!next.isPersistent()) {
                next.destroy();
            }

            next.release(this);
        }

        this.entrypoints.values().forEach(entrypoint -> {
            entrypoint.release(this);
        });

        this.collisionLayers.values().forEach(collisionSet -> {
            collisionSet.forEach(body -> {
                body.getWorld().destroyBody(body);
            });
        });

        this.controller = null;
        this.destroyed = true;
    }

    @Override
    public final boolean claim(OverworldController controller) {
        if (this.controller == null) {
            this.controller = controller;
            return true;
        }

        return false;
    }

    @Override
    public final boolean release(OverworldController controller) {
        this.destroy();
        return true;
    }

    @Override
    public boolean poke() {
        if (!prepared) {
            if (this.tilemap == null || this.tilemap.isLoaded()) {
                this.bodyQueue.forEach(
                    obj -> obj.createBody(this.controller.getCollisionHandler().getWorld()));
                this.entrypointQueue.forEach(entrypoint -> {
                    if (entrypoint.claim(this)) {
                        Entrypoint old = this.entrypoints.put(entrypoint.getName(), entrypoint);
                        if (old != null) {
                            old.release(this);
                        }
                    }
                });

                this.prepareMap(this.tilemap);
                this.entrypointQueue = null;
                this.bodyQueue = null;
                this.prepared = true;
                this.onLoad();

                this.callEvent(new Event(Event.EVT_LOAD));
            }

            return false;
        }

        return true;
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
     * Registers an entrypoint to be used by this
     * {@link WorldRoom}.
     * 
     * <p>If the provided entrypoint's name conflicts with
     * another of which is already registered, the old one
     * is replaced with the one given.</p>
     * 
     * @param entrypoint a new Entrypoint
     */
    public void registerEntrypoint(Entrypoint entrypoint) {
        if (this.entrypointQueue != null) {
            this.entrypointQueue.add(entrypoint);
        } else {
            if (entrypoint.claim(this)) {
                Entrypoint old = this.entrypoints.put(entrypoint.getName(), entrypoint);
                if (old != null) {
                    old.release(this);
                }
            }
        }
    }

    /**
     * Returns an entrypoint held by this {@link WorldRoom}.
     * 
     * @param entrypoint an Entrypoint name
     * 
     * @return the Entrypoint under the given name,
     *         otherwise null
     */
    public Entrypoint getEntrypoint(String entrypoint) {
        return this.entrypoints.get(entrypoint);
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

        if (this.bodyQueue != null) {
            this.bodyQueue.add(obj);
        } else {
            obj.createBody(this.controller.getCollisionHandler().getWorld());
        }
    }

    /**
     * Returns all {@link WorldObject}s registered with this
     * {@link WorldRoom}. <strong>This list is NOT to be
     * modified.</p>
     * 
     * <p>Use {@link #registerObject(WorldObject)} and
     * {@link #removeObject(WorldObject)} to modify this
     * Set.</p>
     * 
     * @return a Set containing this WorldRoom's
     *         WorldObjects; should NOT be directly modified
     */
    public ObjectSet<WorldObject> getObjects() {
        return this.obj;
    }

    /**
     * Registers the provided {@link WorldObject} with this
     * {@link WorldRoom}.
     * 
     * @param obj the WorldObject to register
     */
    public void registerObject(WorldObject obj) {
        if (obj.isDestroyed()) {
            log.warn(
                "attempted to submit destroyed worldobject to a worldroom; cannot accept a destroyed object");
            return;
        }

        if (obj.claim(this)) {
            this.obj.add(obj);
            this.renderOrder.add(obj);
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
            this.renderOrder.removeValue(obj, false);
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
        WorldObject removed = null;
        for (WorldObject obj : this.obj) {
            if (obj.getId() == id) {
                removed = obj;
                break;
            }
        }

        if (removed != null) {
            this.removeObject(removed);
        }
    }

    /**
     * Returns the state of a collision layer.
     * 
     * @param layerName the name of the layer
     * 
     * @return whether or not the layer is active
     */
    public boolean getCollisionLayerState(String layerName) {
        return !this.disabledCollision.contains(layerName);
    }

    /**
     * Sets the state of a collision layer.
     * 
     * @param layerName the name of the layer
     * @param flag whether or not the layer is active
     */
    public void setCollisionLayerState(String layerName, boolean flag) {
        if (flag != this.getCollisionLayerState(layerName)) {
            if (flag) {
                this.disabledCollision.remove(layerName);
            } else {
                this.disabledCollision.add(layerName);
            }

            ObjectSet<Body> bodies = this.collisionLayers.get(layerName);
            if (bodies != null) {
                bodies.forEach(body -> {
                    body.setActive(flag);
                });
            }
        }
    }

    /**
     * Returns the current opacity value set for the
     * provided layer.
     * 
     * @param layer the target layer
     * 
     * @return the layer opacity for the provided layer,
     *         defaulting to 1.0
     */
    public float getLayerOpacity(short layer) {
        return this.opacityMapping.get(layer, 1.0F);
    }

    /**
     * Sets the current opacity value for the provided
     * layer.
     * 
     * @param layer the target layer
     * @param opacity the opacity value, between 0.0 and
     *        1.0, to set
     */
    public void setLayerOpacity(short layer, float opacity) {
        if (opacity >= 1F) {
            this.opacityMapping.remove(layer, 1F); // defaults to 1F;
            return;
        }

        this.opacityMapping.put(layer, opacity < 0 ? 0 : opacity);
    }

    /**
     * Returns the {@link Tilemap} currently loaded on this
     * {@link WorldRoom}.
     * 
     * @return this WorldRoom's current Tilemap
     */
    public Tilemap getMap() {
        return this.tilemap;
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
     * with a higher Y position, ensuring objects "in the
     * back" appear how they should. For objects with a same
     * Y position, its first come, first serve.</p>
     * 
     * <p>Rules for draw order are as follows (tl;dr).</p>
     * 
     * <pre>
     * * Any tilemap layer comes first.
     * * Worldobjects come after.
     * </pre>
     * 
     * <pre>
     * * WorldObjects on the same layer are decided by their
     *   Y position.
     *   * Lower Y positions get rendered in front.
     * * Tile layers on the same layer are decided by their
     *   registry order.
     *   * First ones get rendered in the back. See how
     *     layers react in Tiled for reference.
     * </pre>
     */
    private Array<Layerable> getInRenderOrder() {
        this.renderOrder.sort(RENDER_COMPARATOR);
        return this.renderOrder;
    }

    /**
     * Internal method.
     * 
     * <p>Locks the set tilemap as this room's, and adds its
     * collision to the world.</p>
     */
    private void prepareMap(Tilemap map) {
        if (map == null) {
            return;
        }

        World world = this.getOverworld().getCollisionHandler().getWorld();
        BodyDef def = new BodyDef();
        def.type = BodyType.StaticBody;
        def.active = true;
        map.getObjectLayers().forEach(layer -> {
            if (layer.getName().length() > 0
                && layer.getName().charAt(0) == TilemapFactory.OBJ_DEF_LAYER_PREFIX) {
                // def layer, ignore
            } else {
                ObjectSet<Body> layerBodies = new ObjectSet<>();
                boolean active = this.getCollisionLayerState(layer.getName());
                layer.getShapes().forEach(data -> {
                    def.position.set(data.getPosition());
                    def.position.x = def.position.x * OverworldController.PIXELS_TO_METERS;
                    def.position.y = def.position.y * OverworldController.PIXELS_TO_METERS;
                    def.active = active;

                    Shape shape = data.generateShape();
                    layerBodies.add(world.createBody(def).createFixture(shape, 0).getBody());
                    shape.dispose();
                });
            }
        });

        map.getTileLayers().forEach(this.renderOrder::add);
        map.getImageLayers().forEach(this.renderOrder::add);
    }

    // ---------------- abstract definitions ----------------

    /**
     * Called before processing the rest of the room.
     * 
     * <p>Rooms have self-processing alongside executing the
     * process methods of their contained
     * {@link WorldObject}s. This method is called prior to
     * the objects' processing.</p>
     */
    public abstract void processRoom();

    /**
     * Called right when this room's tilemap finishes
     * loading.
     * 
     * <p>Entrypoint preparation methods should be called
     * here.</p>
     */
    public abstract void onLoad();
}
