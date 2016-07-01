package me.scarlet.undertailor.lua.lib;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaLibrary;

/**
 * Meta library accessible through Lua.
 * 
 * <p>Contains all metatable libraries registered.</p>
 */
public class MetaLib extends LuaLibrary {

    public MetaLib() {
        super("meta");
    }

    @Override
    public void postinit(LuaTable table, LuaValue environment) {
        Lua.injectMetatables(table);
    }

}
