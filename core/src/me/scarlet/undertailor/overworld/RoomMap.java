package me.scarlet.undertailor.overworld;

import com.badlogic.gdx.utils.Disposable;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.gfx.Sprite;
import me.scarlet.undertailor.manager.RoomManager;
import me.scarlet.undertailor.util.ConfigurateUtil;
import me.scarlet.undertailor.wrappers.TilemapWrapper;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public class RoomMap implements Disposable {
    
    public static RoomMap fromConfig(ConfigurationNode node) {
        RoomMap map = new RoomMap();
        map.sizeX = ConfigurateUtil.processInt(node.getNode("sizeX"), null);
        map.sizeY = ConfigurateUtil.processInt(node.getNode("sizeY"), null);
        String[] tilemapNames = ConfigurateUtil.processStringArray(node.getNode("tilemaps"), null);
        String[] stringMapping = ConfigurateUtil.processStringArray(node.getNode("mapping"), null);
        
        map.tilemaps = new TilemapWrapper[tilemapNames.length];
        for(int i = 0; i < map.tilemaps.length; i++) {
            TilemapWrapper wrapper = Undertailor.getTilemapManager().getStyle(tilemapNames[i]);
            if(wrapper == null) {
                Undertailor.instance.error(RoomManager.MANAGER_TAG, "failed to load room: map data referenced non-existing tilemap (" + tilemapNames[i] + ")");
                return null;
            }
            
            map.tilemaps[i] = wrapper;
        }
        
        map.mapping = new TileData[map.sizeY][map.sizeX];
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
                            String[] data = xTiles[x].split("-");
                            String[] tile = data[0].split(":");
                            int tilemap = Integer.parseInt(tile[0]);
                            int spriteIndex = Integer.parseInt(tile[1]);
                            map.mapping[y][x] = new TileData(map.tilemaps[tilemap].getReference(map).getSprite(spriteIndex), data);
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
    
    public static class TileData {
        
        public static class Key {
            
            public static Object parse(int type, String value) {
                if(type == 0) {
                    if(value.equalsIgnoreCase("true")) {
                        return true;
                    }
                    
                    return false;
                }
                
                return null;
            }
            
            private int type; // 0 = bool
            private String identifier;
            
            public Key(String identifier, int type) {
                this.type = type;
                this.identifier = identifier;
            }
        }
        
        public static final Key KEY_WALKABLE = new Key("tailor-walkable", 0);
        
        public static final Map<Key, Object> defaultMap;
        public static final Key[] KEYS = {
                KEY_WALKABLE
        };
        
        static {
            defaultMap = new HashMap<>();
            defaultMap.put(KEY_WALKABLE, true);
        }
        
        private Sprite tile;
        private Map<Key, Object> dataMap;
        
        public TileData(Sprite tile, String[] data) {
            this.tile = tile;
            this.dataMap = new HashMap<>(defaultMap);
            
            for(String dat : data) {
                if(dat.contains(":")) {
                    continue; // ignore sprite mapping
                }
                
                String[] split = dat.split("=");
                if(split.length < 2) {
                    continue;
                }
                
                for(Key key : KEYS) {
                    if(split[0].equals(key.identifier)) {
                        dataMap.put(key, Key.parse(key.type, split[1]));
                    }
                }
            }
            
            System.out.println(dataMap.get(KEY_WALKABLE).toString());
        }
        
        public boolean isWalkable() {
            return (boolean) dataMap.get(KEY_WALKABLE);
        }
        
        public void setWalkable(boolean flag) {
            dataMap.put(KEY_WALKABLE, flag);
        }
    }
    
    private int sizeX;
    private int sizeY;
    private TileData[][] mapping;
    private TilemapWrapper[] tilemaps;
    
    public void render() {
        for(int y = 0; y < sizeY; y++) {
            for(int x = 0; x < sizeX; x++) {
                Sprite tile = mapping[y][x].tile;
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
    
    public TileData getTileAt(int x, int y) {
        return mapping[y][x];
    }
    
    @Override
    public void dispose() {
        for(TilemapWrapper wrapper : tilemaps) {
            wrapper.removeReference(this);
        }
    }
}
