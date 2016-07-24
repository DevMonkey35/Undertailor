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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.Renderable;
import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Renderable} objects.
 */
public class LuaRenderableMeta implements LuaObjectMeta {

    public static LuaObjectValue<Renderable> convert(LuaValue value) {
        return Lua.checkType(value, LuaRenderableMeta.class);
    }

    static Renderable obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaRenderableMeta() {
        this.metatable = new LuaTable();

        // renderable:getTransform()
        set("getTransform", asFunction(vargs -> {
            return orNil(obj(vargs).getTransform());
        }));

        // renderable:setTransform(transform)
        set("setTransform", asFunction(vargs -> {
            obj(vargs).setTransform(
                Lua.<Transform>checkType(vargs.checknotnil(2), LuaTransformMeta.class).getObject());
            return NIL;
        }));

        // renderable:draw([x, y, transform])
        set("draw", asFunction(vargs -> {
            if(vargs.narg() == 0) {
                obj(vargs).render();
                return NIL;
            }

            float x = vargs.checknumber(2).tofloat();
            float y = vargs.checknumber(3).tofloat();
            Transform transform = vargs.isnil(4) ? null
                : Lua.<Transform>checkType(vargs.arg(4), LuaTransformMeta.class).getObject();
            if (transform == null) {
                obj(vargs).render(x, y);
            } else {
                obj(vargs).render(x, y, transform);
            }

            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Renderable.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "renderable";
    }
}
