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

import me.scarlet.undertailor.engine.Destructible;
import me.scarlet.undertailor.engine.EventListener;
import me.scarlet.undertailor.engine.Identifiable;
import me.scarlet.undertailor.engine.Layerable;
import me.scarlet.undertailor.engine.Modular;
import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.engine.PotentialDelay;
import me.scarlet.undertailor.engine.Processable;
import me.scarlet.undertailor.engine.overworld.map.TileLayer;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory;
import me.scarlet.undertailor.engine.overworld.map.TilemapFactory.Tilemap;
import me.scarlet.undertailor.gfx.MultiRenderer;
import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A container for entities within an Overworld.
 */
public abstract class WorldRoom implements Renderable, Processable, Destructible, EventListener,
    Modular<OverworldController>, PotentialDelay {

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
                    if (obj1 instanceof Identifiable && obj2 instanceof Identifiable) {
                        Identifiable iobj1 = (Identifiable) obj1;
                        Identifiable iobj2 = (Identifiable) obj2;
                        if (obj1 instanceof WorldObject) { // tilemap layer vs worldobj
                            return 1;
                        } else { // tilemap layer vs tilemap layer
                            return Long.compare(iobj1.getId(), iobj2.getId());
                        }
                    }

                    return 1;
                }
            } else {
                return Short.compare(obj1.getLayer(), obj2.getLayer());
            }
        });
    }

    // while delayed
    private boolean prepared;
    private Set<WorldObject> bodyQueue;
    private Set<Entrypoint> entrypointQueue;

    // primary
    protected Tilemap tilemap;
    private Set<WorldObject> obj;
    private OverworldController controller;
    private Map<String, Entrypoint> entrypoints;
    private Map<String, Set<Body>> collisionLayers;
    private Map<Short, Float> opacityMapping;

    public WorldRoom(Tilemap map) {
        this.prepared = false;
        this.bodyQueue = new HashSet<>();
        this.entrypointQueue = new HashSet<>();

        this.tilemap = map;
        this.controller = null;
        this.obj = new HashSet<>();
        this.entrypoints = new HashMap<>();
        this.opacityMapping = new HashMap<>();
        this.collisionLayers = new HashMap<>();
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
        Set<Layerable> rendered = this.getInRenderOrder();
        MultiRenderer renderer = this.controller.getRenderer();
        rendered.forEach(obj -> {
            float alpha = this.opacityMapping.containsKey(obj.getLayer()) ? this.opacityMapping.get(obj.getLayer()) : 1.0F;
            renderer.setBatchColor(renderer.getBatchColor(), alpha);
            ((Renderable) obj).draw();
        });
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
        Iterator<WorldObject> iterator = this.obj.iterator();
        while (iterator.hasNext()) {
            WorldObject next = iterator.next();
            iterator.remove();
            next.release(this);
            if (!this.controller.isCharacter(next)) {
                next.destroy();
            }
        }

        this.entrypoints.values().forEach(entrypoint -> {
            entrypoint.release(this);
        });

        this.collisionLayers.values().forEach(collisionSet -> {
            collisionSet.forEach(body -> {
                body.getWorld().destroyBody(body);
            });
        });
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
        this.controller = null;
        return true;
    }

    @Override
    public boolean poke() {
        if (!prepared) {
            if (this.tilemap.isLoaded()) {
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
        if(this.entrypointQueue != null) {
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
        if (this.collisionLayers.containsKey(layerName)) {
            for (Body body : this.collisionLayers.get(layerName)) {
                return body.isActive();
            }
        }

        return false;
    }

    /**
     * Sets the state of a collision layer.
     * 
     * @param layerName the name of the layer
     * @param flag whether or not the layer is active
     */
    public void setCollisionLayerState(String layerName, boolean flag) {
        if (this.collisionLayers.containsKey(layerName)
            && this.getCollisionLayerState(layerName) != flag) { // activating a body is just as expensive as creating it
            this.collisionLayers.get(layerName).forEach(body -> {
                body.setActive(flag);
            });
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
        if(this.opacityMapping.containsKey(layer)) {
            return this.opacityMapping.get(layer);
        }

        return 1F;
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
        if(opacity >= 1F) {
            this.opacityMapping.remove(layer); // defaults to 1F;
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
     * with a higher Y position, ensuring objects
     * "in the back" appear how they should. For objects
     * with a same Y position, its first come, first
     * serve.</p>
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
    private Set<Layerable> getInRenderOrder() {
        RENDER_ORDER.clear();

        if (tilemap != null) {
            tilemap.getTileLayers().forEach(RENDER_ORDER::add);
            tilemap.getImageLayers().forEach(RENDER_ORDER::add);
        }

        obj.forEach(RENDER_ORDER::add);

        return RENDER_ORDER;
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
                Set<Body> layerBodies = new HashSet<>();
                layer.getShapes().forEach(data -> {
                    def.position.set(data.getPosition());
                    def.position.x = def.position.x * OverworldController.PIXELS_TO_METERS;
                    def.position.y = def.position.y * OverworldController.PIXELS_TO_METERS;
                    Shape shape = data.generateShape();
                    layerBodies.add(world.createBody(def).createFixture(shape, 0).getBody());
                    shape.dispose();
                });

                this.collisionLayers.put(layer.getName(), layerBodies);
            }
        });
    }

    // ---------------- abstract definitions ----------------

    @Override
    public abstract boolean catchEvent(String eventName, Object... data);

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
