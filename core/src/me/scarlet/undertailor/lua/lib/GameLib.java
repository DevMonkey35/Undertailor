package me.scarlet.undertailor.lua.lib;

import com.badlogic.gdx.Gdx;
import me.scarlet.undertailor.lua.lib.game.AnimationLib;
import me.scarlet.undertailor.lua.lib.game.AudioLib;
import me.scarlet.undertailor.lua.lib.game.ControlLib;
import me.scarlet.undertailor.lua.lib.game.GraphicsLib;
import me.scarlet.undertailor.lua.lib.game.LoggerLib;
import me.scarlet.undertailor.lua.lib.game.OverworldLib;
import me.scarlet.undertailor.lua.lib.game.UILib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class GameLib extends TwoArgFunction {
    
    public static final String DEFAULT_TITLE = "UNDERTAILOR";
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable game = new LuaTable();
        game.set("setWindowTitle", new _setWindowTitle());
        new UILib().call(LuaValue.valueOf(""), game);
        new AudioLib().call(LuaValue.valueOf(""), game);
        new LoggerLib().call(LuaValue.valueOf(""), game);
        new ControlLib().call(LuaValue.valueOf(""), game);
        new GraphicsLib().call(LuaValue.valueOf(""), game);
        new OverworldLib().call(LuaValue.valueOf(""), game);
        new AnimationLib().call(LuaValue.valueOf(""), game);
        
        Gdx.graphics.setTitle(DEFAULT_TITLE);
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
