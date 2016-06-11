package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.Lua.checkType;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;

import me.scarlet.undertailor.audio.Audio;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

public class LuaAudioMeta implements LuaObjectMeta {

    private LuaTable metatable;

    public LuaAudioMeta() {
        this.metatable = new LuaTable();

        set("getAudioName", asFunction(vargs -> {
            LuaObjectValue<Audio> audio = checkType(vargs.arg1(), LuaAudioMeta.class);
            return valueOf(audio.getObject().getAudioName());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Audio.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "audio";
    }
}
