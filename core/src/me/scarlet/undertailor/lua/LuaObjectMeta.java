package me.scarlet.undertailor.lua;

import org.luaj.vm2.LuaTable;

public interface LuaObjectMeta {

    public Class<?> getTargetObjectClass();

    public LuaTable getMetatable();

    public String getTypeName();

}
