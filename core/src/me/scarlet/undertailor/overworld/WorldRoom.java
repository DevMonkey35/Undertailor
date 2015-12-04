package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Sprite;
import me.scarlet.undertailor.manager.RoomManager;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.util.InputRetriever.InputData;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import me.scarlet.undertailor.wrappers.TilemapWrapper;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                TilemapWrapper wrapper = Undertailor.getTilemapManager().getTilemap(tilemapNames[i]);
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
        
        public void dispose() {
            for(TilemapWrapper wrapper : tilemaps) {
                wrapper.removeReference(this);
            }
        }
    }
    
    public static class Entrypoint extends Rectangle {
        
        private static final long serialVersionUID = -912415115458258327L;
        
    }
    
    private static TreeSet<WorldObject> returnSet;
    
    static {
        returnSet = new TreeSet<WorldObject>((WorldObject obj1, WorldObject obj2) -> {
            if(obj1.getZ() == obj2.getZ()) {
                return Float.compare(obj1.getPosition().y, obj2.getPosition().y);
            } else {
                return Integer.compare(obj1.getZ(), obj2.getZ());
            }
        });
    }
    
    private String id;
    private RoomMap room;
    private RoomDataWrapper roomWrapper;
    private int nextObject;
    private List<Rectangle> entrypoints;
    private Map<Integer, WorldObject> objects;
    
    public WorldRoom(String id, RoomDataWrapper roomWrapper) {
        this.entrypoints = new ArrayList<>();
        this.objects = new HashMap<>();
        this.nextObject = 0;
        this.roomWrapper = roomWrapper;
        this.room = roomWrapper.getReference(this);
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void registerEntrypoint(Rectangle rect) {
        entrypoints.add(rect);
    }
    
    public int registerObject(WorldObject object) {
        int id = nextObject++;
        object.setRoom(this);
        objects.put(id, object);
        return id;
    }
    
    public WorldObject getObject(int id) {
        return objects.get(id);
    }
    
    public void process(float delta, InputData input) {
        for(WorldObject object : objects.values()) {
            object.process(delta, input);
        }
    }
    
    public void render() {
        room.render();
        boolean boxes = Undertailor.getOverworldController().isRenderingHitboxes();
        for(WorldObject object : getObjectsInRenderOrder()) {
            object.render();
            if(boxes) {
                Undertailor.getRenderer().setShapeColor(Color.WHITE);
                object.renderBox();
            }
        }
    }
    
    private TreeSet<WorldObject> getObjectsInRenderOrder() {
        returnSet.clear();
        for(WorldObject object : objects.values()) {
            returnSet.add(object);
        }
        
        return returnSet;
    }
    
    public void dispose() {
        roomWrapper.removeReference(this);
    }
}
