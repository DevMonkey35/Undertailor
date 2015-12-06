package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.LuaWorldObject;
import me.scarlet.undertailor.lua.LuaWorldRoom;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.overworld.WorldRoom;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaWorldRoomMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaWorldRoom.METATABLE == null) {
            LuaWorldRoom.METATABLE = LuaValue.tableOf(new LuaValue[] {INDEX, new LuaWorldRoomMeta()});
        }
    }
    
    public LuaWorldRoomMeta() {
        this.set("getRoomName", new _getRoomName());
        this.set("registerObject", new _registerObject());
        this.set("getObject", new _getObject());
        this.set("removeObject", new _removeObject());
    }
    
    static class _getRoomName extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            WorldRoom room = LuaWorldRoom.checkWorldRoom(arg).getRoom();
            return LuaValue.valueOf(room.getRoomName());
        }
    }
    
    static class _registerObject extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldRoom room = LuaWorldRoom.checkWorldRoom(arg1).getRoom();
            WorldObject object = LuaWorldObject.checkWorldObject(arg2).getWorldObject();
            return LuaValue.valueOf(room.registerObject(object));
        }
    }
    
    static class _getObject extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldRoom room = LuaWorldRoom.checkWorldRoom(arg1).getRoom();
            int id = arg2.checkint();
            if(room.getObject(id) != null) {
                return new LuaWorldObject(room.getObject(id));
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    static class _removeObject extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            WorldRoom room = LuaWorldRoom.checkWorldRoom(arg1).getRoom();
            int id = arg2.checkint();
            room.removeObject(id);
            return LuaValue.NIL;
        }
    }
    
    // TODO map data access
}
