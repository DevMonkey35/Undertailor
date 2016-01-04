package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class LuaEntrypointMeta extends LuaLibrary {
    
    public static LuaObjectValue<Entrypoint> check(LuaValue value) {
        return LuaUtil.checkType(value, Lua.TYPENAME_ENTRYPOINT);
    }
    
    public static LuaObjectValue<Entrypoint> create(Entrypoint value) {
        return LuaObjectValue.of(value, Lua.TYPENAME_ENTRYPOINT, Lua.META_ENTRYPOINT);
    }
    
    public static final LuaLibraryComponent[] COMPONENTS = new LibraryFunction[] {
            new getBoundingBox(),
            new getSpawnPosition(),
            new setSpawnPosition(),
            new getRoomTarget(),
            new setRoomTarget(),
            new getPosition(),
            new setPosition()
    };
    
    public LuaEntrypointMeta() {
        super(null, COMPONENTS);
    }
    
    static class getSpawnPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg(1)).getObject();
            Vector2 spawnpos = entrypoint.getSpawnPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(spawnpos.x),
                    LuaValue.valueOf(spawnpos.y)
            });
        }
    }
    
    static class setSpawnPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            Vector2 spawnpos = entrypoint.getSpawnPosition();
            float x = new Float(args.optdouble(2, spawnpos.x));
            float y = new Float(args.optdouble(3, spawnpos.y));
            
            entrypoint.setSpawnPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg(1)).getObject();
            Vector2 pos = entrypoint.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)
            });
        }
    }
    
    static class setPosition extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 3);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            Vector2 pos = entrypoint.getPosition();
            float x = new Float(args.optdouble(2, pos.x));
            float y = new Float(args.optdouble(3, pos.y));
            
            entrypoint.setPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class getRoomTarget extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            return LuaValue.valueOf(entrypoint.getRoomTarget());
        }
    }
    
    static class setRoomTarget extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 2, 2);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            String target = args.checkjstring(2);
            
            entrypoint.setRoomTarget(target);
            return LuaValue.NIL;
        }
    }
    
    static class getBoundingBox extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = check(args.arg1()).getObject();
            return LuaBoundingBoxMeta.create(entrypoint.getBoundingBox());
        }
    }
}
