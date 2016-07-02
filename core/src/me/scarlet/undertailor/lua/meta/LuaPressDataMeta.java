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
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.input.PressData;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link PressData} objects.
 */
public class LuaPressDataMeta implements LuaObjectMeta {

    public static LuaObjectValue<PressData> convert(LuaValue value) {
        return Lua.checkType(value, LuaPressDataMeta.class);
    }

    static PressData obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaPressDataMeta() {
        this.metatable = new LuaTable();

        // time value converted to seconds
        // pressData:getLastReleaseTime()
        set("getLastReleaseTime", asFunction(vargs -> {
            return valueOf(obj(vargs).getLastReleaseTime() / 1000.0F);
        }));

        // pressData:justReleased(time)
        set("justReleased", asFunction(vargs -> {
            return valueOf(obj(vargs).justReleased((long) (vargs.checknumber(2).tofloat() * 1000.0F)));
        }));

        // time value converted to seconds
        // pressData:getLastPressTime()
        set("getLastPressTime", asFunction(vargs -> {
            return valueOf(obj(vargs).getLastPressTime() / 1000.0F);
        }));

        // pressData:justPressed(time)
        set("justPressed", asFunction(vargs -> {
            return valueOf(obj(vargs).justPressed((long) (vargs.checknumber(2).tofloat() * 1000.0F)));
        }));

        // time value converted to seconds
        // pressData:getHoldTime()
        set("getHoldTime", asFunction(vargs -> {
            return valueOf(obj(vargs).getHoldTime() / 1000.0F);
        }));

        // pressData:isPressed()
        set("isPressed", asFunction(vargs -> {
            return valueOf(obj(vargs).isPressed());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return PressData.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "pressdata";
    }
}
