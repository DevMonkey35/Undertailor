package me.scarlet.undertailor.wrappers;

import me.scarlet.undertailor.overworld.WorldRoom.RoomMap;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.File;

public class RoomDataWrapper extends DisposableWrapper<RoomMap> {

    public static final long MAX_LIFETIME = 60000; // 1min
    
    private File roomScript;
    private ConfigurationNode mapData;
    public RoomDataWrapper(File roomScript, ConfigurationNode mapData) {
        super(null);
        this.mapData = mapData;
        this.roomScript = roomScript;
    }
    
    public File getRoomScript() {
        return roomScript;
    }
    
    @Override
    public RoomMap newReference() {
        return RoomMap.fromConfig(mapData);
    }
    
    @Override
    public boolean allowDispose() {
        if(!this.getReferrers().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public long getMaximumLifetime() {
        return MAX_LIFETIME;
    }
}
