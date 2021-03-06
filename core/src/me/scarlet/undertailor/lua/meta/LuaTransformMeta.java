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

import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Transform} objects.
 */
public class LuaTransformMeta implements LuaObjectMeta {

    public static LuaObjectValue<Transform> convert(LuaValue value) {
        return Lua.checkType(value, LuaTransformMeta.class);
    }

    static Transform obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaTransformMeta() {
        this.metatable = new LuaTable();

        // ---------------- getters ----------------

        // transform:getScaleX()
        set("getScaleX", asFunction(vargs -> {
            return valueOf(obj(vargs).getScaleX());
        }));

        // transform:getScaleY()
        set("getScaleY", asFunction(vargs -> {
            return valueOf(obj(vargs).getScaleY());
        }));

        // transform:getFlipX()
        set("getFlipX", asFunction(vargs -> {
            return valueOf(obj(vargs).getFlipX());
        }));

        // transform:getFlipY()
        set("getFlipY", asFunction(vargs -> {
            return valueOf(obj(vargs).getFlipY());
        }));

        // transform:getRotation()
        set("getRotation", asFunction(vargs -> {
            return valueOf(obj(vargs).getRotation());
        }));

        // ---------------- setters ----------------

        // transform:setScale(scale)
        set("setScale", asFunction(vargs -> {
            obj(vargs).setScale(vargs.checknumber(2).tofloat());
            return NIL;
        }));

        // transform:setScaleX(scaleX)
        set("setScaleX", asFunction(vargs -> {
            obj(vargs).setScaleX(vargs.checknumber(2).tofloat());
            return NIL;
        }));

        // transform:setScaleY(scaleY)
        set("setScaleY", asFunction(vargs -> {
            obj(vargs).setScaleY(vargs.checknumber(2).tofloat());
            return NIL;
        }));

        // transform:setFlipX(flipX)
        set("setFlipX", asFunction(vargs -> {
            obj(vargs).setFlipX(vargs.checkboolean(2));
            return NIL;
        }));

        // transform:setFlipY(flipY)
        set("setFlipY", asFunction(vargs -> {
            obj(vargs).setFlipY(vargs.checkboolean(2));
            return NIL;
        }));

        // transform:setRotation(rotation)
        set("setRotation", asFunction(vargs -> {
            obj(vargs).setRotation(vargs.checknumber(2).tofloat());
            return NIL;
        }));

        // ---------------- other ----------------

        // transform:copyInto(transform)
        set("copyInto", asFunction(vargs -> {
            Transform other = convert(vargs.checknotnil(2)).getObject();
            return orNil(obj(vargs).copyInto(other));
        }));

        // transform:clone()
        set("clone", asFunction(vargs -> {
            return orNil(obj(vargs).clone());
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Transform.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "transform";
    }
}
