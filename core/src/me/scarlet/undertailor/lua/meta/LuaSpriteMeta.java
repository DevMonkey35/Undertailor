package me.scarlet.undertailor.lua.meta;

import org.luaj.vm2.LuaTable;

import me.scarlet.undertailor.gfx.spritesheet.Sprite;
import me.scarlet.undertailor.lua.LuaObjectMeta;

public class LuaSpriteMeta implements LuaObjectMeta {

    @Override
    public Class<?> getTargetObjectClass() {
        return Sprite.class;
    }

    @Override
    public LuaTable getMetatable() {
        return null; // done by everything else; we don't wanna expose anything else either
    }

    @Override
    public String getTypeName() {
        return "sprite";
    }
}
