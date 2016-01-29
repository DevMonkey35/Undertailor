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

package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.BoundingRectangle;
import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.collision.CollisionHandler;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.lua.impl.WorldRoomImplementable;
import me.scarlet.undertailor.manager.ScriptManager;
import me.scarlet.undertailor.overworld.map.RoomMapLayer;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.util.Layerable;
import me.scarlet.undertailor.util.Renderable;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class WorldRoom implements Disposable {
    
    public static class Entrypoint implements Collider {
        
        protected String id;
        private Vector2 velocity;
        private Vector2 spawnloc;
        private String roomTarget;
        private BoundingRectangle boundingBox;
        
        public Entrypoint() {
            this.boundingBox = new BoundingRectangle();
            this.spawnloc = new Vector2(0, 0);
            this.velocity = new Vector2(0, 0); // stored just so we don't need to recreate it
            this.roomTarget = "";
        }
        
        public String getID() {
            return id;
        }
        
        public Vector2 getPosition() {
            return boundingBox.getPosition();
        }
        
        public void setPosition(float x, float y) {
            boundingBox.setPosition(x, y);
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
        
        public void renderBox() {
            Undertailor.getRenderer().setShapeColor(Color.BLUE, 1F);
            this.boundingBox.renderBox();
        }
        
        @Override
        public BoundingRectangle getBoundingBox() {
            return boundingBox;
        }
        
        @Override
        public void onCollide(Collider collider) {
            if(Undertailor.getOverworldController().getCharacterID() > -1) {
                if(collider instanceof WorldObject) {
                    WorldObject obj = (WorldObject) collider;
                    if(obj.getId() == Undertailor.getOverworldController().getCharacterID()) {
                        String[] targetRoom = roomTarget.split(":");
                        String entrypoint = targetRoom.length > 1 ? targetRoom[1] : null;
                        try {
                            ScriptManager scriptMan = Undertailor.getScriptManager();
                            WorldRoomImplementable impl = scriptMan.getImplementable(WorldRoomImplementable.class);
                            WorldRoom room = impl.load(targetRoom[0]);
                            Undertailor.getOverworldController().setCurrentRoom(room, true, id, entrypoint);
                        } catch(LuaScriptException e) {
                            Undertailor.instance.error("overworld", "entrypoint with id " + id + " failed to process room switch: could not load target room " + targetRoom[0]);
                        }
                    }
                }
            }
        }
        
        @Override
        public Vector2 getVelocity() {
            return velocity;
        }
        
        @Override
        public boolean focusCollide() { return false; }
        
        @Override
        public boolean canCollide() { return true; }
        
        @Override
        public boolean isSolid() { return false; }
        
    }
    
    private static final TreeSet<Layerable> RETURN_SET;
    private static final Set<Collider> COLLIDER_SET;
    private static final Set<Collider> TARGET_SET;
    private static long nextId;
    
    static {
        RETURN_SET = new TreeSet<Layerable>((Layerable obj1, Layerable obj2) -> {
            if(obj1.getZ() == obj2.getZ()) {
                if(obj1 instanceof WorldObject && obj2 instanceof WorldObject) {
                    WorldObject wobj1 = (WorldObject) obj1;
                    WorldObject wobj2 = (WorldObject) obj2;
                    return Float.compare(wobj2.getPosition().y, wobj1.getPosition().y);
                } else {
                    if(obj1 instanceof RoomMapLayer) {
                        return -1;
                    }
                    
                    return 1;
                }
            } else {
                return Integer.compare(obj1.getZ(), obj2.getZ());
            }
        });
        
        COLLIDER_SET = new HashSet<Collider>();
        TARGET_SET = new HashSet<Collider>();
        nextId = 0;
    }
    
    private String roomName;
    private RoomDataWrapper roomWrapper;
    private Map<String, Entrypoint> entrypoints;
    
    private Set<WorldObject> removed;
    private Map<Long, WorldObject> added;
    private Map<Long, WorldObject> objects;
    
    public WorldRoom() {
        this.added = new HashMap<>();
        this.removed = new HashSet<>();
        this.entrypoints = new HashMap<>();
        this.objects = new HashMap<>();
        this.roomWrapper = null;
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public RoomDataWrapper getMap() {
        return roomWrapper;
    }
    
    public void setMap(RoomDataWrapper wrapper) {
        this.dispose();
        this.roomWrapper = wrapper;
    }
    
    public Entrypoint getEntrypoint(String name) {
        return entrypoints.get(name);
    }
    
    public void registerEntrypoint(String pointId, Entrypoint point) {
        point.id = pointId;
        Entrypoint old = entrypoints.put(pointId, point);
        if(old != null) {
            old.id = null;
        }
    }
    
    public long registerObject(WorldObject object) {
        long id = nextId++;
        object.id = id;
        object.room = this;
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
    
    public void process(CollisionHandler collisionHandler, float delta, InputData input) {
        for(WorldObject object : objects.values()) {
            Vector2 pos = object.getPosition();
            Vector2 vel = object.getVelocity();
            object.setPosition(pos.x + vel.x, pos.y + vel.y);
        }
        
        processCollisions(collisionHandler);
        onProcess(delta, input);
        for(WorldObject object : objects.values()) {
            object.process(delta, input);
        }
    }
    
    public void render() {
        Set<Layerable> renderOrder = getObjectsInRenderOrder();
        for(Layerable object : renderOrder) {
            if(object instanceof Renderable) {
                ((Renderable) object).render();
            }
        }
        
        if(Undertailor.getOverworldController().isRenderingHitboxes()) {
            for(Layerable object : renderOrder) {
                if(object instanceof WorldObject) {
                    ((WorldObject) object).renderBox();
                }
            }
            
            for(Entrypoint entrypoint : entrypoints.values()) {
                entrypoint.renderBox();
            }
        }
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
    
    private void processCollisions(CollisionHandler handler) {
        TARGET_SET.clear();
        COLLIDER_SET.clear();
        for(Collider collider : objects.values()) {
            if(collider.canCollide()) {
                COLLIDER_SET.add(collider);
                if(collider.focusCollide()) {
                    TARGET_SET.add(collider);
                }
            }
        }
        
        for(Entrypoint entrypoint : entrypoints.values()) {
            COLLIDER_SET.add(entrypoint);
        }
        
        Iterator<Entry<Collider, Set<Collider>>> iterator = handler.process(TARGET_SET, COLLIDER_SET).entrySet().iterator();
        iterator.forEachRemaining(entry -> {
            for(Collider collider : entry.getValue()) {
                collider.onCollide(entry.getKey());
                if(collider instanceof WorldObject) {
                    entry.getKey().onCollide(collider);
                }
            }
        });
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
        for(WorldObject object : objects.values()) {
            object.onPause();
        }
    }
    
    public void resume() {
        this.onResume();
        for(WorldObject object : objects.values()) {
            object.onResume();
        }
    }
    
    private TreeSet<Layerable> getObjectsInRenderOrder() {
        RETURN_SET.clear();
        
        if(this.roomWrapper != null) {
            for(RoomMapLayer layer : roomWrapper.getReference().getLayers()) {
                RETURN_SET.add(layer);
            }
        }
        
        for(WorldObject object : objects.values()) {
            RETURN_SET.add(object);
        }
        
        return RETURN_SET;
    }
    
    @Override
    public void dispose() {
        if(roomWrapper != null) {
            roomWrapper.removeReference(this);
            roomWrapper.dispose();
        }
    }
    
    public void onPause() {}
    public void onResume() {}
    public void onProcess(float delta, InputData input) {}
    public void onEnter(Entrypoint entrypoint) {}
    public void onExit(Entrypoint exitpoint) {}
}
