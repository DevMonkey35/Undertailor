package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.valueOf;
import static org.luaj.vm2.LuaValue.NIL;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.audio.SoundFactory.Sound;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

public class LuaSoundMeta implements LuaObjectMeta {

    static LuaObjectValue<Sound> convert(LuaValue value) {
        return Lua.checkType(value, LuaSoundMeta.class);
    }

    static Sound obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaSoundMeta() {
        this.metatable = new LuaTable();

        // sound:loop([vol, pitch, pan])
        set("loop", asFunction(vargs -> {
            float vol = vargs.optnumber(2, valueOf(1)).tofloat();
            float pitch = vargs.optnumber(3, valueOf(1)).tofloat();
            float pan = vargs.optnumber(4, valueOf(0)).tofloat();
            return of(obj(vargs).loop(vol, pitch, pan));
        }));

        // sound:play([vol, pitch, pan])
        set("play", asFunction(vargs -> {
            float vol = vargs.optnumber(2, valueOf(1)).tofloat();
            float pitch = vargs.optnumber(3, valueOf(1)).tofloat();
            float pan = vargs.optnumber(4, valueOf(0)).tofloat();
            return of(obj(vargs).play(vol, pitch, pan));
        }));

        // sound:stop()
        set("stop", asFunction(vargs -> {
            obj(vargs).stop();
            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Sound.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "sound";
    }
}
