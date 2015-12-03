package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaValue;

public class LuaFont extends LuaValue {
    
    public static final String TYPENAME = "tailor-font";
    public static LuaValue METATABLE;
    
    @Override
    public int type() {
        return LuaValue.TVALUE;
    }
    
    @Override
    public String typename() {
        return TYPENAME;
    }
    
    @Override
    public LuaValue getmetatable() {
        return METATABLE;
    }
}
