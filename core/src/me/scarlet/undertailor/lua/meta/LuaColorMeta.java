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

import static me.scarlet.undertailor.lua.LuaObjectValue.of;
import static me.scarlet.undertailor.util.LuaUtil.asFunction;
import static org.luaj.vm2.LuaValue.valueOf;

import com.badlogic.gdx.graphics.Color;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Color} objects.
 */
public class LuaColorMeta implements LuaObjectMeta {

    static LuaObjectValue<Color> convert(LuaValue value) {
        return Lua.checkType(value, LuaColorMeta.class);
    }

    static Color obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaColorMeta() {
        this.metatable = new LuaTable();

        // color:add(color)
        // color:add(r, g, b)
        set("add", asFunction(vargs -> {
            Color color = obj(vargs);
            if(vargs.arg(2).isnumber()) {
                float r = vargs.optint(2, 0) / 255F;
                float g = vargs.optint(3, 0) / 255F;
                float b = vargs.optint(4, 0) / 255F;
                return of(color.add(r, g, b, 0));
            } else {
                Color added = convert(vargs.arg(2)).getObject();
                return of(color.add(added));
            }
        }));

        // color:multiply(color)
        // color:multiply(r, g, b)
        set("multiply", asFunction(vargs -> {
            Color color = obj(vargs);
            if(vargs.arg(2).isnumber()) {
                float r = vargs.optint(2, 0) / 255F;
                float g = vargs.optint(3, 0) / 255F;
                float b = vargs.optint(4, 0) / 255F;
                return of(color.mul(r, g, b, 0));
            } else {
                Color multiplier = convert(vargs.arg(2)).getObject();
                return of(color.mul(multiplier));
            }
        }));

        // color:subtract(color)
        // color:subtract(r, g, b)
        set("subtract", asFunction(vargs -> {
            Color color = obj(vargs);
            if(vargs.arg(2).isnumber()) {
                float r = vargs.optint(2, 0) / 255F;
                float g = vargs.optint(3, 0) / 255F;
                float b = vargs.optint(4, 0) / 255F;
                return of(color.sub(r, g, b, 0));
            } else {
                Color subtracted = convert(vargs.arg(2)).getObject();
                return of(color.sub(subtracted));
            }
        }));

        // color:set(color)
        // color:set(r, g, b)
        set("set", asFunction(vargs -> {
            Color color = obj(vargs);
            if(vargs.arg(2).isnumber()) {
                float r = vargs.optint(2, 0) / 255F;
                float g = vargs.optint(3, 0) / 255F;
                float b = vargs.optint(4, 0) / 255F;
                return of(color.set(r, g, b, 1));
            } else {
                Color set = convert(vargs.arg(2)).getObject();
                return of(color.set(set));
            }
        }));

        // color:clone()
        set("clone", asFunction(vargs -> {
            return of(obj(vargs).cpy());
        }));

        // color:toHex()
        set("toHex", asFunction(vargs -> {
            return valueOf(obj(vargs).toString());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Color.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "gdx-color";
    }
}
