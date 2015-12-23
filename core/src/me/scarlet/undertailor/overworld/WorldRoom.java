package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.collision.BoundingRectangle;
import me.scarlet.undertailor.collision.Collider;
import me.scarlet.undertailor.collision.CollisionHandler;
import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.gfx.Sprite;
import me.scarlet.undertailor.lua.LuaWorldRoom;
import me.scarlet.undertailor.manager.RoomManager;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import me.scarlet.undertailor.wrappers.TilemapWrapper;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class WorldRoom implements Disposable {
    
    public static class RoomMap implements Disposable {
        
        public static RoomMap fromConfig(ConfigurationNode node) {
            RoomMap map = new RoomMap();
            map.sizeX = ConfigurateUtil.processInt(node.getNode("sizeX"), null);
            map.sizeY = ConfigurateUtil.processInt(node.getNode("sizeY"), null);
            String[] tilemapNames = ConfigurateUtil.processStringArray(node.getNode("tilemaps"), null);
            String[] stringMapping = ConfigurateUtil.processStringArray(node.getNode("mapping"), null);
            
            map.tilemaps = new TilemapWrapper[tilemapNames.length];
            for(int i = 0; i < map.tilemaps.length; i++) {
                TilemapWrapper wrapper = Undertailor.getTilemapManager().getObject(tilemapNames[i]);
                if(wrapper == null) {
                    Undertailor.instance.error(RoomManager.MANAGER_TAG, "failed to load room: map data referenced non-existing tilemap (" + tilemapNames[i] + ")");
                    return null;
                }
                
                map.tilemaps[i] = wrapper;
            }
            
            map.mapping = new Sprite[map.sizeY][map.sizeX];
            try {
                for(int y = 0; y < map.sizeY; y++) {
                    String xMapping = stringMapping[y];
                    if(xMapping == null) {
                        map.mapping[y] = null;
                    } else {
                        String[] xTiles = xMapping.split(",");
                        for(int x = 0; x < map.sizeX; x++) {
                            if(xTiles[x] == null || xTiles[x].equalsIgnoreCase("-")) {
                                map.mapping[y][x] = null;
                            } else {
                                String[] tile = xTiles[x].split(":");
                                int tilemap = Integer.parseInt(tile[0]);
                                int spriteIndex = Integer.parseInt(tile[1]);
                                map.mapping[y][x] = map.tilemaps[tilemap].getReference(map).getSprite(spriteIndex);
                            }
                        }
                    }
                }
            } catch(Exception e) {
                Undertailor.instance.error("roomman", "failed to load room mapping: bad map data");
                return null;
            }
            
            return map;
        }
        
        private int sizeX;
        private int sizeY;
        private Sprite[][] mapping;
        private TilemapWrapper[] tilemaps;
        
        public void render() {
            for(int y = 0; y < sizeY; y++) {
                for(int x = 0; x < sizeX; x++) {
                    Sprite tile = mapping[y][x];
                    if(tile != null) {
                        float xPos = x * 20F;
                        float yPos = y * 20F;
                        tile.draw(xPos, yPos, 1F, 1F, 0F, false, false, 20, 20, true);
                    }
                }
            }
        }
        
        public int getSizeX() {
            return sizeX;
        }
        
        public int getSizeY() {
            return sizeY;
        }
        
        @Override
        public void dispose() {
            for(TilemapWrapper wrapper : tilemaps) {
                wrapper.removeReference(this);
            }
        }
    }
    
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
                            WorldRoom room = new LuaWorldRoom(Undertailor.getRoomManager().getObject(targetRoom[0])).getRoom();
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
    
    private static final TreeSet<WorldObject> RETURN_SET;
    private static final Set<Collider> COLLIDER_SET;
    private static final Set<Collider> TARGET_SET;
    private static long nextId;
    
    static {
        RETURN_SET = new TreeSet<WorldObject>((WorldObject obj1, WorldObject obj2) -> {
            if(obj1.getZ() == obj2.getZ()) {
                return Float.compare(obj2.getPosition().y, obj1.getPosition().y);
            } else {
                return Integer.compare(obj1.getZ(), obj2.getZ());
            }
        });
        
        COLLIDER_SET = new HashSet<Collider>();
        TARGET_SET = new HashSet<Collider>();
        nextId = 0;
    }
    
    private String roomName;
    private RoomMap room;
    private RoomDataWrapper roomWrapper;
    private Map<String, Entrypoint> entrypoints;
    
    private Set<WorldObject> removed;
    private Map<Long, WorldObject> added;
    private Map<Long, WorldObject> objects;
    
    public WorldRoom(String id, RoomDataWrapper roomWrapper) {
        this.added = new HashMap<>();
        this.removed = new HashSet<>();
        this.entrypoints = new HashMap<>();
        this.objects = new HashMap<>();
        this.roomWrapper = roomWrapper;
        this.room = roomWrapper.getReference(this);
        this.roomName = id;
    }
    
    public String getRoomName() {
        return roomName;
    }
    
    public RoomMap getMap() {
        return room;
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
        room.render();
        for(WorldObject object : getObjectsInRenderOrder()) {
            object.render();
        }
        
        if(Undertailor.getOverworldController().isRenderingHitboxes()) {
            for(WorldObject object : getObjectsInRenderOrder()) {
                object.renderBox();
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
                obj.id = -1;
                obj.room = null;
                obj.onDestroy();
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
    
    private TreeSet<WorldObject> getObjectsInRenderOrder() {
        RETURN_SET.clear();
        for(WorldObject object : objects.values()) {
            RETURN_SET.add(object);
        }
        
        return RETURN_SET;
    }
    
    @Override
    public void dispose() {
        roomWrapper.removeReference(this);
    }
    
    public void onPause() {}
    public void onResume() {}
    public void onProcess(float delta, InputData input) {}
    public void onEnter(Entrypoint entrypoint) {}
    public void onExit(Entrypoint exitpoint) {}
}
