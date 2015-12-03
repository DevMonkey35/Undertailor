package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.Undertailor;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class DebugLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable debug = new LuaTable();
        debug.set("setFrameCap", new _setFrameCap());
        
        env.set("debug", debug);
        return debug;
    }
    
    static class _setFrameCap extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            Undertailor.setFrameCap(arg.checkint());
            return LuaValue.NIL;
        }
    }
}
