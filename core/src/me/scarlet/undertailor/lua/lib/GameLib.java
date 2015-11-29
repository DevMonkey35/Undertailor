package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.Gdx;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class GameLib extends TwoArgFunction {
    
    public static final String DEFAULT_TITLE = "UNDERTAilor";
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable game = new LuaTable();
        game.set("setWindowTitle", new _setWindowTitle());
        new AudioLib().call(LuaValue.valueOf(""), game);
        
        env.set("game", game);
        return game;
    }
    
    static class _setWindowTitle extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String title;
            if(arg.isnil()) {
                title = DEFAULT_TITLE;
            } else {
                title = arg.checkjstring();
            }
            
            Gdx.graphics.setTitle(title);
            return LuaValue.NIL;
        }
    }
}
