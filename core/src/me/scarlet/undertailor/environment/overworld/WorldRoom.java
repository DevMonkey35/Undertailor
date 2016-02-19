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

package me.scarlet.undertailor.environment.overworld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.collision.CollisionHandler;
import me.scarlet.undertailor.collision.bbshapes.BoundingBox;
import me.scarlet.undertailor.collision.bbshapes.BoundingRectangle;
import me.scarlet.undertailor.environment.OverworldController;
import me.scarlet.undertailor.environment.overworld.map.RoomMapLayer;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.Layerable;
import me.scarlet.undertailor.util.Positionable;
import me.scarlet.undertailor.util.Renderable;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class WorldRoom implements Disposable {
    
    public static class Entrypoint implements Collider {
        
        public static final BodyDef ENTRYPOINT_BODY_DEF;
        public static final String ENTRYPOINT_BOX_ID = "entrypointBox";
        
        static {
            ENTRYPOINT_BODY_DEF = new BodyDef();
            ENTRYPOINT_BODY_DEF.active = true;
            ENTRYPOINT_BODY_DEF.allowSleep = true;
            ENTRYPOINT_BODY_DEF.awake = true;
            ENTRYPOINT_BODY_DEF.fixedRotation = true;
            ENTRYPOINT_BODY_DEF.type = BodyType.StaticBody;
        }
        
        protected String id;
        protected Body body;
        protected WorldRoom currentRoom;
        
        private Vector2 spawnloc;
        private String roomTarget;
        private Set<Collider> contacts;
        private Map<String, BoundingBox> boundingBoxes;
        
        public Entrypoint() {
            this.boundingBoxes = new HashMap<>();
            this.boundingBoxes.put(ENTRYPOINT_BOX_ID, new BoundingRectangle());
            this.spawnloc = new Vector2(0, 0);
            this.roomTarget = "";
            this.contacts = new HashSet<>();
        }
        
        public String getID() {
            return id;
        }
        
        public Vector2 getPosition() {
            return body.getPosition();
        }
        
        public void setPosition(float x, float y) {
            body.getPosition().set(x, y);
        }
        
        public Vector2 getSpawnPosition() {
            return spawnloc;
        }
        
        public void setSpawnPosition(float x, float y) {
            spawnloc.set(x, y);
        }
        
        public String getRoomTarget() {
            return roomTarget;
        }
        
        public void setRoomTarget(String target) {
            this.roomTarget = target;
        }
        
        @Override
        public Set<Collider> getContacts() {
            return this.contacts;
        }
        
        @Override
        public Body getBody() {
            return this.body;
        }
        
        @Override
        public Set<BoundingBox> getBoundingBoxes() {
            return new HashSet<>(this.boundingBoxes.values());
        }
        
        @Override
        public void onCollide(Collider collider) {
            if(this.currentRoom != null) {
                OverworldController ovw = currentRoom.getOwningController();
                if(ovw.getCharacterID() > -1) {
                    if(collider instanceof WorldObject) {
                        WorldObject obj = (WorldObject) collider;
                        if(obj.getId() == ovw.getCharacterID()) {
                            String[] targetRoom = roomTarget.split(":");
                            String entrypoint = targetRoom.length > 1 ? targetRoom[1] : null;
                            try {
                                ScriptManager scriptMan = Undertailor.getScriptManager();
                                WorldRoomImplementable impl = scriptMan.getImplementable(WorldRoomImplementable.class);
                                WorldRoom room = impl.load(targetRoom[0]);
                                ovw.setCurrentRoom(room, true, id, entrypoint);
                            } catch(LuaScriptException e) {
                                Undertailor.instance.error("overworld", "entrypoint with id " + id + " failed to process room switch: could not load target room " + targetRoom[0]);
                            }
                        }
                    }
                }
            }
        }
        
        @Override
        public boolean canCollide() { return true; }
        
        @Override
        public boolean isOneSidedReaction() { return true; }
        
        @Override
        public boolean isCollisionIgnored(Collider collider) {
            if(this.currentRoom != null) {
                if(collider instanceof WorldObject && ((WorldObject) collider).getId() == this.currentRoom.getOwningController().getCharacterID()) {
                    return false;
                }
            }
            
            return true;
        }
        
        @Override
        public void setOneSidedReaction(boolean flag) {} // nope
        
        @Override
        public void setIgnoreCollisionWith(Collider collider, boolean flag) {} // nope

        @Override
        public BoundingBox getBoundingBox(String id) { return this.boundingBoxes.get(ENTRYPOINT_BOX_ID); } // nope

        @Override
        public void setBoundingBox(String id, BoundingBox box) {} // nope
    }
    
    private static final TreeSet<Layerable> RETURN_SET;
    private static long nextId;
    
    static {
        RETURN_SET = new TreeSet<>((Layerable obj1, Layerable obj2) -> {
            if (obj1.getZ() == obj2.getZ()) {
                if (obj1 instanceof Positionable && obj2 instanceof Positionable) {
                    Positionable wobj1 = (Positionable) obj1;
                    Positionable wobj2 = (Positionable) obj2;

                    if (wobj1.getPosition().y == wobj2.getPosition().y) {
                        return 1; // doesn't matter as long as its not 0
                    } else {
                        return Float.compare(wobj2.getPosition().y, wobj1.getPosition().y);
                    }
                } else {
                    if (obj1 instanceof RoomMapLayer) {
                        return -1;
                    }

                    return 1;
                }
            } else {
                return Integer.compare(obj1.getZ(), obj2.getZ());
            }
        });
        
        nextId = 0;
    }
    
    private String roomName;
    private RoomDataWrapper roomWrapper;
    private Map<String, Entrypoint> entrypoints;
    
    private Set<WorldObject> removed;
    private Map<Long, WorldObject> added;
    private Map<Long, WorldObject> objects;
    private CollisionHandler collision;
    
    protected OverworldController currentController;
    
    public WorldRoom() {
        this.added = new HashMap<>();
        this.removed = new HashSet<>();
        this.entrypoints = new HashMap<>();
        this.objects = new HashMap<>();
        this.roomWrapper = null;
        this.collision = new CollisionHandler();
        this.currentController = null;
    }
    
    public OverworldController getOwningController() {
        return this.currentController;
    }
    
    public void claim(OverworldController controller) {
        if(this.currentController == null) {
            this.currentController = controller;
        } else {
            throw new IllegalStateException("cannot claim room for controller; already owned by another controller");
        }
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public RoomDataWrapper getMap() {
        return roomWrapper;
    }
    
    public void setMap(RoomDataWrapper wrapper) {
        if(roomWrapper != null) {
            roomWrapper.removeReference(this);
            roomWrapper.dispose();
        }
        
        this.roomWrapper = wrapper;
    }
    
    public Entrypoint getEntrypoint(String name) {
        return entrypoints.get(name);
    }
    
    public void registerEntrypoint(String pointId, Entrypoint point) {
        point.id = pointId;
        point.currentRoom = this;
        Entrypoint old = entrypoints.put(pointId, point);
        if(old != null) {
            old.id = null;
            old.currentRoom = null;
        }
    }
    
    public long registerObject(WorldObject object) {
        long id = nextId++;
        object.claim(id, this, collision.getWorld().createBody(object.getBodyDef()));
        object.onRegister(object.id, this);
        
        added.put(id, object);
        return id;
    }
    
    public void registerPersistentObject(WorldObject object) {
        added.put(object.id, object);
    }
    
    public WorldObject getObject(long id) {
        return objects.get(id);
    }
    
    public void removeObject(long id) {
        WorldObject obj = objects.get(id);
        if(obj != null) {
            removed.add(obj);
        }
    }
    
    public void forceProcess() {
        updateMapping();
    }
    
    public void process(float delta, InputData input) {
        onProcess(delta, input);
        
        for(WorldObject object : objects.values()) {
            object.process(delta, input);
        }
        
        collision.step(delta);

        objects.values().stream().filter(WorldObject::canCollide)
                .forEach(object -> object.getContacts().stream()
                        .filter(collider -> collider.canCollide()
                                && !collider.isOneSidedReaction()
                                && !object.isCollisionIgnored(collider))
                        .forEach(object::onCollide));
    }
    
    public void render() {
        Set<Layerable> renderOrder = getObjectsInRenderOrder();
        renderOrder.stream()
                .filter(object -> object instanceof Renderable)
                .forEach(object -> ((Renderable) object).render());
    }
    
    private void updateMapping() {
        for(Entry<Long, WorldObject> entry : added.entrySet()) {
            WorldObject obj = entry.getValue();
            obj.id = entry.getKey();
            obj.room = this;
            objects.put(entry.getKey(), obj);
        }
        
        for(WorldObject object : removed) {
            object.room = null;
            objects.remove(object.id);
            object.id = -1;
        }
    }
    
    public Set<WorldObject> prepareExit() {
        Iterator<WorldObject> iterator = objects.values().iterator();
        Set<WorldObject> set = new HashSet<>();
        iterator.forEachRemaining(obj -> {
            if(obj.isPersisting()) {
                set.add(obj);
            } else {
                obj.onDestroy();
                obj.id = -1;
                obj.room = null;
            }
        });
        
        return set;
    }
    
    public void pause() {
        this.onPause();
        objects.values().forEach(WorldObject::onPause);
    }
    
    public void resume() {
        this.onResume();
        objects.values().forEach(WorldObject::onResume);
    }
    
    private TreeSet<Layerable> getObjectsInRenderOrder() {
        RETURN_SET.clear();
        
        if(this.roomWrapper != null) {
            for(RoomMapLayer layer : roomWrapper.getReference().getLayers()) {
                RETURN_SET.add(layer);
                RETURN_SET.addAll(layer.getSpriteObjects().stream().collect(Collectors.toList()));
            }
        }

        RETURN_SET.addAll(objects.values().stream().collect(Collectors.toList()));
        
        return RETURN_SET;
    }
    
    @Override
    public void dispose() {
        if(roomWrapper != null) {
            roomWrapper.removeReference(this);
            roomWrapper.dispose();
            this.roomWrapper = null;
        }
        
        this.collision.getWorld().dispose();
        this.collision = null;
    }
    
    public CollisionHandler getCollisionHandler() {
        return this.collision;
    }
    
    public void onPause() {}
    public void onResume() {}
    public void onProcess(float delta, InputData input) {}
    public void onEnter(Entrypoint entrypoint) {}
    public void onExit(Entrypoint exitpoint) {}
}
