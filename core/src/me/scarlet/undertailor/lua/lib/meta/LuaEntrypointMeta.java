package me.scarlet.undertailor.lua.lib.meta;

import com.badlogic.gdx.math.Vector2;
import me.scarlet.undertailor.lua.LuaBoundingBox;
import me.scarlet.undertailor.lua.LuaEntrypoint;
import me.scarlet.undertailor.overworld.WorldRoom.Entrypoint;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaEntrypointMeta extends LuaTable {
    
    public static void prepareMetatable() {
        if(LuaEntrypoint.METATABLE == null) {
            LuaEntrypoint.METATABLE = LuaValue.tableOf(new LuaValue[] { INDEX, new LuaEntrypointMeta() });
        }
    }
    
    public LuaEntrypointMeta() {
        this.set("getBoundingBox", new _getBoundingBox());
        this.set("getSpawnPosition", new _getSpawnPosition());
        this.set("setSpawnPosition", new _setSpawnPosition());
        this.set("getRoomTarget", new _getRoomTarget());
        this.set("setRoomTarget", new _setRoomTarget());
        this.set("getPosition", new _getPosition());
        this.set("setPosition", new _setPosition());
    }
    
    static class _getSpawnPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(args.arg(1)).getEntrypoint();
            Vector2 spawnpos = entrypoint.getSpawnPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(spawnpos.x),
                    LuaValue.valueOf(spawnpos.y)
            });
        }
    }
    
    static class _setSpawnPosition extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(arg1).getEntrypoint();
            Vector2 spawnpos = entrypoint.getSpawnPosition();
            float x = arg2.isnil() ? spawnpos.x : new Float(arg2.checkdouble());
            float y = arg3.isnil() ? spawnpos.x : new Float(arg3.checkdouble());
            
            entrypoint.setSpawnPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class _getPosition extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(args.arg(1)).getEntrypoint();
            Vector2 pos = entrypoint.getPosition();
            return LuaValue.varargsOf(new LuaValue[] {
                    LuaValue.valueOf(pos.x),
                    LuaValue.valueOf(pos.y)
            });
        }
    }
    
    static class _setPosition extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(arg1).getEntrypoint();
            Vector2 pos = entrypoint.getPosition();
            float x = arg2.isnil() ? pos.x : new Float(arg2.checkdouble());
            float y = arg3.isnil() ? pos.x : new Float(arg3.checkdouble());
            
            entrypoint.setPosition(x, y);
            return LuaValue.NIL;
        }
    }
    
    static class _getRoomTarget extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(arg).getEntrypoint();
            
            return LuaValue.valueOf(entrypoint.getRoomTarget());
        }
    }
    
    static class _setRoomTarget extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(arg1).getEntrypoint();
            String target = arg2.checkjstring();
            
            entrypoint.setRoomTarget(target);
            return LuaValue.NIL;
        }
    }
    
    static class _getBoundingBox extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Entrypoint entrypoint = LuaEntrypoint.checkEntrypoint(arg).getEntrypoint();
            
            return new LuaBoundingBox(entrypoint.getBoundingBox());
        }
    }
}
