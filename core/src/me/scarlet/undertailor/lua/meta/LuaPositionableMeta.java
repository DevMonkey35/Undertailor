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
import static me.scarlet.undertailor.util.LuaUtil.varargsOf;
import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.valueOf;

import com.badlogic.gdx.math.Vector2;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.Positionable;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Positionable} objects.
 */
public class LuaPositionableMeta implements LuaObjectMeta {

    public static LuaObjectValue<Positionable> convert(LuaValue value) {
        return Lua.checkType(value, LuaPositionableMeta.class);
    }

    static Positionable obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }
    
    private LuaTable metatable;
    
    public LuaPositionableMeta() {
        this.metatable = new LuaTable();

        // positionable:getPosition()
        set("getPosition", asFunction(vargs -> {
            Vector2 position = obj(vargs).getPosition();
            return varargsOf(valueOf(position.x), valueOf(position.y));
        }));

        // positionable:setPosition(x[, y)
        set("setPosition", asFunction(vargs -> {
            Positionable obj = obj(vargs);
            float x = vargs.checknumber(2).tofloat();
            float y = vargs.isnil(3) ? obj.getPosition().y : vargs.checknumber(3).tofloat();
            obj.setPosition(x, y);
            return NIL;
        }));

        // positionable:getHeight()
        set("getHeight", asFunction(vargs -> {
            return valueOf(obj(vargs).getHeight());
        }));

        // positionable:setHeight(height)
        set("setHeight", asFunction(vargs -> {
            float height = vargs.checknumber(2).tofloat();
            obj(vargs).setHeight(height);
            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Positionable.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "positionable";
    }
}