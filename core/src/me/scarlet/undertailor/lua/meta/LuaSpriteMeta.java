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
import static org.luaj.vm2.LuaValue.varargsOf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.gfx.Transform;
import me.scarlet.undertailor.gfx.spritesheet.Sprite;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Sprite} objects.
 */
public class LuaSpriteMeta implements LuaObjectMeta {

    public static LuaObjectValue<Sprite> convert(LuaValue value) {
        return Lua.checkType(value, LuaSpriteMeta.class);
    }

    static Sprite obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaSpriteMeta() {
        this.metatable = new LuaTable();

        // sprite:getTransform()
        set("getTransform", asFunction(vargs -> {
            return orNil(obj(vargs).getTransform());
        }));

        // sprite:setTransform(transform)
        set("setTransform", asFunction(vargs -> {
            obj(vargs).setTransform(
                Lua.<Transform>checkType(vargs.checknotnil(2), LuaTransformMeta.class).getObject());
            return NIL;
        }));

        // sprite:clone()
        set("clone", asFunction(vargs -> {
            return orNil(obj(vargs).clone());
        }));

        // sprite:getSpriteSize();
        set("getSpriteSize", asFunction(vargs -> {
            TextureRegion region = obj(vargs).getTextureRegion();
            return varargsOf(valueOf(region.getRegionWidth()), valueOf(region.getRegionHeight()));
        }));

        // sprite:render([x, y, transform])
        set("render", asFunction(vargs -> {
            if (vargs.narg() == 0) {
                obj(vargs).render();
                return NIL;
            }

            float x = (float) vargs.optdouble(2, 0);
            float y = (float) vargs.optdouble(3, 0);
            Transform transform = vargs.isnil(4) ? null
                : Lua.<Transform>checkType(vargs.arg(4), LuaTransformMeta.class).getObject();
            if(transform != null) {
                obj(vargs).render(x, y, transform);
            } else {
                obj(vargs).render(x, y);
            }

            return NIL;
        }));
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Sprite.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "sprite";
    }
}
