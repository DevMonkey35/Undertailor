package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class DebugLib extends LuaLibrary {
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new setFrameCap()
    };
    
    public DebugLib() {
        super("debug", COMPONENTS);
    }
    
    static class setFrameCap extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 1, 1);
            
            Undertailor.setFrameCap(args.checkint(1));
            return LuaValue.NIL;
        }
    }
}
