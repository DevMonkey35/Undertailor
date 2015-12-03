package me.scarlet.undertailor.lua.lib.game;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class ControlLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable control = new LuaTable();
        
        env.set("control", control);
        return control;
    }
    
    
}
