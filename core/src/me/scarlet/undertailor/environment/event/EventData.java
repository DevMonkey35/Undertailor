package me.scarlet.undertailor.environment.event;

import org.luaj.vm2.LuaTable;

public interface EventData {
    
    String getName();
    Object get(String key);
    default LuaTable asLuaTable() { return null; }
    
}
