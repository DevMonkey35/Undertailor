/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Tellerva, Marc Lawrence
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.scarlet.undertailor.lua.meta;

import static me.scarlet.undertailor.lua.LuaObjectValue.orNil;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.audio.AudioPlayable;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link AudioPlayable} objects.
 */
public class LuaAudioPlayableMeta implements LuaObjectMeta {

    public static LuaObjectValue<AudioPlayable<?>> convert(LuaValue value) {
        return Lua.checkType(value, LuaAudioPlayableMeta.class);
    }

    static AudioPlayable<?> obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaAudioPlayableMeta() {
        this.metatable = new LuaTable();

        // playable:loop([vol, pitch, pan])
        set("loop", asFunction(vargs -> {
            float vol = vargs.optnumber(2, valueOf(1)).tofloat();
            float pitch = vargs.optnumber(3, valueOf(1)).tofloat();
            float pan = vargs.optnumber(4, valueOf(0)).tofloat();
            return orNil(obj(vargs).loop(vol, pitch, pan));
        }));

        // playable:play([vol, pitch, pan])
        set("play", asFunction(vargs -> {
            float vol = vargs.optnumber(2, valueOf(1)).tofloat();
            float pitch = vargs.optnumber(3, valueOf(1)).tofloat();
            float pan = vargs.optnumber(4, valueOf(0)).tofloat();
            return orNil(obj(vargs).play(vol, pitch, pan));
        }));

        // playable:stop()
        set("stop", asFunction(vargs -> {
            obj(vargs).stop();
            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return AudioPlayable.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "audioplayable";
    }

}
