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

import me.scarlet.undertailor.engine.ui.UIComponent;
import me.scarlet.undertailor.engine.ui.UIObject;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link UIObject} objects.
 */
public class LuaUIObjectMeta implements LuaObjectMeta {

    private LuaTable metatable;

    public static LuaObjectValue<UIObject> convert(LuaValue value) {
        return Lua.checkType(value, LuaUIObjectMeta.class);
    }

    static UIObject obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    public LuaUIObjectMeta() {
        this.metatable = new LuaTable();

        // uiObject:isActive()
        set("isActive", asFunction(vargs -> {
            return valueOf(obj(vargs).isActive());
        }));

        // uiObject:getMaxLifetime()
        // Converts time unit to seconds.
        set("getMaxLifetime", asFunction(vargs -> {
            long millis = obj(vargs).getMaxLifetime();
            float seconds = millis / 1000.0F;
            return valueOf(seconds);
        }));

        // uiObject:setMaxLifetime(seconds)
        // Converts passed time unit to milliseconds.
        set("setMaxLifetime", asFunction(vargs -> {
            float seconds = vargs.checknumber(2).tofloat();
            long millis = (long) (seconds * 1000L);
            obj(vargs).setMaxLifetime(millis);
            return NIL;
        }));

        // uiObject:isPastLifetime()
        set("isPastLifetime", asFunction(vargs -> {
            return valueOf(obj(vargs).isPastLifetime());
        }));

        // uiObject:getLifetime()
        // Converts time unit to seconds.
        set("getLifetime", asFunction(vargs -> {
            return valueOf(obj(vargs).getLifetime() / 1000.0F);
        }));

        // uiObject:resetLifetime()
        set("resetLifetime", asFunction(vargs -> {
            obj(vargs).resetLifetime();
            return NIL;
        }));

        // uiObject:registerComponent(component)
        set("registerComponent", asFunction(vargs -> {
            UIComponent component = LuaUIComponentMeta.convert(vargs.checknotnil(2)).getObject();
            obj(vargs).registerComponent(component);
            return NIL;
        }));

        // we're modular and can actually be safely reused without error
        // worldObject:remove()
        set("remove", asFunction(vargs -> {
            UIObject obj = obj(vargs);
            if (obj.getParent() != null) {
                return valueOf(obj.release(obj.getParent()));
            }

            return LuaValue.FALSE;
        }));
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return UIObject.class;
    }

    @Override
    public LuaTable getMetatable() {
        return metatable;
    }

    @Override
    public String getTypeName() {
        return "uiobject";
    }
}
