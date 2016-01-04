package me.scarlet.undertailor.lua.lib.meta;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.overworld.WorldObject;
import me.scarlet.undertailor.overworld.WorldRoom;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaWorldRoomMeta extends LuaLibrary {
    
    public static LuaObjectValue<WorldRoom> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_WORLDROOM);
    }
    
    public static LuaObjectValue<WorldRoom> create(WorldRoom room) {
        return LuaObjectValue.of(room, Lua.TYPENAME_WORLDROOM, Lua.META_WORLDROOM);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new getRoomName(),
            new registerObject(),
            new getObject(),
            new removeObject(),
            new newEntrypoint(),
            new registerEntrypoint()
    };
    
    public LuaWorldRoomMeta() {
        super(null, COMPONENTS);
    }
    
    static class getRoomName extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            WorldRoom room = check(args.arg1()).getObject();
            return LuaValue.valueOf(room.getRoomName());
        }
    }
    
    static class registerObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            WorldObject object = LuaWorldObjectMeta.check(args.arg(2)).getObject();
            return LuaValue.valueOf(room.registerObject(object));
        }
    }
    
    static class getObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            int id = args.checkint(2);
            if(room.getObject(id) != null) {
                return LuaWorldObjectMeta.create(room.getObject(id));
            } else {
                return LuaValue.NIL;
            }
        }
    }
    
    static class registerEntrypoint extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 3, 3);
            
            WorldRoom room = check(args.arg1()).getObject();
            String id = args.checkjstring(2);
            Entrypoint entrypoint = LuaEntrypointMeta.check(args.arg(3)).getObject();
            
            room.registerEntrypoint(id, entrypoint);
            return LuaValue.NIL;
        }
    }
    
    static class removeObject extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            WorldRoom room = check(args.arg1()).getObject();
            int id = args.checkint(2);
            room.removeObject(id);
            return LuaValue.NIL;
        }
    }
    
    static class newEntrypoint extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 0);
            
            return LuaEntrypointMeta.create(new Entrypoint());
        }
    }
    
    // TODO map data access
}
