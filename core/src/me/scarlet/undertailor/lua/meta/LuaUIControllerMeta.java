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

import me.scarlet.undertailor.engine.ui.UIController;
import me.scarlet.undertailor.engine.ui.UIObject;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link UIController} objects.
 */
public class LuaUIControllerMeta implements LuaObjectMeta {

    private LuaTable metatable;

    public static LuaObjectValue<UIController> convert(LuaValue value) {
        return Lua.checkType(value, LuaUIControllerMeta.class);
    }

    static UIController obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    public LuaUIControllerMeta() {
        this.metatable = new LuaTable();

        // controller:getUIObject(id)
        set("getUIObject", asFunction(vargs -> {
            return orNil(obj(vargs).getUIObject(vargs.checklong(2)));
        }));

        // controller:registerUIObject(object)
        set("registerUIObject", asFunction(vargs -> {
            UIObject object = LuaUIObjectMeta.convert(vargs.checknotnil(2)).getObject();
            return valueOf(obj(vargs).registerUIObject(object));
        }));

        // controller:removeUIObject(id)
        // controller:removeUIObject(obj)
        set("removeUIObject", asFunction(vargs -> {
            if (vargs.isnumber(2)) { // gave an id
                obj(vargs).removeUIObject(vargs.checklong(2));
            } else { // gave an object
                UIObject obj = LuaUIObjectMeta.convert(vargs.checknotnil(2)).getObject();
                obj(vargs).removeUIObject(obj);
            }

            return NIL;
        }));
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return UIController.class;
    }

    @Override
    public LuaTable getMetatable() {
        return metatable;
    }

    @Override
    public String getTypeName() {
        return "uicontroller";
    }
}
