package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.util.LuaUtil;
import me.scarlet.undertailor.wrappers.RoomDataWrapper;
import org.luaj.vm2.LuaValue;

public class LuaRoomMapMeta extends LuaLibrary {

    public static LuaObjectValue<RoomDataWrapper> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_WORLDMAP);
    }
    
    public static LuaObjectValue<RoomDataWrapper> create(RoomDataWrapper room) {
        return LuaObjectValue.of(room, Lua.TYPENAME_WORLDMAP, Lua.META_WORLDMAP);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            
    };
    
    public LuaRoomMapMeta() {
        super(null, COMPONENTS);
    }
}
