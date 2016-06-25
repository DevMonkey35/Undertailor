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

import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.audio.AudioData;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link AudioData} objects.
 */
public class LuaAudioDataMeta implements LuaObjectMeta {

    public static LuaObjectValue<AudioData> convert(LuaValue value) {
        return Lua.checkType(value, LuaAudioDataMeta.class);
    }

    static AudioData obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }
    
    private LuaTable metatable;
    
    public LuaAudioDataMeta() {
        this.metatable = new LuaTable();

        // data:getVolume()
        set("getVolume", asFunction(vargs -> {
            return valueOf(obj(vargs).getVolume());
        }));

        // data:setVolume(nVolume)
        set("setVolume", asFunction(vargs -> {
            obj(vargs).setVolume(vargs.arg(2).checknumber().tofloat());
            return NIL;
        }));

        // data:getPitch()
        set("getPitch", asFunction(vargs -> {
            return valueOf(obj(vargs).getPitch());
        }));

        // data:setPitch(nPitch)
        set("setPitch", asFunction(vargs -> {
            obj(vargs).setPitch(vargs.arg(2).checknumber().tofloat());
            return NIL;
        }));

        // data:getPan()
        set("getPan", asFunction(vargs -> {
            return valueOf(obj(vargs).getPan());
        }));

        // data:setPan(nPan)
        set("setPan", asFunction(vargs -> {
            obj(vargs).setPan(vargs.arg(2).checknumber().tofloat());
            return NIL;
        }));

        // data:isLooping()
        set("isLooping", asFunction(vargs -> {
            return valueOf(obj(vargs).isLooping());
        }));

        // data:setLooping(bLooping)
        set("setLooping", asFunction(vargs -> {
            obj(vargs).setLooping(vargs.arg(2).checkboolean());
            return NIL;
        }));

        // data:isPlaying()
        set("isPlaying", asFunction(vargs -> {
            valueOf(obj(vargs).isLooping());
            return NIL;
        }));

        // data:stop()
        set("stop", asFunction(vargs -> {
            obj(vargs).stop();
            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return AudioData.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "audiodata";
    }
}
