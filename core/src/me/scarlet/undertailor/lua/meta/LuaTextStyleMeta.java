package me.scarlet.undertailor.lua.meta;

import org.luaj.vm2.LuaTable;

import me.scarlet.undertailor.gfx.text.TextStyle;
import me.scarlet.undertailor.lua.LuaObjectMeta;

public class LuaTextStyleMeta implements LuaObjectMeta {

    @Override
    public Class<?> getTargetObjectClass() {
        return TextStyle.class;
    }

    @Override
    public LuaTable getMetatable() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "tlr-textstyle";
    }
}
