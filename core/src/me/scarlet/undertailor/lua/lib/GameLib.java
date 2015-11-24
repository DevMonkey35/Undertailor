package me.scarlet.undertailor.lua.lib;

import me.scarlet.undertailor.lua.lib.game.AudioLib;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class GameLib extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable game = new LuaTable();
        new AudioLib().call(LuaValue.valueOf(""), game);
        
        env.set("game", game);
        return game;
    }
}
