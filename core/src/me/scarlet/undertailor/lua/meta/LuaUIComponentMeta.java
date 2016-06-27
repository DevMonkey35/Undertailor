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

import com.badlogic.gdx.math.Vector2;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.ui.UIComponent;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link UIComponent} objects.
 */
public class LuaUIComponentMeta implements LuaObjectMeta {

    private LuaTable metatable;

    public static LuaObjectValue<UIComponent> convert(LuaValue value) {
        return Lua.checkType(value, LuaUIComponentMeta.class);
    }

    static UIComponent obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    public LuaUIComponentMeta() {
        this.metatable = new LuaTable();

        // component:getParent()
        set("getParent", asFunction(vargs -> {
            return orNil(obj(vargs).getParent());
        }));

        // component:getScreenPosition()
        set("getScreenPosition", asFunction(vargs -> {
            Vector2 pos = obj(vargs).getScreenPosition();
            return varargsOf(valueOf(pos.x), valueOf(pos.y));
        }));
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public void postMetaInit(LuaTable metatable) {
        metatable.set("process", NIL); // Processable
        metatable.set("catchEvent", NIL); // EventListener
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return UIComponent.class;
    }

    @Override
    public LuaTable getMetatable() {
        return metatable;
    }

    @Override
    public String getTypeName() {
        return "UIComponent";
    }
}
