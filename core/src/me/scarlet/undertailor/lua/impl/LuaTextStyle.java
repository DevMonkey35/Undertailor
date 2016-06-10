package me.scarlet.undertailor.lua.impl;

import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;

import me.scarlet.undertailor.exception.LuaScriptException;
import me.scarlet.undertailor.gfx.text.TextStyle;
import me.scarlet.undertailor.lua.LuaImplementable;
import me.scarlet.undertailor.lua.LuaObjectValue;
import me.scarlet.undertailor.lua.ScriptManager;

import java.io.File;
import java.io.FileNotFoundException;

public class LuaTextStyle implements LuaImplementable<TextStyle>, TextStyle {

    public static final String FUNC_APPLY = "apply";

    private static final LuaTable DM_PROXY;

    static {
        DM_PROXY = new LuaTable();
    }

    private LuaObjectValue<TextStyle> luaObj;

    public LuaTextStyle(ScriptManager manager, File luaFile)
        throws FileNotFoundException, LuaScriptException {
        this.luaObj = new LuaObjectValue<TextStyle>(this);
        this.luaObj.load(manager, luaFile);
    }

    @Override
    public Class<?> getPrimaryIdentifyingClass() {
        return TextStyle.class;
    }

    @Override
    public LuaObjectValue<TextStyle> getObjectValue() {
        return this.luaObj;
    }

    @Override
    public void setObjectValue(LuaObjectValue<TextStyle> value) {
        this.luaObj = value;
    }

    @Override
    public void apply(DisplayMeta meta, long time, char character, int charIndex, int textLength) {
        DM_PROXY.set("offX", meta.offX);
        DM_PROXY.set("offY", meta.offY);
        DM_PROXY.set("scaleX", meta.scaleX);
        DM_PROXY.set("scaleY", meta.scaleY);
        this.invokeSelf(FUNC_APPLY, DM_PROXY, valueOf(time), valueOf(character), valueOf(charIndex),
            valueOf(textLength));
        meta.offX = DM_PROXY.get("offX").tofloat();
        meta.offY = DM_PROXY.get("offY").tofloat();
        meta.scaleX = DM_PROXY.get("scaleX").tofloat();
        meta.scaleY = DM_PROXY.get("scaleY").tofloat();
    }
}
