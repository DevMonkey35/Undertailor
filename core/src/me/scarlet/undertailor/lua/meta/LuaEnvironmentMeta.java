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
import static org.luaj.vm2.LuaValue.valueOf;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import me.scarlet.undertailor.engine.Environment;
import me.scarlet.undertailor.lua.Lua;
import me.scarlet.undertailor.lua.LuaObjectMeta;
import me.scarlet.undertailor.lua.LuaObjectValue;

/**
 * Metadata for {@link LuaObjectValue}s holding
 * {@link Environment} objects.
 */
public class LuaEnvironmentMeta implements LuaObjectMeta {

    public static LuaObjectValue<Environment> convert(LuaValue value) {
        return Lua.checkType(value, LuaEnvironmentMeta.class);
    }

    static Environment obj(Varargs args) {
        return convert(args.arg1()).getObject();
    }

    private LuaTable metatable;

    public LuaEnvironmentMeta() {
        this.metatable = new LuaTable();

        // environment:getName()
        set("getName", asFunction(vargs -> {
            return valueOf(obj(vargs).getName());
        }));

        // environment:getScheduler()
        set("getScheduler", asFunction(vargs -> {
            return orNil(obj(vargs).getScheduler());
        }));

        // environment:getOverworld()
        set("getOverworld", asFunction(vargs -> {
            return orNil(obj(vargs).getOverworld());
        }));

        // environment:getUI()
        set("getUI", asFunction(vargs -> {
            return orNil(obj(vargs).getUI());
        }));
    }

    @Override
    public boolean isPrimaryType() {
        return true;
    }

    @Override
    public Class<?> getTargetObjectClass() {
        return Environment.class;
    }

    @Override
    public LuaTable getMetatable() {
        return this.metatable;
    }

    @Override
    public String getTypeName() {
        return "environment";
    }
}
