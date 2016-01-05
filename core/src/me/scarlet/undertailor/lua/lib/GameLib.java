package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.Gdx;
import me.scarlet.undertailor.Undertailor;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;
import me.scarlet.undertailor.lua.LuaLibraryComponent;
import me.scarlet.undertailor.util.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class GameLib extends LuaLibrary {
    
    public static final String DEFAULT_TITLE = "UNDERTAILOR";
    public static boolean defset = false;
    
    public static final LuaLibraryComponent[] COMPONENTS = {
            new setWindowTitle(),
            new setFrameCap(),
            
            Lua.LIB_UI,
            Lua.LIB_AUDIO,
            Lua.LIB_LOGGER,
            Lua.LIB_GRAPHICS,
            Lua.LIB_OVERWORLD,
            Lua.LIB_ANIMATION
    };
    
    public GameLib() {
        super("game", COMPONENTS);
    }
    
    @Override
    public void postinit(LuaValue env, LuaValue game) {
        if(!defset) {
            Gdx.graphics.setTitle(DEFAULT_TITLE);
            defset = true;
        }
    }
    
    static class setWindowTitle extends LibraryFunction {
        @Override
        public Varargs execute(Varargs args) {
            LuaUtil.checkArguments(args, 0, 1);
            Gdx.graphics.setTitle(args.optjstring(1, DEFAULT_TITLE));
            return LuaValue.NIL;
        }
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
